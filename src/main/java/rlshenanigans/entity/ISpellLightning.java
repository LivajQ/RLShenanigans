package rlshenanigans.entity;

import net.minecraft.entity.EntityLivingBase;

import javax.vecmath.Color4f;

public interface ISpellLightning {
    EntityLivingBase getTarget();
    
    EntityLivingBase getPreviousTarget();
    
    Color4f getColor();
}
