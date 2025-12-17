package rlshenanigans.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.particle.ParticleSmokeLarge;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.handlers.RLSClientHandler;

@SideOnly(Side.CLIENT)
public class ParticleRainFire extends ParticleRain {

    public ParticleRainFire(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setRBGColorF(1.0F, 0.45F, 0.02F);
        this.particleScale *= 3.0F;
        this.motionY = 0.0F;
        this.particleAlpha = 1.0F;
        this.particleMaxAge = 100;
        
        this.setParticleTexture(RLSClientHandler.PARTICLE_RAIN_TRANSPARENT);
    }
    
    public void onUpdate() {
        super.onUpdate();
        if (this.rand.nextFloat() < 0.05F) {
            Particle smoke = new ParticleSmokeLarge.Factory().createParticle(0, this.world, this.posX, this.posY, this.posZ, 0 ,0, 0);
            Minecraft.getMinecraft().effectRenderer.addEffect(smoke);
        }
        else if (this.rand.nextFloat() < 0.05F) {
            Particle flame = new ParticleFlame.Factory().createParticle(0, this.world, this.posX, this.posY, this.posZ, 0 ,-0.2F, 0);
            Minecraft.getMinecraft().effectRenderer.addEffect(flame);
        }
    }
    
    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

        float scale = 0.1F * this.particleScale;
        
        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
        
        Vec3d[] corners = new Vec3d[] {
                new Vec3d(-rotationX * scale - rotationXY * scale, -rotationZ * scale, -rotationYZ * scale - rotationXZ * scale),
                new Vec3d(-rotationX * scale + rotationXY * scale,  rotationZ * scale, -rotationYZ * scale + rotationXZ * scale),
                new Vec3d( rotationX * scale + rotationXY * scale,  rotationZ * scale,  rotationYZ * scale + rotationXZ * scale),
                new Vec3d( rotationX * scale - rotationXY * scale, -rotationZ * scale,  rotationYZ * scale - rotationXZ * scale)
        };
        
        if (this.particleAngle != 0.0F) {
            float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float cos = MathHelper.cos(angle * 0.5F);
            float sin = MathHelper.sin(angle * 0.5F);
            
            Vec3d axis = new Vec3d(
                    sin * cameraViewDir.x,
                    sin * cameraViewDir.y,
                    sin * cameraViewDir.z
            );
            
            for (int i = 0; i < 4; ++i) {
                corners[i] = axis.scale(2.0D * corners[i].dotProduct(axis))
                        .add(corners[i].scale(cos * cos - axis.dotProduct(axis)))
                        .add(axis.crossProduct(corners[i]).scale(2.0F * cos));
            }
        }
        
        int brightness = this.getBrightnessForRender(partialTicks);
        int j = brightness >> 16 & 65535;
        int k = brightness & 65535;
        
        TextureAtlasSprite s = this.particleTexture;
        
        int index = this.particleTextureIndexX + this.particleTextureIndexY * 16;
        
        int cellsX = 16;
        int cellsY = 16;
        
        int cx = index % cellsX;
        int cy = index / cellsX;
        
        float su0 = s.getMinU();
        float su1 = s.getMaxU();
        float sv0 = s.getMinV();
        float sv1 = s.getMaxV();
        
        float uSpan = su1 - su0;
        float vSpan = sv1 - sv0;
        
        float cellU = uSpan / cellsX;
        float cellV = vSpan / cellsY;
        
        float u0 = su0 + cx * cellU;
        float u1 = u0 + cellU;
        float v0 = sv0 + cy * cellV;
        float v1 = v0 + cellV;

        buffer.pos(x + corners[0].x, y + corners[0].y, z + corners[0].z).tex(u1, v1).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(x + corners[1].x, y + corners[1].y, z + corners[1].z).tex(u1, v0).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(x + corners[2].x, y + corners[2].y, z + corners[2].z).tex(u0, v0).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(x + corners[3].x, y + corners[3].y, z + corners[3].z).tex(u0, v1).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(j, k).endVertex();
    }
    
    @Override
    public void setParticleTextureIndex(int particleTextureIndex) {
        this.particleTextureIndexX = particleTextureIndex % 16;
        this.particleTextureIndexY = particleTextureIndex / 16;
    }
    
    @Override
    public int getFXLayer() {
        return 1;
    }
    
    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }
}