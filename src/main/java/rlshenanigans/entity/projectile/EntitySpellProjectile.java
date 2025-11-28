package rlshenanigans.entity.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.CombatAssistHandler;

import javax.vecmath.Color3f;
import java.lang.reflect.Field;

public abstract class EntitySpellProjectile extends EntityThrowable implements IEntityAdditionalSpawnData {
    protected Explosion explosion;
    protected float damage;
    protected float gravity;
    protected EntityLivingBase shooter;
    
    public EntitySpellProjectile(World worldIn) {
        super(worldIn);
    }
    
    public EntitySpellProjectile(World worldIn, EntityLivingBase shooter, float damage, float gravity) {
        super(worldIn, shooter);
        this.damage = damage;
        this.gravity = gravity;
        this.shooter = shooter;
    }
    
    public void onUpdate() {
        if (this.gravity == 0.0F) {
            double tempMotionX = this.motionX;
            double tempMotionY = this.motionY;
            double tempMotionZ = this.motionZ;
            
            super.onUpdate();
            
            this.motionX = tempMotionX;
            this.motionY = tempMotionY;
            this.motionZ = tempMotionZ;
        }
        else super.onUpdate();
    }
    
    public Color3f getColor() {
        return new Color3f(1.0F, 1.0F, 1.0F);
    }
    
    @Override
    protected float getGravityVelocity() {
        return gravity;
    }
    
    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.setDead();
    }
    
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(this.width);
        buffer.writeFloat(this.height);
    }
    
    @Override
    public void readSpawnData(ByteBuf additionalData) {
        float width = additionalData.readFloat();
        float height = additionalData.readFloat();
        this.setSize(width, height);
    }
    
    public float textureSize() {
        return 1.0F;
    }
    
    public boolean canDamageSelf() {
        return false;
    }
    
    @Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
    public static class Handler {
        
        @SubscribeEvent
        public static void onLivingAttack(LivingHurtEvent event) {
            DamageSource source = event.getSource();
            EntityLivingBase target = event.getEntityLiving();
            
            if (!(source.getImmediateSource() instanceof EntitySpellProjectile)) return;
            
            EntitySpellProjectile projectile = (EntitySpellProjectile) source.getImmediateSource();
            
            if (projectile.canDamageSelf()) return;
            
            EntityLivingBase caster = projectile.getThrower();
            if (caster == null || target == null) return;
            
            if (caster == target) {
                event.setCanceled(true);
                return;
            }
           
            if (caster instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) caster;
                if (CombatAssistHandler.isEntityTamedByPlayer(target, player)) event.setCanceled(true);
            }
        }
        
        @SubscribeEvent
        public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
            Explosion explosion = event.getExplosion();
            
            try {
                Field exploderField = Explosion.class.getDeclaredField("field_77283_e");
                exploderField.setAccessible(true);
                Entity exploder = (Entity) exploderField.get(explosion);
                
                if (!(exploder instanceof EntitySpellProjectile)) return;
                
                EntitySpellProjectile projectile = (EntitySpellProjectile) exploder;
                if (projectile.canDamageSelf()) return;
                EntityLivingBase caster = projectile.getThrower();
                
                event.getAffectedEntities().removeIf(entity ->
                        entity == caster || (caster instanceof EntityPlayer &&
                                        entity instanceof EntityLivingBase &&
                                        CombatAssistHandler.isEntityTamedByPlayer((EntityLivingBase) entity, (EntityPlayer) caster))
                );
                
                for (Entity entity : event.getAffectedEntities()) {
                    if (entity instanceof EntityLivingBase) {
                        EntityLivingBase target = (EntityLivingBase) entity;
                        target.attackEntityFrom(
                                new EntityDamageSource("explosion", projectile).setMagicDamage().setDamageBypassesArmor(),
                                projectile.damage
                        );
                    }
                }
                
            } catch (Exception ignored) {}
        }
    }
}