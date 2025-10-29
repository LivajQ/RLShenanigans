package rlshenanigans.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

import java.util.UUID;

public class ParasiteEntityAIFollowOwner extends EntityAIBase {
    
    private final EntityParasiteBase parasite;
    private EntityLivingBase owner;
    private final World world;
    private final double followSpeed;
    private final float minDist;
    private final float maxDist;
    private boolean tamed;
    private UUID ownerId;
    
    public ParasiteEntityAIFollowOwner(EntityParasiteBase parasite, double speed, float minDist, float maxDist) {
        this.parasite = parasite;
        this.world = parasite.world;
        this.followSpeed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.tamed = parasite.getEntityData().getBoolean("Tamed");
        this.ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
        this.setMutexBits(3);
    }
    
    @Override
    public boolean shouldExecute() {
        if (!tamed) {
            if (parasite.ticksExisted % 100 == 0) {
                tamed = parasite.getEntityData().getBoolean("Tamed");
                ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
            }
            return false;
        }
        
        if (ownerId == null) return false;
        
        owner = world.getPlayerEntityByUUID(ownerId);
        if (owner == null) return false;
        
        return parasite.getDistanceSq(owner) > (minDist * minDist);
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return !parasite.getNavigator().noPath() && parasite.getDistanceSq(owner) > (maxDist * maxDist);
    }
    
    @Override
    public void startExecuting() {
        parasite.getNavigator().tryMoveToEntityLiving(owner, followSpeed);
    }
    
    @Override
    public void resetTask() {
        owner = null;
        parasite.getNavigator().clearPath();
    }
    
    @Override
    public void updateTask() {
        double dx = parasite.posX - owner.posX;
        double dz = parasite.posZ - owner.posZ;
        double horizontalDistanceSq = dx * dx + dz * dz;
        
        if (horizontalDistanceSq > 576.0D){
            parasite.setLocationAndAngles(owner.posX, owner.posY, owner.posZ, owner.rotationYaw, owner.rotationPitch);
        }
    }
}