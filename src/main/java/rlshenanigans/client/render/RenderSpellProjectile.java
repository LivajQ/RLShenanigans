package rlshenanigans.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import rlshenanigans.entity.projectile.EntitySpellProjectile;

import javax.vecmath.Color3f;

public class RenderSpellProjectile<T extends EntitySpellProjectile> extends Render<T> {
    
    protected final ResourceLocation texture;
    
    public RenderSpellProjectile(RenderManager manager, ResourceLocation texture) {
        super(manager);
        this.texture = texture;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
    
    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        Color3f color = entity.getColor();
        GlStateManager.color(color.x, color.y, color.z);
        
        GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        
        this.bindEntityTexture(entity);
        
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        renderQuad();
        
        GlStateManager.popMatrix();
    }
    
    protected void renderQuad() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-0.5, -0.5, 0).tex(0, 1).endVertex();
        buffer.pos(0.5, -0.5, 0).tex(1, 1).endVertex();
        buffer.pos(0.5, 0.5, 0).tex(1, 0).endVertex();
        buffer.pos(-0.5, 0.5, 0).tex(0, 0).endVertex();
        tessellator.draw();
    }
}