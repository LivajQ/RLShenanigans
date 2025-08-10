package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

import java.util.UUID;

public class MiscEntityAIFollowOwner extends EntityAIBase
{
    private final EntityCreature mob;
    private EntityLivingBase owner;
    private final World world;
    private final double followSpeed;
    private final float minDist;
    private final float maxDist;
    
    public MiscEntityAIFollowOwner(EntityCreature mob, double speed, float minDist, float maxDist) {
        this.mob = mob;
        this.world = mob.world;
        this.followSpeed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setMutexBits(3);
    }
    
    @Override
    public boolean shouldExecute() {
        if (!mob.getEntityData().getBoolean("MiscTamed")) return false;
        
        UUID ownerId = mob.getEntityData().getUniqueId("OwnerUUID");
        if (ownerId == null) return false;
        
        if (mob.getAttackTarget() != null) return false;
        if (mob.getRevengeTarget() != null) return false;
        
        owner = world.getPlayerEntityByUUID(ownerId);
        if (owner == null) return false;
        
        return mob.getDistanceSq(owner) > (minDist * minDist);
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return !mob.getNavigator().noPath() && mob.getDistanceSq(owner) > (maxDist * maxDist);
    }
    
    @Override
    public void startExecuting() {
        mob.getNavigator().tryMoveToEntityLiving(owner, followSpeed);
    }
    
    @Override
    public void resetTask() {
        owner = null;
        mob.getNavigator().clearPath();
    }
    
    @Override
    public void updateTask() {
        double dx = mob.posX - owner.posX;
        double dz = mob.posZ - owner.posZ;
        double horizontalDistanceSq = dx * dx + dz * dz;
        
        if (horizontalDistanceSq > 576.0D){
            mob.setLocationAndAngles(owner.posX + 1.0, owner.posY, owner.posZ + 1.0, owner.rotationYaw, owner.rotationPitch);
        }
    }
}