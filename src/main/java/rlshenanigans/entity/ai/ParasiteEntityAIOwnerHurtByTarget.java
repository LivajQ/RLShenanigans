package rlshenanigans.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.world.World;
import rlshenanigans.handlers.ForgeConfigHandler;

import java.util.UUID;

public class ParasiteEntityAIOwnerHurtByTarget extends EntityAITarget {
    
    private final EntityParasiteBase parasite;
    private EntityLivingBase attacker;
    private final World world;
    private boolean tamed;
    private UUID ownerId;
    
    public ParasiteEntityAIOwnerHurtByTarget(EntityParasiteBase parasite) {
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
        
        attacker = owner.getLastAttackedEntity();
        if(attacker == null) return false;
        int revengeTimer = owner.getLastAttackedEntityTime();
        
        if (attacker.getEntityData().hasUniqueId("OwnerUUID")) {
            UUID attackerOwnerId = attacker.getEntityData().getUniqueId("OwnerUUID");
            if (attackerOwnerId.equals(owner.getUniqueID())) return false;
        }
        
        if (attacker.equals(parasite)) return false;
        if (attacker.equals(owner)) return false;
        if (attacker instanceof EntityParasiteBase && !ForgeConfigHandler.parasite.parasiteOnParasiteViolence) return false;
        
        return revengeTimer != parasite.getRevengeTimer();
    }
    
    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.attacker);
        super.startExecuting();
    }
}