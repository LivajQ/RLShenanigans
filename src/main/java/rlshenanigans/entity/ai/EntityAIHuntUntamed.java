package rlshenanigans.entity.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import rlshenanigans.handlers.CombatAssistHandler;

import javax.annotation.Nullable;

public class EntityAIHuntUntamed<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
    private final boolean notifyPrey;
    
    public EntityAIHuntUntamed(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean notifyPrey) {
        this(creature, classTarget, checkSight, false, notifyPrey);
    }
    
    public EntityAIHuntUntamed(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby, boolean notifyPrey) {
        this(creature, classTarget, 10, checkSight, onlyNearby, notifyPrey, null);
    }
    
    public EntityAIHuntUntamed(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, boolean notifyPrey, @Nullable final Predicate <? super T > targetSelector) {
        super(creature, classTarget, chance, checkSight, onlyNearby, target -> {
            if (target == null || !target.isEntityAlive()) return false;
            
            if (targetSelector != null && !targetSelector.test(target)) return false;
            
            return !CombatAssistHandler.isEntityTamed(target);
        });
        this.notifyPrey = notifyPrey;
    }
    
    @Override
    public void updateTask() {
        super.updateTask();
        if (targetEntity instanceof EntityLiving && this.notifyPrey) {
            EntityLiving targetLiving = (EntityLiving) targetEntity;
            if (targetLiving.getAttackTarget() == null) targetLiving.setAttackTarget(taskOwner);
        }
    }
}