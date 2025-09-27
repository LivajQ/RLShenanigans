package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityDarkling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.creature.EntityAmalgalichTamed;
import rlshenanigans.mixin.vanilla.EntityLivingBaseAccessor;
import rlshenanigans.potion.PotionPookie;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class CombatAssistHandler {
    
    @SubscribeEvent
    public static void pookieAffectedAssist(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getTrueSource() instanceof EntityLivingBase)) return;
        EntityLivingBase target = event.getEntityLiving();
        EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
        
        EntityPlayer player;
        EntityLivingBase entity;
        if (attacker instanceof EntityPlayer && !(target instanceof EntityParasiteBase)) {
            player = (EntityPlayer) attacker;
            entity = target;
        }
        else if (!(attacker instanceof EntityParasiteBase) && target instanceof EntityPlayer) {
            player = (EntityPlayer) target;
            entity = attacker;
        }
        else return;
        if (player.world.isRemote) return;
        if (player.getActivePotionEffect(PotionPookie.INSTANCE) == null) return;
        
        List<EntityParasiteBase> parasites = player.world.getEntitiesWithinAABB(EntityParasiteBase.class,
                player.getEntityBoundingBox().grow(24.0D),
                p -> p != null && !p.isDead && p.getAttackTarget() != entity && !p.getEntityData().getBoolean("Tamed") &&
                        p.getEntityData().getBoolean("PookieAffected") && !p.isBeingRidden());
        
        for (EntityParasiteBase parasite : parasites) {
            parasite.setAttackTarget(entity);
        }
    }
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        
        if (event.getEntityLiving() instanceof EntityDarkling) {
            EntityDarkling darkling = (EntityDarkling) event.getEntityLiving();
            
            if (!darkling.isTamed() && darkling.getAttackTarget() instanceof EntityPlayer) {
                EntityPlayer targetPlayer = (EntityPlayer) darkling.getAttackTarget();
                
                List<EntityAmalgalichTamed> amalgalichList = darkling.world.getEntitiesWithinAABB(
                        EntityAmalgalichTamed.class,
                        targetPlayer.getEntityBoundingBox().grow(10.0D),
                        amalgalich -> amalgalich.isTamed() && amalgalich.getPlayerOwner() != null
                );
                
                for (EntityAmalgalichTamed amalgalich : amalgalichList) {
                    if (amalgalich.getPlayerOwner().getUniqueID().equals(targetPlayer.getUniqueID())) {
                        darkling.setPlayerOwner(targetPlayer);
                        darkling.onTamedByPlayer();
                        darkling.setAggressive(true);
                        break;
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void invokeTamedDrops(LivingDeathEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) return;
        
        EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
        NBTTagCompound data = entity.getEntityData();
        if (!data.getBoolean("MiscTamed") && !(entity instanceof EntityParasiteBase && data.getBoolean("Tamed"))) return;
        if (!data.hasUniqueId("OwnerUUID")) return;
        
        EntityPlayer owner = entity.world.getPlayerEntityByUUID(entity.getEntityData().getUniqueId("OwnerUUID"));
        EntityLivingBase target = event.getEntityLiving();
        if (owner == null || target == owner || isEntityTamedByPlayer(target, owner)) return;
        
        try {
            Field attackingPlayerField = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_70717_bb");
            attackingPlayerField.setAccessible(true);
            attackingPlayerField.set(target, owner);
            
            Field recentlyHitField = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_70718_bc");
            recentlyHitField.setAccessible(true);
            recentlyHitField.setInt(target, 100);
            
            DamageSource source = DamageSource.causePlayerDamage(owner);
            ((EntityLivingBaseAccessor) target).invokeDropLoot(true, 0, source);
        } catch (Exception ignored) {}
    }
    
    @SubscribeEvent
    public static void cancelTamedDamage(LivingAttackEvent event) {
        if (ForgeConfigHandler.misc.friendlyFire) return;
        Entity attacker = event.getSource().getTrueSource();
        if (!(attacker instanceof EntityPlayer)) return;
        if (isEntityTamedByPlayer(event.getEntityLiving(), (EntityPlayer) attacker)) event.setCanceled(true);
    }

    public static boolean isEntityTamed(EntityLivingBase entity) {
        NBTTagCompound data = entity.getEntityData();
        
        if (data.getBoolean("Tamed")) return true;
        if (data.hasUniqueId("OwnerUUID") && !data.getUniqueId("OwnerUUID").equals("")) return true;
        
        try {
            if (entity instanceof TameableCreatureEntity) {
                if (((TameableCreatureEntity) entity).isTamed()) return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        
        if (entity instanceof EntityTameable) {
            if (((EntityTameable) entity).isTamed()) return true;
        }
        
        if (entity instanceof IEntityOwnable) {
            if (((IEntityOwnable) entity).getOwnerId() != null) return true;
        }
        
        return false;
    }
    
    public static boolean isEntityTamedByPlayer(EntityLivingBase entity, EntityPlayer player) {
        NBTTagCompound entityData = entity.getEntityData();
        
        if (entityData.hasUniqueId("OwnerUUID") && entityData.getUniqueId("OwnerUUID").equals(player.getUniqueID())) return true;
        
        try {
            if (entity instanceof TameableCreatureEntity) {
                UUID ownerId = ((TameableCreatureEntity) entity).getOwnerId();
                if (ownerId != null && ownerId.equals(player.getUniqueID())) return true;
            }
        } catch (NoClassDefFoundError ignored) {}
        
        if (entity instanceof EntityTameable) {
            UUID ownerId = ((EntityTameable) entity).getOwnerId();
            if (ownerId != null && ownerId.equals(player.getUniqueID())) return true;
        }
        
        if (entity instanceof IEntityOwnable) {
            UUID ownerId = ((IEntityOwnable) entity).getOwnerId();
            if (ownerId != null && ownerId.equals(player.getUniqueID())) return true;
        }
        
        return false;
    }
}