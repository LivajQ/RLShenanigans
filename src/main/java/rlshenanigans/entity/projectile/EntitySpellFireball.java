package rlshenanigans.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.vecmath.Color3f;

public class EntitySpellFireball extends EntitySpellProjectile {
    
    public EntitySpellFireball(World worldIn) {
        super(worldIn);
    }
    
    public EntitySpellFireball(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
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
            world.createExplosion(this, posX, posY, posZ, 2.0F, true);
            this.setDead();
        }
    }
    
    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
    
    @Override
    public Color3f getColor() {
        return new Color3f(1.0F, 1.0F, 0.0F);
    }
}