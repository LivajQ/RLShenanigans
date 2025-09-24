package rlshenanigans.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.npc.EntityNPCPhantom;

@SideOnly(Side.CLIENT)
public class ParticleNPCPhantomSummon extends Particle
{
    public ParticleNPCPhantomSummon(World world, EntityNPCPhantom phantom, double x, double y, double z, double motionX, double motionY, double motionZ, int color) {
        super(world, x, y, z, motionX, motionY, motionZ);
        
        this.particleMaxAge = 20 + rand.nextInt(10);
        this.particleGravity = 0.1F;
        Vec3d glowColor = phantom.getPhantomGlowColor();
        this.setRBGColorF((float) glowColor.x, (float) glowColor.y, (float) glowColor.z);
        this.setSize(0.4F, 0.4F);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
    }
}