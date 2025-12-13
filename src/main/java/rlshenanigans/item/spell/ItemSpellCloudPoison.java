package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import rlshenanigans.entity.spell.EntitySpellCloudPoison;
import rlshenanigans.handlers.ForgeConfigHandler;

import javax.vecmath.Color3f;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class ItemSpellCloudPoison extends ItemSpellBase {

    public ItemSpellCloudPoison(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 0.4F);
        
        int textureIndex = getTextureIndexFromEnum(EnumParticleTypes.CLOUD);
        int particleCount = 80;
        int particleAge = 40;
        double particleSpeed = 3.0D;
        double particleSpread = 0.8D;
        
        Vec3d look = caster.getLookVec();
        
        double originX = caster.posX;
        double originY = caster.posY + caster.getEyeHeight();
        double originZ = caster.posZ;
        
        double posX = originX + look.x * 0.3;
        double posY = originY + look.y * 0.3;
        double posZ = originZ + look.z * 0.3;
        
        for (int i = 0; i < particleCount; i++) {
            double motionX = (look.x * particleSpeed + ((RLSRAND.nextDouble() - 0.5D) * particleSpread)) * 0.1D;
            double motionY = (look.y * particleSpeed + ((RLSRAND.nextDouble() - 0.5D) * particleSpread)) * 0.1D;
            double motionZ = (look.z * particleSpeed + ((RLSRAND.nextDouble() - 0.5D) * particleSpread)) * 0.1D;

            spawnCastParticle(textureIndex, 1, particleAge, posX, posY, posZ, motionX, motionY, motionZ);
        }
        
        EntitySpellCloudPoison cloud = new EntitySpellCloudPoison(caster.world, caster, look.x, look.y, look.z);
        cloud.setPosition(originX, originY - cloud.height / 2, originZ);
        caster.world.spawnEntity(cloud);
    }
    
    @Override
    public Color3f getParticleColor() {
        return new Color3f(0.7F, 0.0F, 0.7F);
    }
}