package rlshenanigans.entity.spell;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.particle.ParticleRainFire;
import rlshenanigans.handlers.CombatAssistHandler;

import java.util.List;

public class EntitySpellRainOfFire extends EntitySpellBase {
    private int lifetime;
    private final float radius;
    private final float altitude;
    private final float particleAmount;
    
    public EntitySpellRainOfFire(World world) {
        this(world, null, 400);
    }
    
    public EntitySpellRainOfFire(World world, EntityLivingBase caster, int lifetime) {
        this(world, caster, lifetime, 12, 24.0F, 16.0F);
    }
    
    public EntitySpellRainOfFire(World world, EntityLivingBase caster, int lifetime, int particleAmount, float radius, float altitude) {
        super(world, caster, 0.0F, 0.0F, 0.0F, 0.0F);
        this.noClip = true;
        this.lifetime = lifetime;
        this.radius = radius;
        this.altitude = altitude;
        this.particleAmount = particleAmount;
        this.setNoGravity(true);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (this.ticksExisted < 10) return;
        if (caster != null) this.setPosition(caster.posX, caster.posY + caster.height + this.altitude, caster.posZ);
        
        if (world.isRemote) {
            this.spawnRainParticles();
            return;
        }
        
        if (lifetime-- <= 0) this.setDead();
        
        if (this.ticksExisted % 20 == 0 && caster != null) {
            double cx = caster.posX;
            double cy = caster.posY + caster.height + this.altitude;
            double cz = caster.posZ;
            
            AxisAlignedBB box = new AxisAlignedBB(
                    cx - this.radius,
                    cy - 50,
                    cz - this.radius,
                    cx + this.radius,
                    cy,
                    cz + this.radius
            );
            
            
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, box, this::isValidTarget);
            
            for (EntityLivingBase entity : entities) {
                entity.attackEntityFrom(EntityDamageSource.causeIndirectMagicDamage(this, caster), 5.0F);
                entity.setFire(100);
            }
        }
        
        if (this.ticksExisted % 5 == 0 && world.rand.nextFloat() < 0.5F) {
            
            double cx = caster.posX;
            double cy = caster.posY + caster.height + this.altitude;
            double cz = caster.posZ;
            
            double rx = cx + (world.rand.nextDouble() - 0.5) * this.radius * 2;
            double rz = cz + (world.rand.nextDouble() - 0.5) * this.radius * 2;
            
            RayTraceResult hit = world.rayTraceBlocks(
                    new Vec3d(rx, cy, rz),
                    new Vec3d(rx, cy - 100, rz),
                    false,
                    true,
                    false
            );
            
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = hit.getBlockPos().up();

                if (world.isAirBlock(pos) && world.getBlockState(pos.down()).isOpaqueCube()) {
                    world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            }
        }
        
    }
    
    private boolean isValidTarget(EntityLivingBase e) {
        if (caster == null) return false;
        
        boolean validTarget = e != caster &&
                        (!(caster instanceof EntityPlayer) || !CombatAssistHandler.isEntityTamedByPlayer(e, (EntityPlayer) caster));
        
        double startY = e.posY + e.height;
        double endY = caster.posY + caster.height + this.altitude;
        
        RayTraceResult result = world.rayTraceBlocks(
                new Vec3d(e.posX, startY, e.posZ),
                new Vec3d(e.posX, endY, e.posZ),
                true,
                true,
                false
        );
        
        boolean blocked = (result != null);
    
        return validTarget && !blocked;
    }
    
    @SideOnly(Side.CLIENT)
    private void spawnRainParticles() {
        double x = this.posX;
        double y = this.posY;
        double z = this.posZ;
        
        for (int i = 0; i < this.particleAmount; i++) {
            double px = x + (world.rand.nextDouble() - 0.5) * this.radius * 2;
            double pz = z + (world.rand.nextDouble() - 0.5) * this.radius * 2;
            
            Minecraft.getMinecraft().effectRenderer.addEffect(
                    new ParticleRainFire(world, px, y, pz)
            );
        }
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.setDead();
    }
}