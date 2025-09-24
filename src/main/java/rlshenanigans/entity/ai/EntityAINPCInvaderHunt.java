package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import rlshenanigans.entity.npc.EntityNPCInvader;

import java.util.List;

public class EntityAINPCInvaderHunt extends EntityAIBase {
    private final EntityNPCInvader entity;
    private final EntityPlayer target;
    
    public EntityAINPCInvaderHunt(EntityNPCInvader entity, EntityPlayer target) {
        this.entity = entity;
        this.target = target;
    }
    
    @Override
    public boolean shouldExecute() {
        return target != null && target.isEntityAlive();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return target != null && target.isEntityAlive();
    }
    
    @Override
    public void startExecuting() {
        entity.setAttackTarget(target);
    }
    
    @Override
    public void updateTask() {
        if (entity.ticksExisted % 20 != 0) return;
        
        List<EntityLivingBase> nearbyThreats = entity.world.getEntitiesWithinAABB(EntityLivingBase.class,
                entity.getEntityBoundingBox().grow(16.0D),
                e -> e != null && e.isEntityAlive() && (
                        (e instanceof EntityLiving && ((EntityLiving) e).getAttackTarget() == entity) || (e instanceof EntityPlayer))
        );
        
        if (!nearbyThreats.isEmpty()) {
            EntityLivingBase closest = null;
            double closestDistSq = Double.MAX_VALUE;
            
            for (EntityLivingBase threat : nearbyThreats) {
                double distSq = entity.getDistanceSq(threat);
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closest = threat;
                }
            }
            
            if (closest != null && closest != entity.getAttackTarget()) entity.setAttackTarget(closest);
        }
        
        else if (entity.getAttackTarget() != target) entity.setAttackTarget(target);
    }
    
    @Override
    public void resetTask() {
        entity.setAttackTarget(target);
    }
}