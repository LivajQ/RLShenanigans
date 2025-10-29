package rlshenanigans.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.world.World;

import java.util.UUID;

public class MiscEntityAIOwnerHurtByTarget extends EntityAITarget
{
    private final EntityCreature mob;
    private EntityLivingBase attacker;
    private final World world;
    private boolean tamed;
    private UUID ownerId;
    
    public MiscEntityAIOwnerHurtByTarget(EntityCreature mob) {
        super(mob, false);
        this.mob = mob;
        this.world = mob.world;
        this.tamed = mob.getEntityData().getBoolean("MiscTamed");
        this.ownerId = mob.getEntityData().getUniqueId("OwnerUUID");
        this.setMutexBits(1);
    }
    
    
    @Override
    public boolean shouldExecute() {
        if (!tamed) {
            if (mob.ticksExisted % 100 == 0) {
                tamed = mob.getEntityData().getBoolean("MiscTamed");
                ownerId = mob.getEntityData().getUniqueId("OwnerUUID");
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
        
        if (EntityGorgon.isStoneMob(attacker)) return false;
        
        if (attacker.equals(mob)) return false;
        if (attacker.equals(owner)) return false;
        
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
}