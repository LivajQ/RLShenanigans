package rlshenanigans.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleHalo extends Particle {
    
    public ParticleHalo(World world, IParticleHalo parent) {
        super(world, parent.getParentPos().x, parent.getParentPos().y, parent.getParentPos().z);
        this.setRBGColorF(1.0F, 0.6F, 0.1F);
        this.motionY = 0.0F;
        this.particleAlpha = 1.0F;
        this.particleMaxAge = 80;
        
    }
}
