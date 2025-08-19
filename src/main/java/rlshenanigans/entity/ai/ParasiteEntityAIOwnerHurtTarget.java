package rlshenanigans.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import rlshenanigans.handlers.ForgeConfigHandler;

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
        
        target = owner.getRevengeTarget();
        if (target == null) return false;
        
        if (target.getEntityData().hasUniqueId("OwnerUUID")) {
            UUID targetOwnerId = target.getEntityData().getUniqueId("OwnerUUID");
            if (targetOwnerId.equals(owner.getUniqueID())) return false;
        }
 
        if (target.equals(parasite)) return false;
        if (target.equals(getOwner())) return false;
        if (target instanceof EntityParasiteBase && !ForgeConfigHandler.parasite.parasiteOnParasiteViolence) return false;
        
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