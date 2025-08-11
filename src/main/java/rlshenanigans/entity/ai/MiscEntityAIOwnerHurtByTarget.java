package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import java.util.UUID;

public class MiscEntityAIOwnerHurtByTarget extends EntityAITarget
{
    private final EntityCreature mob;
    private EntityLivingBase attacker;
    
    public MiscEntityAIOwnerHurtByTarget(EntityCreature mob) {
        super(mob, false);
        this.mob = mob;
        this.setMutexBits(1);
    }
    
    @Override
    public boolean shouldExecute() {
        if (!mob.getEntityData().getBoolean("MiscTamed")) return false;
        
        EntityLivingBase owner = getOwner();
        if (owner == null) return false;
        
        attacker = owner.getLastAttackedEntity();
        if(attacker == null) return false;
        int revengeTimer = owner.getLastAttackedEntityTime();
        
        if (attacker.getEntityData().hasUniqueId("OwnerUUID")) {
            UUID attackerOwnerId = attacker.getEntityData().getUniqueId("OwnerUUID");
            if (attackerOwnerId.equals(getOwner().getUniqueID())) return false;
        }
        
        if (attacker.equals(mob)) return false;
        if (attacker.equals(getOwner())) return false;
        
        return revengeTimer != mob.getRevengeTimer();
    }
    
    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.attacker);
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