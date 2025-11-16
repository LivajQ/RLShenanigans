package rlshenanigans.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.item.ItemSpellBase;

import javax.vecmath.Color3f;

@SideOnly(Side.CLIENT)
public class ParticleSpell extends Particle {
    
    public ParticleSpell(ItemSpellBase spell, World world, int textureIndex, int particleAge, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        
        Color3f color = spell.getParticleColor();
        this.setRBGColorF(color.x, color.y, color.z);
        this.setAlphaF(spell.getParticleAlpha());
        this.particleMaxAge = particleAge;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.setParticleTextureIndex(textureIndex);
    }
}