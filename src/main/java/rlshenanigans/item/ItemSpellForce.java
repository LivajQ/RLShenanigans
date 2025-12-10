package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;

import javax.vecmath.Color3f;
import java.util.List;

public class ItemSpellForce extends ItemSpellBase {
    
    public ItemSpellForce(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, 0.45F); //mid
        for (int x = 1; x <= 60; x++) {
            this.spawnCastParticle(caster, getTextureIndexFromEnum(EnumParticleTypes.CRIT_MAGIC), 1, 0.3D);
        }
        caster.world.createExplosion(caster, caster.posX, caster.posY + caster.height / 2, caster.posZ, 4.0F, false);
        
        List<EntityLivingBase> entities = caster.world.getEntitiesWithinAABB(EntityLivingBase.class,
                caster.getEntityBoundingBox().grow(8), e -> e != caster);
        
        for (EntityLivingBase entity : entities) {
            double dx = entity.posX - caster.posX;
            double dz = entity.posZ - caster.posZ;
            double distance = Math.sqrt(dx * dx + dz * dz);
            
            if (distance > 0.01F) {
                double strength = 5.0D;
                double knockbackX = (dx / distance) * strength;
                double knockbackZ = (dz / distance) * strength;
                
                entity.addVelocity(knockbackX, strength / 2, knockbackZ);
                entity.velocityChanged = true;
            }
        }
    }
    
    @Override
    public Color3f getParticleColor() {
        return new Color3f(0.9F, 0.9F, 0.9F);
    }
    
    @Override
    public float getParticleAlpha() {
        return 0.85F;
    }
}
