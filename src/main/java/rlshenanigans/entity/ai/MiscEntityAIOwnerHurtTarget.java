package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import java.util.UUID;

public class MiscEntityAIOwnerHurtTarget extends EntityAITarget
{
    private final EntityCreature mob;
    private EntityLivingBase target;
    
    public MiscEntityAIOwnerHurtTarget(EntityCreature mob) {
        super(mob, false);
        this.mob = mob;
        this.setMutexBits(1);
    }
    
    @Override
    public boolean shouldExecute() {
        if (!mob.getEntityData().getBoolean("MiscTamed")) return false;
        
        EntityLivingBase owner = getOwner();
        if (owner == null) return false;
        
        target = owner.getRevengeTarget();
        if (target == null) return false;
        
        UUID targetOwnerId = target.getEntityData().getUniqueId("OwnerUUID");
        if(targetOwnerId == null) return false;
        
        if (target.equals(mob)) return false;
        if (target.equals(getOwner())) return false;
        if (targetOwnerId.equals(getOwner().getUniqueID())) return false;
        
        return true;
    }
    
    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.target);
        super.startExecuting();
    }
    
    @Override
    public void resetTask() {
        this.taskOwner.setAttackTarget(null);
        this.target = null;
    }
    
    private EntityLivingBase getOwner() {
        UUID ownerId = mob.getEntityData().getUniqueId("OwnerUUID");
        return ownerId != null ? mob.world.getPlayerEntityByUUID(ownerId) : null;
    }
}