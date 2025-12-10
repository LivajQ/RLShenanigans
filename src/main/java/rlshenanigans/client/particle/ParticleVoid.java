package rlshenanigans.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleVoid extends Particle {
    
    public ParticleVoid(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float r, float g, float b, float size) {
        super(world, x, y, z, motionX, motionY, motionZ);
        
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.particleMaxAge = 20 + rand.nextInt(10);
        this.particleGravity = 0.0F;
        this.setRBGColorF(r, g, b);
        this.particleScale = size;
    }
}