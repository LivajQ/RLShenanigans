package rlshenanigans.entity.spell;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.particle.ParticleVoid;

import java.util.List;

public class EntitySpellImplosion extends EntitySpellBase {
    private int lifetime;
    
    public EntitySpellImplosion(World world) {
        this(world, null, 60);
    }
    
    public EntitySpellImplosion(World world, EntityLivingBase caster, int lifetime) {
        super(world, caster, 0.0F, 0.0F, 0.0F, 1.0F);
        this.noClip = true;
        this.lifetime = lifetime;
        this.setSize(3.0F, 3.0F);
        this.setNoGravity(true);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (world.isRemote) {
            AxisAlignedBB bb = this.getEntityBoundingBox();
            
            for (int i = 0; i < 5; i++) {
                double centerX = (bb.minX + bb.maxX) / 2.0;
                double centerY = (bb.minY + bb.maxY) / 2.0;
                double centerZ = (bb.minZ + bb.maxZ) / 2.0;
                
                double expand = 10.0F;
                
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
            }
            return;
        }
        
        if (lifetime-- <= 0) {
            List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
                    this.getEntityBoundingBox().grow(8), e -> e != caster);
            
            for (EntityLivingBase entity : entities) {
                double dx = entity.posX - this.posX;
                double dz = entity.posZ - this.posZ;
                double distance = Math.sqrt(dx * dx + dz * dz);
                
                if (distance > 0.01F) {
                    double strength = 7.0D;
                    double knockbackX = (dx / distance) * strength;
                    double knockbackZ = (dz / distance) * strength;
                    
                    entity.addVelocity(knockbackX, strength / 2, knockbackZ);
                    entity.velocityChanged = true;
                }
            }
            world.createExplosion(this, this.posX, this.posY + this.height / 2, this.posZ, 12.0F, true);
            
            this.setDead();
            return;
        }
        
        List<EntityLivingBase> entities = caster.world.getEntitiesWithinAABB(EntityLivingBase.class,
                caster.getEntityBoundingBox().grow(24), e -> e != caster);
        
        for (EntityLivingBase entity : entities) {
            double dx = this.posX - entity.posX;
            double dy = this.posY - entity.posY;
            double dz = this.posZ - entity.posZ;
            
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance < 0.01) continue;
            
            double strength = 0.25D;
            double pullX = (dx / distance) * strength;
            double pullY = (dy / distance) * strength;
            double pullZ = (dz / distance) * strength;
            
            entity.addVelocity(pullX, pullY, pullZ);
            entity.velocityChanged = true;
        }
        
    }
    
    @SideOnly(Side.CLIENT)
    private void spawnVoidParticle(double x, double y, double z, double velX, double velY, double velZ) {
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new ParticleVoid(world, x, y, z, velX, velY, velZ, 0.0F, 0.0F, 0.0F, 8.5F)
        );
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.setDead();
    }
}