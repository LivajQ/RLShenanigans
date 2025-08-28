package rlshenanigans.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import rlshenanigans.client.model.block.ModelPaintingTemplate;
import rlshenanigans.tileentity.TileEntityPaintingTemplate;

@SuppressWarnings("rawtypes")
public class RenderRLSTileEntity extends TileEntitySpecialRenderer {
    private final ModelPaintingTemplate paintingTemplate = new ModelPaintingTemplate();
    private static final ResourceLocation PAINTINTG_WOOD = new ResourceLocation("rlshenanigans", "textures/blocks/painting_template_wood.png");
    
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.scale(1F, -1F, -1F);
        
        float scale = 0.0625F;
        
        if (te instanceof TileEntityPaintingTemplate) {
            TileEntityPaintingTemplate paintingTE = (TileEntityPaintingTemplate) te;
            renderPaintingTemplate(scale, paintingTE);
        }
        
        GlStateManager.popMatrix();
    }
    
    
    public void renderPaintingTemplate(float scale, TileEntityPaintingTemplate te) {
        this.bindTexture(PAINTINTG_WOOD);
        paintingTemplate.frame.render(scale);
        paintingTemplate.back.render(scale);
        
        ResourceLocation tex = te.getPainting();
        if (tex != null) {
            this.bindTexture(tex);
            paintingTemplate.painting.render(scale);
        }
    }
}