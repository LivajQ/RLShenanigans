package rlshenanigans.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import rlshenanigans.entity.EntitySpellBase;
import rlshenanigans.entity.ISpellLightning;

import javax.vecmath.Color4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderSpellLightning<T extends EntitySpellBase & ISpellLightning> extends RenderSpellEntity<T> {
    
    public RenderSpellLightning(RenderManager renderManager, ModelBase model, ResourceLocation texture) {
        super(renderManager, model, texture);
    }
    
    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        EntityLivingBase target = entity.getTarget();
        EntityLivingBase previousTarget = entity.getPreviousTarget();
        if (target == null || previousTarget == null || target == previousTarget) return;
        
        double tx = target.posX - entity.posX + x;
        double ty = target.posY + target.height * 0.5 - entity.posY + y;
        double tz = target.posZ - entity.posZ + z;
        
        double px = previousTarget.posX - entity.posX + x;
        double py = previousTarget.posY + previousTarget.height * 0.5 - entity.posY + y;
        double pz = previousTarget.posZ - entity.posZ + z;
        
        Random rand = entity.world.rand;
        
        List<Vec3d> points = new ArrayList<>();
        int segments = 8;
        double jitter = 0.3;
        for (int i = 0; i <= segments; i++) {
            double t = i / (double) segments;
            double ix = px + (tx - px) * t + (rand.nextDouble() - 0.5) * jitter;
            double iy = py + (ty - py) * t + (rand.nextDouble() - 0.5) * jitter;
            double iz = pz + (tz - pz) * t + (rand.nextDouble() - 0.5) * jitter;
            points.add(new Vec3d(ix, iy, iz));
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        Color4f c = entity.getColor();
        GlStateManager.color(c.x, c.y, c.z, c.w);
        GL11.glLineWidth(5.0f);
        
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        
        for (Vec3d p : points) {
            buf.pos(p.x, p.y, p.z).endVertex();
            
            if (rand.nextFloat() < 0.03F) {
                double worldX = p.x + entity.posX - x;
                double worldY = p.y + entity.posY - y;
                double worldZ = p.z + entity.posZ - z;
                
                entity.world.spawnParticle(
                        EnumParticleTypes.SPELL_MOB,
                        worldX, worldY, worldZ,
                         1.0,
                         1.0,
                         0.0
                );
            }
        }
        
        tess.draw();
        
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}