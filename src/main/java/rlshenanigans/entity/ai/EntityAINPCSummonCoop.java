package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import rlshenanigans.entity.npc.EntityNPCSummon;
import rlshenanigans.handlers.CombatAssistHandler;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.NPCPhantomSyncFadePacket;

import java.util.List;

public class EntityAINPCSummonCoop extends EntityAIBase {
    protected final EntityNPCSummon entity;
    protected final EntityPlayer player;
    protected final double followSpeed;
    
    public EntityAINPCSummonCoop(EntityNPCSummon entity, EntityPlayer player, double followSpeed) {
        this.entity = entity;
        this.player = player;
        this.followSpeed = followSpeed;
    }
    
    @Override
    public boolean shouldExecute() {
        return player != null && !player.isDead && entity.isEntityAlive();
    }
    
    @Override
    public void updateTask() {
        if (entity.getDistanceSq(player) > 25.0D) entity.getNavigator().tryMoveToEntityLiving(player, followSpeed);
        
        if (!entity.getIsDespawning() && entity.getDistanceSq(player) > 32 * 32) {
            entity.spawnInRadius(entity.world, player.getPosition(), 2, 5, true);
            entity.setPhantomFadeTime(20);
            RLSPacketHandler.INSTANCE.sendToAllTracking(new NPCPhantomSyncFadePacket(entity), entity);
        }
        
        EntityLivingBase currentTarget = entity.getAttackTarget();
        
        if (currentTarget == player) entity.setAttackTarget(null);
        
        if (currentTarget == null || !currentTarget.isEntityAlive()) {
            EntityLivingBase playerTarget = player.getRevengeTarget();
            if (playerTarget != null
                    && playerTarget.isEntityAlive()
                    && !CombatAssistHandler.isEntityTamedByPlayer(playerTarget, player)) entity.setAttackTarget(playerTarget);
            
            EntityLivingBase revengeTarget = entity.getRevengeTarget();
            if (revengeTarget != null
                    && revengeTarget.isEntityAlive()
                    && !CombatAssistHandler.isEntityTamedByPlayer(revengeTarget, player)) entity.setAttackTarget(revengeTarget);

            EntityLivingBase playerAttacked = player.getLastAttackedEntity();
            if (playerAttacked != null
                    && playerAttacked.isEntityAlive()
                    && !CombatAssistHandler.isEntityTamedByPlayer(playerAttacked, player)) entity.setAttackTarget(playerAttacked);
        }
        
        if (entity.ticksExisted % 20 == 0) {
            if (entity.getDistanceSq(player) > 32.0D * 32.0D) {
                entity.setPositionAndUpdate(player.posX, player.posY, player.posZ);
                entity.setAttackTarget(null);
                entity.getNavigator().clearPath();
                return;
            }
            
           if (entity.getAttackTarget() == null || !entity.getAttackTarget().isEntityAlive()) {
               List<EntityLiving> nearby = entity.world.getEntitiesWithinAABB(EntityLiving.class,
                       entity.getEntityBoundingBox().grow(16.0F),
                       e -> e != entity && e.isEntityAlive() && (e.getAttackTarget() == player || e.getAttackTarget() == entity)
               );
               
               if (!nearby.isEmpty()) entity.setAttackTarget(nearby.get(0));
           }
        }
    }
}