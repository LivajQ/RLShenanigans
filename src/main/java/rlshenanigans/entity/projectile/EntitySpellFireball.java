package rlshenanigans.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.vecmath.Color3f;

public class EntitySpellFireball extends EntitySpellProjectile {
    protected float strength;
    
    public EntitySpellFireball(World worldIn) {
        super(worldIn);
    }
    
    public EntitySpellFireball(World worldIn, EntityLivingBase shooter, float damage, float size, float strength) {
        this(worldIn, shooter, damage, size, strength, 0.0F);

    }
    
    public EntitySpellFireball(World worldIn, EntityLivingBase shooter, float damage, float size, float strength, float gravity) {
        super(worldIn, shooter, damage, gravity);
        this.strength = strength;
        this.setSize(size, size);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (world.isRemote) {
            for (int i = 0; i < 2; i++) {
                world.spawnParticle(EnumParticleTypes.FLAME,
                        posX + (rand.nextDouble() - 0.5) * 0.2,
                        posY + (rand.nextDouble() - 0.5) * 0.2,
                        posZ + (rand.nextDouble() - 0.5) * 0.2,
                        0, 0, 0);
            }
        }
    }
    
    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            world.createExplosion(this, posX, posY, posZ, strength, true);
            this.setDead();
        }
    }
    
    @Override
    public boolean isInWater() {
        return false;
    }
    
    @Override
    public boolean handleWaterMovement() {
        return false;
    }
    
    @Override
    public Color3f getColor() {
        return new Color3f(1.0F, 0.0F, 0.0F);
    }
    
    @Override
    public float textureSize() {
        return 2.0F;
    }
}