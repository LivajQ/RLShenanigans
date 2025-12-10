package rlshenanigans.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.npc.EntityNPCPhantom;

import javax.vecmath.Color3f;

@SideOnly(Side.CLIENT)
public class ParticleNPCPhantomSummon extends Particle
{
    public ParticleNPCPhantomSummon(World world, EntityNPCPhantom phantom, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        
        this.particleMaxAge = 20 + rand.nextInt(10);
        this.particleGravity = 0.1F;
        Color3f glowColor = phantom.getPhantomGlowColor();
        this.setRBGColorF(glowColor.x, glowColor.y, glowColor.z);
        this.setSize(0.4F, 0.4F);
    }
}