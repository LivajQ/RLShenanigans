package rlshenanigans.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;

import javax.vecmath.Color3f;


public abstract class EntitySpellProjectile extends EntityThrowable {
    
    public EntitySpellProjectile(World worldIn) {
        super(worldIn);
    }
    
    public EntitySpellProjectile(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }
    
    public Color3f getColor() {
        return new Color3f(1.0F, 1.0F, 1.0F);
    }
}
