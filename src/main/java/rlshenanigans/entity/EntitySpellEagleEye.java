package rlshenanigans.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

public class EntitySpellEagleEye extends EntitySpellBase {
    private int lifetime;

    public EntitySpellEagleEye(World world) {
        this(world, null, 6000);
    }
    
    public EntitySpellEagleEye(World world, EntityLivingBase caster, int lifetime) {
        super(world, caster, 1.0F, 1.0F, 1.0F, 1.0F);
        this.noClip = true;
        this.lifetime = lifetime;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (caster == null || lifetime-- <= 0) {
            this.setDead();
            return;
        }
        
        this.setPosition(caster.posX, caster.posY + caster.height + 0.5F, caster.posZ);
        
        if (!this.world.isRemote && this.ticksExisted % 20 == 0) {
            List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB (EntityLivingBase.class, this.getEntityBoundingBox().grow(32));
            for (EntityLivingBase entity : entities) {
                if (entity == caster && !caster.isPotionActive(MobEffects.NIGHT_VISION)) caster.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 400));
                if (entity != caster && !entity.isPotionActive(MobEffects.GLOWING)) entity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 400));
            }
        }
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.setDead();
    }
}