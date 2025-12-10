package rlshenanigans.entity.projectile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.particle.ParticleVoid;
import rlshenanigans.entity.EntitySpellImplosion;

import javax.vecmath.Color3f;

public class ProjectileSpellImplosion extends ProjectileSpellBase {
    private int lifetime;
    
    public ProjectileSpellImplosion(World worldIn) {
        super(worldIn);
    }
    
    public ProjectileSpellImplosion(World worldIn, EntityLivingBase shooter, float damage, float size, float gravity) {
        this(worldIn, shooter, damage, size, gravity, 60);
    }
    
    public ProjectileSpellImplosion(World worldIn, EntityLivingBase shooter, float damage, float size, float gravity, int lifetime) {
        super(worldIn, shooter, damage, gravity);
        this.setSize(size, size);
        this.lifetime = lifetime;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (world.isRemote) {
            AxisAlignedBB bb = this.getEntityBoundingBox();
            
            double centerX = (bb.minX + bb.maxX) / 2.0;
            double centerY = (bb.minY + bb.maxY) / 2.0;
            double centerZ = (bb.minZ + bb.maxZ) / 2.0;
            
            double expand = 2.5F;
            
            double x = bb.minX + rand.nextDouble() * (bb.maxX - bb.minX);
            double y = bb.minY + rand.nextDouble() * (bb.maxY - bb.minY);
            double z = bb.minZ + rand.nextDouble() * (bb.maxZ - bb.minZ);
            x += (rand.nextDouble() - 0.5) * expand;
            y += (rand.nextDouble() - 0.5) * expand;
            z += (rand.nextDouble() - 0.5) * expand;
            double velX = (centerX - x) * 0.1;
            double velY = (centerY - y) * 0.1;
            double velZ = (centerZ - z) * 0.1;
            
            spawnVoidParticle(x, y, z, velX, velY, velZ);
            
            return;
        }
        
        if (lifetime-- <= 0) this.onImpact(new RayTraceResult(this));
    }
    
    @SideOnly(Side.CLIENT)
    private void spawnVoidParticle(double x, double y, double z, double velX, double velY, double velZ) {
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new ParticleVoid(world, x, y, z, velX, velY, velZ, 0.0F, 0.0F, 0.0F, 2.5F)
        );
    }
    
    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            EntitySpellImplosion implosion = new EntitySpellImplosion(world, shooter, 60);
            implosion.setPosition(posX, posY, posZ);
            world.spawnEntity(implosion);
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
        return new Color3f(0.3F, 0.3F, 0.3F);
    }
    
    @Override
    public float textureSize() {
        return 2.0F;
    }
}
