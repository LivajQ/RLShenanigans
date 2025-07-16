package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.Vec3d;

public class ParasiteEntityAIFollow extends EntityAIBase {
    private final EntityCreature follower;
    private final EntityPlayer targetPlayer;
    private final double speed;
    private final float stopDistance;
    private final float areaSize;
    private final PathNavigate navigator;
    private int timeToRecalcPath;
    private float oldWaterCost;
    
    public ParasiteEntityAIFollow(EntityCreature follower, EntityPlayer targetPlayer, double speed, float stopDistance, float areaSize) {
        this.follower = follower;
        this.targetPlayer = targetPlayer;
        this.speed = speed;
        this.stopDistance = stopDistance;
        this.areaSize = areaSize;
        this.navigator = follower.getNavigator();
        this.setMutexBits(3);
    }
    
    @Override
    public boolean shouldExecute() {
        if (targetPlayer == null || targetPlayer.isInvisible()) return false;
        double distanceSq = follower.getDistanceSq(targetPlayer);
        return distanceSq <= areaSize * areaSize && distanceSq > stopDistance * stopDistance;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return !navigator.noPath() && follower.getDistanceSq(targetPlayer) > stopDistance * stopDistance;
    }
    
    @Override
    public void startExecuting() {
        timeToRecalcPath = 0;
        oldWaterCost = follower.getPathPriority(PathNodeType.WATER);
        follower.setPathPriority(PathNodeType.WATER, 0.0F);
    }
    
    @Override
    public void resetTask() {
        navigator.clearPath();
        follower.setPathPriority(PathNodeType.WATER, oldWaterCost);
    }
    
    @Override
    public void updateTask() {
        if (!follower.getLeashed() && targetPlayer != null) {
            follower.getLookHelper().setLookPositionWithEntity(targetPlayer, 10.0F, follower.getVerticalFaceSpeed());
            
            if (--timeToRecalcPath <= 0) {
                timeToRecalcPath = 10;
                double distSq = follower.getDistanceSq(targetPlayer);
                
                if (distSq > stopDistance * stopDistance) {
                    navigator.tryMoveToEntityLiving(targetPlayer, speed);
                } else {
                    navigator.clearPath();
                    Vec3d lookVec = targetPlayer.getLookVec();
                    navigator.tryMoveToXYZ(
                            follower.posX - lookVec.x * 2,
                            follower.posY,
                            follower.posZ - lookVec.z * 2,
                            speed
                    );
                }
            }
        }
    }
}