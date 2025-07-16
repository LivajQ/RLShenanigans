package rlshenanigans.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import java.util.UUID;

public class ParasiteEntityAIOwnerHurtByTarget extends EntityAITarget
{
    private final EntityParasiteBase parasite;
    private EntityLivingBase attacker;
    
    public ParasiteEntityAIOwnerHurtByTarget(EntityParasiteBase parasite) {
        super(parasite, false);
        this.parasite = parasite;
    }
    
    @Override
    public boolean shouldExecute() {
        if (!parasite.getEntityData().getBoolean("Tamed")) return false;
        
        EntityLivingBase owner = getOwner();
        if (owner == null) return false;
        
        attacker = owner.getLastAttackedEntity();
        int revengeTimer = owner.getLastAttackedEntityTime();
        
        if (attacker != null && attacker.equals(parasite)) return false;
        if (attacker != null && attacker.equals(getOwner())) return false;
        if (attacker != null && attacker instanceof EntityParasiteBase) return false;
        
        
        return attacker != null && revengeTimer != parasite.getRevengeTimer();
    }
    
    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.attacker);
        super.startExecuting();
    }
    
    private EntityLivingBase getOwner() {
        UUID ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
        return ownerId != null ? parasite.world.getPlayerEntityByUUID(ownerId) : null;
    }
}