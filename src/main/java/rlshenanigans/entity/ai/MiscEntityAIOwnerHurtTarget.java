package rlshenanigans.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.world.World;

import java.util.UUID;

public class MiscEntityAIOwnerHurtTarget extends EntityAITarget
{
    private final EntityCreature mob;
    private EntityLivingBase target;
    private final World world;
    private boolean tamed;
    private UUID ownerId;
    
    public MiscEntityAIOwnerHurtTarget(EntityCreature mob) {
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
        
        target = owner.getRevengeTarget();
        if (target == null) return false;
        
        if (target.getEntityData().hasUniqueId("OwnerUUID")) {
            UUID targetOwnerId = target.getEntityData().getUniqueId("OwnerUUID");
            if (targetOwnerId.equals(owner.getUniqueID())) return false;
        }
        
        if (EntityGorgon.isStoneMob(target)) return false;
        
        if (target.equals(mob)) return false;
        if (target.equals(owner)) return false;
        
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
}