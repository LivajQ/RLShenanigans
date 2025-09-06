package rlshenanigans.handlers;

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
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.ai.ParasiteEntityAIFollowOwner;
import rlshenanigans.entity.ai.ParasiteEntityAIOwnerHurtByTarget;
import rlshenanigans.entity.ai.ParasiteEntityAIOwnerHurtTarget;
import rlshenanigans.mixin.vanilla.EntityLivingBaseMixin;
import rlshenanigans.mixin.vanilla.EntityMixin;
import rlshenanigans.packet.OpenParasiteGuiPacket;
import rlshenanigans.packet.ParticlePulsePacket;
import rlshenanigans.util.ParasiteNames;
import rlshenanigans.util.SizeMultiplierHelper;
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
        
        if(!ForgeConfigHandler.parasite.parasiteTamingEnabled) return;
        if (!(target instanceof EntityParasiteBase)) return;  //not enough apparently? apples still consumed for normal mobs
        EntityParasiteBase parasite = (EntityParasiteBase) target;
        
       /*
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
        */
        
        if (world.isRemote || parasite.getEntityData().getBoolean("Tamed")) return;
        if (event.getHand() != EnumHand.MAIN_HAND) return;
        
        ResourceLocation rl = new ResourceLocation(ForgeConfigHandler.parasite.parasiteTamingItem);
        Item item = ForgeRegistries.ITEMS.getValue(rl);
        int meta = ForgeConfigHandler.parasite.parasiteTamingItemMetadata;
        
        if (stack.getItem() == item && stack.getMetadata() == meta)
        {
            if (!player.capabilities.isCreativeMode)
            {
                stack.shrink(1);
            }
            
            if (world.rand.nextInt(3) == 0)
            {
                setTags(parasite, player, world);
                
                TamedParasiteRegistry.track(parasite, player);
                
                RLSPacketHandler.INSTANCE.sendTo(new OpenParasiteGuiPacket(parasite.getEntityId()), (EntityPlayerMP) player);
                
                parasite.targetTasks.addTask(1, new ParasiteEntityAIOwnerHurtByTarget(parasite));
                parasite.targetTasks.addTask(2, new ParasiteEntityAIOwnerHurtTarget(parasite));
                parasite.tasks.taskEntries.removeIf(entry ->
                        entry.action.getClass().getSimpleName().toLowerCase().contains("follow"));
                parasite.tasks.addTask(6, new ParasiteEntityAIFollowOwner(parasite, 2.0D, 10.0F, 2.0F));
                
                world.playSound(null, parasite.getPosition(), SoundEvents.ENTITY_CAT_PURR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                
                RLSPacketHandler.INSTANCE.sendToAll(
                        new ParticlePulsePacket(parasite, EnumParticleTypes.HEART, 100, 30));
                
                float sizeMultiplier = parasite.getEntityData().getFloat("SizeMultiplier");
                float baseWidth = parasite.getEntityData().getFloat("BaseWidth");
                float baseHeight = parasite.getEntityData().getFloat("BaseHeight");
                if (player instanceof EntityPlayerMP) {
                    SizeMultiplierHelper.resizeEntity(parasite.getEntityWorld(), parasite.getEntityId(), (EntityPlayerMP) player,
                            sizeMultiplier,baseWidth, baseHeight, true);
                }
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
        if (ForgeConfigHandler.parasite.parasiteDeathResummonEnabled) return;
        if (parasite.getEntityData().getBoolean("SafeDespawn")) return;
        
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
        
        if(parasite.ticksExisted % 20 == 0) {
            List<EntityLiving> hostiles = parasite.world.getEntitiesWithinAABB(EntityLiving.class, parasite.getEntityBoundingBox().grow(24));
            for (EntityLiving mob : hostiles) {
                if (mob.getAttackTarget() == owner) {
                    if(mob instanceof EntityParasiteBase) continue;
                    if(parasite.getAttackTarget() != mob) parasite.setAttackTarget(mob);
                    break;
                }
            }
            
            if (!parasite.getEntityData().getBoolean("Waiting")) {
                double dx = parasite.posX - owner.posX;
                double dz = parasite.posZ - owner.posZ;
                double distanceSq = dx * dx + dz * dz;
                
                if (distanceSq > 32 * 32)
                {
                    for (EntityAITasks.EntityAITaskEntry entry : parasite.tasks.taskEntries)
                    {
                        entry.action.resetTask();
                    }
                    parasite.setAttackTarget(null);
                    parasite.setRevengeTarget(null);
                    parasite.getNavigator().clearPath();
                    parasite.tasks.onUpdateTasks();
                    parasite.setLocationAndAngles(owner.posX, owner.posY, owner.posZ, owner.rotationYaw, owner.rotationPitch);
                }
            }
        }
        
        parasite.tasks.taskEntries.removeIf(entry -> {
            String name = entry.action.getClass().getName().toLowerCase();
            return name.contains("flightatt")
                    || name.contains("blocklight")
                    || name.contains("parasitefollow");
        });
        
        parasite.targetTasks.taskEntries.removeIf(entry -> {
            String name = entry.action.getClass().getName().toLowerCase();
            return name.contains("nearestatt");
        });
        
        EntityLivingBase attackTarget = parasite.getAttackTarget();
        EntityLivingBase revengeTarget = parasite.getRevengeTarget();
        
        boolean attackOwnedByPlayer = false;
        boolean revengeOwnedByPlayer = false;
        
        if (attackTarget != null) {
            NBTTagCompound attackTargetData = attackTarget.getEntityData();
            if (attackTargetData.hasUniqueId("OwnerUUID")) {
                UUID ownerUUID = attackTargetData.getUniqueId("OwnerUUID");
                attackOwnedByPlayer = ownerUUID.equals(owner.getUniqueID());
            }
        }
        
        if (revengeTarget != null) {
            NBTTagCompound revengeTargetData = revengeTarget.getEntityData();
            if (revengeTargetData.hasUniqueId("OwnerUUID")) {
                UUID ownerUUID = revengeTargetData.getUniqueId("OwnerUUID");
                revengeOwnedByPlayer = ownerUUID.equals(owner.getUniqueID());
            }
        }
        
        boolean canAttackParasites = ForgeConfigHandler.parasite.parasiteOnParasiteViolence;
        
        if(attackTarget instanceof EntityParasiteBase && !canAttackParasites) parasite.setAttackTarget(null);
        if(revengeTarget instanceof EntityParasiteBase && !canAttackParasites) parasite.setRevengeTarget(null);
        if (attackTarget != null && (attackTarget.equals(owner) || attackOwnedByPlayer)) parasite.setAttackTarget(null);
        if (revengeTarget != null && (revengeTarget.equals(owner) || revengeOwnedByPlayer)) parasite.setRevengeTarget(null);
        
        
        if (!hasLoyaltyTasks(parasite))
        {
            parasite.targetTasks.addTask(1, new ParasiteEntityAIOwnerHurtByTarget(parasite));
            parasite.targetTasks.addTask(2, new ParasiteEntityAIOwnerHurtTarget(parasite));
        }
        
        if (!hasFollowTask(parasite) && !parasite.getEntityData().getBoolean("Waiting"))
        {
            parasite.tasks.addTask(3, new ParasiteEntityAIFollowOwner(parasite, 2.0D, 10.0F, 2.0F));
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
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        
        if (!(player instanceof EntityPlayerMP)) return;
        
        if (target instanceof EntityParasiteBase) {
            EntityParasiteBase parasite = (EntityParasiteBase) target;
            
            if (parasite.getEntityData().getBoolean("Tamed")) {
                float sizeMultiplier = parasite.getEntityData().getFloat("SizeMultiplier");
                float baseWidth = parasite.getEntityData().getFloat("BaseWidth");
                float baseHeight = parasite.getEntityData().getFloat("BaseHeight");
                SizeMultiplierHelper.resizeEntity(parasite.getEntityWorld(), parasite.getEntityId(), (EntityPlayerMP) player,
                        sizeMultiplier,baseWidth, baseHeight, false);
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event)
    {
        if (event.getEntityLiving().world.isRemote) return;
        
        EntityLivingBase target = event.getEntityLiving();
        Entity attacker = event.getSource().getTrueSource();
        if (!(attacker instanceof EntityParasiteBase)) return;
        
        NBTTagCompound attackerData = attacker.getEntityData();
        NBTTagCompound targetData = target.getEntityData();
        
        if (!attackerData.hasUniqueId("OwnerUUID")) return;
        
        if (target instanceof EntityPlayer) {
            if (attackerData.getUniqueId("OwnerUUID").equals(target.getUniqueID())) {
                event.setCanceled(true);
            }
        }
        
        else {
            if (!targetData.hasUniqueId("OwnerUUID")) return;
            if (attackerData.getUniqueId("OwnerUUID").equals(targetData.getUniqueId("OwnerUUID")))
                event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        Entity entity = event.getEntityBeingMounted();
        if(!(entity instanceof EntityParasiteBase) || !entity.getEntityData().getBoolean("Tamed")) return;
        if (event.isDismounting() && entity.isInWater() && !event.getEntityMounting().isSneaking()) {
            event.setCanceled(true);
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
    
    public static void setTags(EntityParasiteBase parasite, EntityPlayer player, World world) {
        parasite.getEntityData().setBoolean("Tamed", true);
        parasite.getEntityData().setBoolean("Waiting", false);
        parasite.getEntityData().setUniqueId("OwnerUUID", player.getUniqueID());
        parasite.setAttackTarget(null);
        String chosenName = ParasiteNames.getRandomName(world.rand);
        parasite.setCustomNameTag(chosenName);
        parasite.setAlwaysRenderNameTag(true);
        parasite.getEntityData().setBoolean("PersistenceRequired", true);
        parasite.enablePersistence();
        parasite.getEntityData().setBoolean("parasitedespawn", false);
        parasite.getEntityData().setFloat("BaseWidth", ((EntityMixin) parasite).getWidth());
        parasite.getEntityData().setFloat("BaseHeight", ((EntityMixin) parasite).getHeight());
        parasite.getEntityData().setFloat("SizeMultiplier", 1.0F);
    }
}