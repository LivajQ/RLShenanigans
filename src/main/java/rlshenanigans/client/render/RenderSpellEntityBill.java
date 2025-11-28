package rlshenanigans.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import rlshenanigans.entity.EntitySpellBase;

@SideOnly(Side.CLIENT)
public class RenderSpellEntityBill<T extends EntitySpellBase> extends RenderSpellEntity<T> {
    public float size;
    
    public RenderSpellEntityBill(RenderManager renderManager, ModelBase model, float size, ResourceLocation texture) {
        super(renderManager, model, texture);
        this.size = size;
    }
    
    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.rotate(180 - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        this.bindTexture(this.getEntityTexture(entity));

        GlStateManager.color(entity.red, entity.green, entity.blue, entity.alpha);
        
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        
        buffer.pos(-size, -size, 0).tex(0, 1).endVertex();
        buffer.pos( size, -size, 0).tex(1, 1).endVertex();
        buffer.pos( size,  size, 0).tex(1, 0).endVertex();
        buffer.pos(-size,  size, 0).tex(0, 0).endVertex();
        
        tess.draw();
        
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}