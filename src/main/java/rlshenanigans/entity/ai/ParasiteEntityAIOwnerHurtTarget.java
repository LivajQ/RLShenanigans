package rlshenanigans.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.world.World;
import rlshenanigans.handlers.ForgeConfigHandler;

import java.util.UUID;

public class ParasiteEntityAIOwnerHurtTarget extends EntityAITarget
{
    private final EntityParasiteBase parasite;
    private EntityLivingBase target;
    private final World world;
    private boolean tamed;
    private UUID ownerId;
    
    public ParasiteEntityAIOwnerHurtTarget(EntityParasiteBase parasite) {
        super(parasite, false);
        this.parasite = parasite;
        this.world = parasite.world;
        this.tamed = parasite.getEntityData().getBoolean("Tamed");
        this.ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
        this.setMutexBits(1);
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
        
        EntityLivingBase owner = world.getPlayerEntityByUUID(ownerId);
        if (owner == null) return false;
        
        target = owner.getRevengeTarget();
        if (target == null) return false;
        
        if (target.getEntityData().hasUniqueId("OwnerUUID")) {
            UUID targetOwnerId = target.getEntityData().getUniqueId("OwnerUUID");
            if (targetOwnerId.equals(owner.getUniqueID())) return false;
        }
 
        if (target.equals(parasite)) return false;
        if (target.equals(owner)) return false;
        if (target instanceof EntityParasiteBase && !ForgeConfigHandler.parasite.parasiteOnParasiteViolence) return false;
        
        return true;
    }
    
    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.target);
        super.startExecuting();
    }
}