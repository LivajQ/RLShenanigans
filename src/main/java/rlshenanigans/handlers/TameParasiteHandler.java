package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.ai.ParasiteEntityAIFollowOwner;
import rlshenanigans.entity.ai.ParasiteEntityAIOwnerHurtByTarget;
import rlshenanigans.entity.ai.ParasiteEntityAIOwnerHurtTarget;
import rlshenanigans.mixin.vanilla.EntityLivingBaseMixin;
import rlshenanigans.packet.OpenParasiteGuiPacket;
import rlshenanigans.packet.ParticlePulsePacket;
import rlshenanigans.util.ParasiteNames;
import rlshenanigans.util.TamedParasiteRegistry;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class TameParasiteHandler
{
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        World world = player.world;
        
        if (!(target instanceof EntityParasiteBase)) return;  //not enough apparently? apples still consumed for normal mobs
        EntityParasiteBase parasite = (EntityParasiteBase) target;
        
        if (stack.getItem() == Items.GOLDEN_APPLE && stack.getMetadata() == 0)
        {
            if (!player.capabilities.isCreativeMode)
            {
                stack.shrink(1);
            }
            
            RLSPacketHandler.INSTANCE.sendToAll(
                    new ParticlePulsePacket(parasite, EnumParticleTypes.HEART, 100, 30)
            );
            
            event.setCanceled(true);
        }
        
        if (world.isRemote || parasite.getEntityData().getBoolean("Tamed")) return;
        
        if (stack.getItem() == Items.GOLDEN_APPLE && stack.getMetadata() == 1)
        {
            if (!player.capabilities.isCreativeMode)
            {
                stack.shrink(1);
            }
            
            if (world.rand.nextInt(3) == 0)
            {
                parasite.getEntityData().setBoolean("Tamed", true);
                parasite.getEntityData().setBoolean("Waiting", false);
                parasite.getEntityData().setUniqueId("OwnerUUID", player.getUniqueID());
                parasite.setAttackTarget(null);
                String chosenName = ParasiteNames.getRandomName(world.rand);
                parasite.setCustomNameTag(chosenName);
                parasite.setAlwaysRenderNameTag(true);
                parasite.getEntityData().setBoolean("PersistenceRequired", true);
                ((EntityLiving)target).enablePersistence();
                parasite.getEntityData().setBoolean("parasitedespawn", false);
                parasite.getEntityData().setBoolean("ParasiteDespawn", false);
                parasite.getEntityData().setBoolean("AllowConverting", false);
                
                TamedParasiteRegistry.track(parasite, player);
 
                if (!world.isRemote) {
                    RLSPacketHandler.INSTANCE.sendTo(new OpenParasiteGuiPacket(parasite.getEntityId()), (EntityPlayerMP) player);
                }
                
                parasite.targetTasks.addTask(1, new ParasiteEntityAIOwnerHurtByTarget(parasite));
                parasite.targetTasks.addTask(2, new ParasiteEntityAIOwnerHurtTarget(parasite));
                parasite.tasks.taskEntries.removeIf(entry ->
                        entry.action.getClass().getSimpleName().toLowerCase().contains("follow"));
                parasite.tasks.addTask(6, new ParasiteEntityAIFollowOwner(parasite, 2.0D, 10.0F, 2.0F));
                
                world.playSound(null, parasite.getPosition(), SoundEvents.ENTITY_CAT_PURR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                
                RLSPacketHandler.INSTANCE.sendToAll(
                        new ParticlePulsePacket(parasite, EnumParticleTypes.HEART, 100, 30)
                );
            }
            else
            {
                RLSPacketHandler.INSTANCE.sendToAll(
                        new ParticlePulsePacket(parasite, EnumParticleTypes.SMOKE_NORMAL, 20, 15)
                );
            }
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        
        EntityLivingBase target = event.getEntityLiving();
        DamageSource source = event.getSource();
        Entity killer = source.getTrueSource();
        
        if (killer instanceof EntityParasiteBase) {
            EntityParasiteBase parasite = (EntityParasiteBase) killer;
            NBTTagCompound data = parasite.getEntityData();
            
            if (data.hasUniqueId("OwnerUUID")) {
                UUID ownerId = data.getUniqueId("OwnerUUID");
                EntityPlayerMP owner = FMLCommonHandler.instance()
                        .getMinecraftServerInstance()
                        .getPlayerList()
                        .getPlayerByUUID(ownerId);
                
                if (owner != null) {
                    target.setLastAttackedEntity(owner);
                    target.getCombatTracker().trackDamage(DamageSource.causePlayerDamage(owner), target.getHealth(), 1.0F);
                    ((EntityLivingBaseMixin) target).invokeDropLoot(true, 0, DamageSource.GENERIC);
                }
            }
        }
        
        if (!(target instanceof EntityParasiteBase)) return;
        
        EntityParasiteBase parasite = (EntityParasiteBase) event.getEntityLiving();
        if (!parasite.getEntityData().getBoolean("Tamed")) return;
        
        TamedParasiteRegistry.untrack(parasite.getUniqueID());
        
        UUID ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
        EntityPlayer owner = parasite.world.getPlayerEntityByUUID(ownerId);
        if (owner != null && !parasite.world.isRemote) {
            owner.sendMessage(new TextComponentString("§fYour §dpookie §fhas §c§lDIED §f(╥﹏╥)"));
        }
    }
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityParasiteBase)) return;
        
        EntityParasiteBase parasite = (EntityParasiteBase) event.getEntityLiving();
        if (parasite.getEntityData().getBoolean("Tamed") && !parasite.getEntityData().getBoolean("PersistenceRequired"))
            parasite.getEntityData().setBoolean("PersistenceRequired", true);
        
        if (!parasite.getEntityData().getBoolean("Tamed")) return;
        
        removeIfActive(parasite, SharedMonsterAttributes.MAX_HEALTH, UUID.fromString("554f3929-4194-4ae5-a4da-4b528a89ca32"));
        removeIfActive(parasite, SharedMonsterAttributes.ATTACK_DAMAGE, UUID.fromString("554f3929-4196-4ae5-a4da-4b528a89ca32"));
        removeIfActive(parasite, SharedMonsterAttributes.ARMOR, UUID.fromString("554f3929-4195-4ae5-a4da-4b528a89ca32"));
        removeIfActive(parasite, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, UUID.fromString("554f3929-4197-4ae5-a4da-4b528a89ca32"));
        
        UUID ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
        if (ownerId == null) return;
        
        boolean stillTracked = TamedParasiteRegistry.getOwnedBy(ownerId).stream()
                .anyMatch(info -> info.mobUUID.equals(parasite.getUniqueID()));
        
        if (!stillTracked) {
            parasite.setDead();
        }
        
        EntityPlayer owner = parasite.world.getPlayerEntityByUUID(ownerId);
        if (owner == null) return;
        
        parasite.targetTasks.taskEntries.removeIf(entry ->
        {
            String name = entry.action.getClass().getSimpleName().toLowerCase();
            return name.contains("near");
        });
        parasite.targetTasks.taskEntries.removeIf(entry ->
        {
            String name = entry.action.getClass().getSimpleName().toLowerCase();
            return name.contains("flightatt");
        });
        
        EntityLivingBase attackTarget = parasite.getAttackTarget();
        EntityLivingBase revengeTarget = parasite.getRevengeTarget();
        
        if (attackTarget != null && (attackTarget.equals(owner)
                || attackTarget instanceof EntityParasiteBase)) {
            parasite.setAttackTarget(null);
        }
        if (revengeTarget != null && (revengeTarget.equals(owner)
                || revengeTarget instanceof EntityParasiteBase)) {
            parasite.setRevengeTarget(null);
        }
        
        if (!hasLoyaltyTasks(parasite))
        {
            parasite.targetTasks.addTask(1, new ParasiteEntityAIOwnerHurtByTarget(parasite));
            parasite.targetTasks.addTask(2, new ParasiteEntityAIOwnerHurtTarget(parasite));
        }
        
        if (!hasFollowTask(parasite) && !parasite.getEntityData().getBoolean("Waiting"))
        {
            parasite.tasks.addTask(6, new ParasiteEntityAIFollowOwner(parasite, 2.0D, 10.0F, 2.0F));
        }
        
        if (parasite.getRevengeTarget() != null && parasite.getRevengeTarget().equals(parasite)) {
            parasite.setRevengeTarget(null);
        }
        if (parasite.getAttackTarget() instanceof EntityParasiteBase) {
            EntityParasiteBase target = (EntityParasiteBase) parasite.getAttackTarget();
            if (target.getEntityData().getBoolean("Tamed")) {
                parasite.setAttackTarget(null);
            }
        }
        if (parasite.getRevengeTarget() instanceof EntityParasiteBase) {
            EntityParasiteBase target = (EntityParasiteBase) parasite.getRevengeTarget();
            if (target.getEntityData().getBoolean("Tamed")) {
                parasite.setRevengeTarget(null);
            }
        }
    }
    
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) { //curse you charge attacks
        Entity entity = event.getEntity();
        
        if (entity instanceof EntityDamage) {
            EntityDamage damage = (EntityDamage) entity;
            
            List<EntityParasiteBase> nearbyParasites = entity.world.getEntitiesWithinAABB(
                    EntityParasiteBase.class,
                    entity.getEntityBoundingBox().grow(2.0)
            );
            
            for (EntityParasiteBase parasite : nearbyParasites) {
                UUID ownerUUID = parasite.getEntityData().getUniqueId("OwnerUUID");
                
                if (ownerUUID != null && parasite.isBeingRidden()) {
                    Entity rider = parasite.getPassengers().get(0);
                    if (rider instanceof EntityPlayer && ((EntityPlayer) rider).getUniqueID().equals(ownerUUID)) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        Entity attacker = event.getSource().getTrueSource();
        
        if (attacker instanceof EntityParasiteBase && target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            EntityParasiteBase parasite = (EntityParasiteBase) attacker;
            NBTTagCompound data = parasite.getEntityData();
            
            if (data.hasUniqueId("OwnerUUID") && data.getUniqueId("OwnerUUID").equals(player.getUniqueID())) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean hasLoyaltyTasks(EntityParasiteBase parasite) {
        for (EntityAITasks.EntityAITaskEntry entry : parasite.targetTasks.taskEntries)
        {
            if (entry.action instanceof ParasiteEntityAIOwnerHurtTarget ||
                    entry.action instanceof ParasiteEntityAIOwnerHurtByTarget) return true;
        }
        return false;
    }
    
    private static boolean hasFollowTask(EntityParasiteBase parasite) {
        for (EntityAITasks.EntityAITaskEntry entry : parasite.tasks.taskEntries) {
            if (entry.action instanceof ParasiteEntityAIFollowOwner) return true;
        }
        return false;
    }
    
    private static void removeIfActive(EntityLivingBase entity, IAttribute attribute, UUID uuid) {
        IAttributeInstance instance = entity.getEntityAttribute(attribute);
        if (instance != null && instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }
}