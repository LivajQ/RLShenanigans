package rlshenanigans.handlers;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAITasks;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.ai.*;
import rlshenanigans.mixin.vanilla.EntityLivingBaseMixin;
import rlshenanigans.packet.ParticlePulsePacket;
import rlshenanigans.util.TameableMiscWhitelist;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class TameMiscHandler {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        World world = player.world;
        
        if (!ForgeConfigHandler.misc.miscTamingEnabled) return;
        if (!(target instanceof EntityLiving)) return;
        
        EntityLiving mob = (EntityLiving) target;
        
        if (world.isRemote) return;
        if (event.getHand() != EnumHand.MAIN_HAND) return;
        
        if (mob.getEntityData().getBoolean("MiscTamed") && mob instanceof EntityCreature) {
            EntityCreature creature = (EntityCreature) mob;
            UUID ownerId = creature.getEntityData().getUniqueId("OwnerUUID");
            
            if (player.isSneaking()) {
                if (ownerId != null && ownerId.equals(player.getUniqueID())) {
                    String name = creature.getDisplayName().getFormattedText();
                    boolean isWaiting = creature.getEntityData().getBoolean("Waiting");
                    
                    if (isWaiting) {
                        creature.tasks.addTask(1, new MiscEntityAIFollowOwner(creature, 2.0D, 10.0F, 2.0F));
                        player.sendStatusMessage(new TextComponentString(name + " is now following"), true);
                    }
                    
                    else {
                        creature.getNavigator().clearPath();
                        creature.tasks.taskEntries.removeIf(entry ->
                                entry.action instanceof MiscEntityAIFollowOwner);
                        player.sendStatusMessage(new TextComponentString(name + " is now roaming"), true);
                    }
                    creature.getEntityData().setBoolean("Waiting", !isWaiting);
                    event.setCanceled(true);
                }
            }
            else player.startRiding(creature, true);
        }
        
        if(mob.getEntityData().getBoolean("MiscTamed")) return;
        
        ResourceLocation mobRL = EntityList.getKey(mob);
        if (mobRL == null) return;
        
        for (TameableMiscWhitelist.Entry entry : TameableMiscWhitelist.getEntries()) {
            if (!entry.mobId.equals(mobRL.toString())) continue;
            
            ResourceLocation itemRL = new ResourceLocation(entry.itemId);
            Item whitelistItem = ForgeRegistries.ITEMS.getValue(itemRL);
            if (whitelistItem == null) continue;
            
            if (stack.getItem() == whitelistItem && stack.getMetadata() == entry.metadata) {
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                
                if (world.rand.nextInt(3) == 0) {
                    mob.getEntityData().setBoolean("MiscTamed", true);
                    mob.getEntityData().setBoolean("Waiting", false);
                    mob.getEntityData().setUniqueId("OwnerUUID", player.getUniqueID());
                    mob.setAttackTarget(null);
                    mob.enablePersistence();
                    mob.getEntityData().setBoolean("PersistenceRequired", true);
                    
                    if (mob instanceof EntityCreature) {
                        EntityCreature creature = (EntityCreature) mob;
                        creature.targetTasks.addTask(1, new MiscEntityAIOwnerHurtByTarget(creature));
                        creature.targetTasks.addTask(1, new MiscEntityAIOwnerHurtTarget(creature));
                        
                        creature.tasks.taskEntries.removeIf(taskEntry ->
                                taskEntry.action.getClass().getSimpleName().toLowerCase().contains("follow"));
                        creature.tasks.addTask(1, new MiscEntityAIFollowOwner(creature, 2.0D, 10.0F, 2.0F));
                        
                    }
                    RLSPacketHandler.INSTANCE.sendToAll(
                            new ParticlePulsePacket(mob, EnumParticleTypes.HEART, 100, 30));
                }
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onSetTarget(LivingSetAttackTargetEvent event) {
        EntityLivingBase mob = event.getEntityLiving();
        EntityLivingBase target = event.getTarget();
        
        if (!mob.getEntityData().getBoolean("MiscTamed")) return;
        
        if (target != null) {
            UUID ownerUUID = mob.getEntityData().getUniqueId("OwnerUUID");
            if (ownerUUID != null && ownerUUID.equals(target.getUniqueID())) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        
        EntityLivingBase target = event.getEntityLiving();
        DamageSource source = event.getSource();
        Entity killer = source.getTrueSource();
        
        if (killer instanceof EntityLiving) {
            EntityLiving mob = (EntityLiving) killer;
            NBTTagCompound data = mob.getEntityData();
            
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
    }
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLiving)) return;
        
        EntityLiving mob = (EntityLiving) event.getEntityLiving();
        
        if (!mob.getEntityData().getBoolean("MiscTamed")) return;
        
        UUID ownerId = mob.getEntityData().getUniqueId("OwnerUUID");
        if (ownerId == null) return;
        
        EntityPlayer owner = mob.world.getPlayerEntityByUUID(ownerId);
        if (owner == null) return;
        
        if(mob.ticksExisted % 20 == 0) {
            List<EntityMob> hostiles = mob.world.getEntitiesWithinAABB(EntityMob.class, mob.getEntityBoundingBox().grow(24));
            for (EntityMob hostileMob : hostiles) {
                if (hostileMob.getAttackTarget() == owner) {
                    if (mob.getAttackTarget() != hostileMob) mob.setAttackTarget(hostileMob);
                    break;
                }
            }
            
            if (!mob.getEntityData().getBoolean("Waiting")) {
                double dx = mob.posX - owner.posX;
                double dz = mob.posZ - owner.posZ;
                double distanceSq = dx * dx + dz * dz;
                
                if (distanceSq > 32 * 32) {
                    for (EntityAITasks.EntityAITaskEntry entry : mob.tasks.taskEntries) {
                        entry.action.resetTask();
                    }
                    mob.setAttackTarget(null);
                    mob.setRevengeTarget(null);
                    mob.getNavigator().clearPath();
                    mob.tasks.onUpdateTasks();
                    mob.setLocationAndAngles(owner.posX, owner.posY, owner.posZ, owner.rotationYaw, owner.rotationPitch);
                }
            }
        }
        
        mob.targetTasks.taskEntries.removeIf(entry -> {
            String name = entry.action.getClass().getSimpleName().toLowerCase();
            return name.contains("near")
                    || name.contains("flightatt")
                    || name.contains("blocklight")
                    || name.contains("stareatt")
                    || name.contains("watchclo")
                    || name.contains("fleesun");
        });
        
        EntityLivingBase attackTarget = mob.getAttackTarget();
        EntityLivingBase revengeTarget = mob.getRevengeTarget();
        
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
        
        if (attackTarget != null && (attackTarget.equals(owner) || attackOwnedByPlayer)) {
            mob.setAttackTarget(null);
        }
        
        if (revengeTarget != null && (revengeTarget.equals(owner) || revengeOwnedByPlayer)) {
            mob.setRevengeTarget(null);
        }
        
        
        if (!hasLoyaltyTasks(mob) && mob instanceof EntityCreature)
        {
            EntityCreature creature = (EntityCreature) mob;
            creature.targetTasks.addTask(1, new MiscEntityAIOwnerHurtByTarget(creature));
            creature.targetTasks.addTask(1, new MiscEntityAIOwnerHurtTarget(creature));
        }
        
        if (!hasFollowTask(mob) && !mob.getEntityData().getBoolean("Waiting") && mob instanceof EntityCreature)
        {
            EntityCreature creature = (EntityCreature) mob;
            creature.tasks.addTask(1, new MiscEntityAIFollowOwner(creature, 2.0D, 10.0F, 2.0F));
        }
        
        if (mob.getRevengeTarget() != null && mob.getRevengeTarget().equals(mob)) {
            mob.setRevengeTarget(null);
        }
        
        if (mob.getAttackTarget() instanceof EntityLiving) {
            EntityLiving target = (EntityLiving) mob.getAttackTarget();
            if (target.getEntityData().getBoolean("MiscTamed")) {
                mob.setAttackTarget(null);
            }
        }
        if (mob.getRevengeTarget() instanceof EntityLiving) {
            EntityLiving target = (EntityLiving) mob.getRevengeTarget();
            if (target.getEntityData().getBoolean("MiscTamed")) {
                mob.setRevengeTarget(null);
            }
        }
        
        Potion cothEffect = ForgeRegistries.POTIONS.getValue(new ResourceLocation("srparasites", "coth"));
        if (cothEffect != null && mob.isPotionActive(cothEffect)) {
            mob.removePotionEffect(cothEffect);
        }
    }
    
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        Entity attacker = event.getSource().getTrueSource();
        if(attacker == null) return;
        
        NBTTagCompound data = attacker.getEntityData();
        NBTTagCompound targetData = target.getEntityData();
        
        if (!data.hasUniqueId("OwnerUUID")) return;
        if (!data.getBoolean("MiscTamed")) return;
        
        if(target instanceof EntityPlayer) {
            if (data.getUniqueId("OwnerUUID").equals(target.getUniqueID())) event.setCanceled(true);
        }
        
        else {
            if (!targetData.hasUniqueId("OwnerUUID")) return;
            if (data.getUniqueId("OwnerUUID").equals(targetData.getUniqueId("OwnerUUID"))) event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        Entity entity = event.getEntityBeingMounted();
        if (!entity.getEntityData().getBoolean("MiscTamed")) return;
        if (event.isDismounting() && entity.isInWater() && !event.getEntityMounting().isSneaking()) {
            event.setCanceled(true);
        }
    }

    private static boolean hasLoyaltyTasks(EntityLiving mob) {
        for (EntityAITasks.EntityAITaskEntry entry : mob.targetTasks.taskEntries)
        {
            if (entry.action instanceof MiscEntityAIOwnerHurtTarget ||
                    entry.action instanceof MiscEntityAIOwnerHurtByTarget) return true;
        }
        return false;
    }
    
    private static boolean hasFollowTask(EntityLiving mob) {
        for (EntityAITasks.EntityAITaskEntry entry : mob.tasks.taskEntries) {
            if (entry.action instanceof MiscEntityAIFollowOwner) return true;
        }
        return false;
    }
}