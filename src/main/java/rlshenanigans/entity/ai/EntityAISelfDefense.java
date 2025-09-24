package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class EntityAISelfDefense<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
    public EntityAISelfDefense(EntityCreature creature, Class<T> targetClass) {
        super(creature, targetClass, 10, true, false, target -> {
            if (target == null || !target.isEntityAlive()) return false;
            
            if (!(target instanceof EntityLiving)) return false;
            EntityLiving livingTarget = (EntityLiving) target;
            
            return livingTarget.getAttackTarget() == creature;
        });
    }
}