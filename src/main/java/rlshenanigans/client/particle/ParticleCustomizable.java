package rlshenanigans.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleCustomizable extends Particle {
    
    public ParticleCustomizable(World world, int textureIndex, double x, double y, double z) {
        this(world, textureIndex, x, y, z, 0.0D, 0.0D, 0.0D);
    }
    
    public ParticleCustomizable(World world, int textureIndex, double x, double y, double z,
                                double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.setParticleTextureIndex(textureIndex);
    }
    
    public ParticleCustomizable color(int argb) {
        float a = ((argb >> 24) & 0xFF) / 255f;
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8)  & 0xFF) / 255f;
        float b = (argb         & 0xFF) / 255f;
        
        return color(r, g, b, a);
    }
    
    public ParticleCustomizable color(float r, float g, float b, float a) {
        this.setRBGColorF(r, g, b);
        this.particleAlpha = a;
        return this;
    }
    
    public ParticleCustomizable maxAge(int maxAge) {
        this.particleMaxAge = maxAge;
        return this;
    }
    
    public ParticleCustomizable scale(float scale) {
        this.particleScale = scale;
        this.setSize(scale, scale);
        return this;
    }
}