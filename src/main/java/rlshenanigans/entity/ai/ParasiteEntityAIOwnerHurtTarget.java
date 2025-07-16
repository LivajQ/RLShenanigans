package rlshenanigans.entity.ai;


import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import java.util.UUID;

public class ParasiteEntityAIOwnerHurtTarget extends EntityAITarget
{
    private final EntityParasiteBase parasite;
    private EntityLivingBase target;
    
    public ParasiteEntityAIOwnerHurtTarget(EntityParasiteBase parasite) {
        super(parasite, false);
        this.parasite = parasite;
    }
    
    @Override
    public boolean shouldExecute() {
        if (!parasite.getEntityData().getBoolean("Tamed")) return false;
        
        EntityLivingBase owner = getOwner();
        if (owner == null) return false;
        
        EntityLivingBase attacker = owner.getRevengeTarget();
        if (attacker == null) return false;
 
        if (attacker != null && attacker.equals(parasite)) return false;
        if (attacker != null && attacker.equals(getOwner())) return false;
        if (attacker != null && attacker instanceof EntityParasiteBase) return false;
        
        this.target = attacker;
        return true;
    }
    
    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.target);
        super.startExecuting();
    }
    
    private EntityLivingBase getOwner() {
        UUID ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
        return ownerId != null ? parasite.world.getPlayerEntityByUUID(ownerId) : null;
    }
}