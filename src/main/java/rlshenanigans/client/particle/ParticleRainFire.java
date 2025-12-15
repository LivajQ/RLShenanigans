package rlshenanigans.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleRainFire extends ParticleRain implements IParticleHalo {
    
    private final Vector3d parentPos = new Vector3d();
    private static final ResourceLocation TEXTURE = new ResourceLocation("rlshenanigans:textures/particle/rain_transparent.png");
    
    public ParticleRainFire(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setRBGColorF(1.0F, 0.6F, 0.1F);
        this.motionY = 0.0F;
        this.particleAlpha = 1.0F;
        this.particleMaxAge = 80;
        ParticleHalo halo = new ParticleHalo(world, this);
    }
    
    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }
    
    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }
    
    @Override
    public Vector3d getParentPos() {
        parentPos.x = this.posX;
        parentPos.y = this.posY;
        parentPos.z = this.posZ;
        return parentPos;
    }
}