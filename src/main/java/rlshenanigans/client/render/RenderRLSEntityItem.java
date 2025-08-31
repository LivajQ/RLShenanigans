package rlshenanigans.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rlshenanigans.client.model.entity.item.ModelPaintingTemplate;
import rlshenanigans.entity.item.EntityPaintingTemplate;

public class RenderRLSEntityItem<T extends Entity> extends Render<T> {
    private final ModelPaintingTemplate paintingTemplate = new ModelPaintingTemplate();
    private static final ResourceLocation PAINTING_WOOD = new ResourceLocation("rlshenanigans", "textures/entity/item/painting_template_wood.png");
    private static final float SCALE_BASE = 0.0625F;
    public RenderRLSEntityItem(RenderManager manager) {
        super(manager);
    }
    
    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.5, z);
        GlStateManager.scale(1F, -1F, -1F);
        
        if (entity instanceof EntityPaintingTemplate) {
            EntityPaintingTemplate paintingEntity = (EntityPaintingTemplate) entity;
            renderPaintingTemplate(paintingEntity);
        }
        
        GlStateManager.popMatrix();
    }
    
    private void renderPaintingTemplate(EntityPaintingTemplate paintingEntity) {
        int width = paintingEntity.getWidth();
        int height = paintingEntity.getHeight();
        
        setFacing(paintingEntity.getFacing());
        GlStateManager.translate(0, 0, 0.47F);
        GlStateManager.translate(-0.5F, 0.5F, 0F);
        GlStateManager.scale(width, height, 1F);
        GlStateManager.translate(0.5F, -0.5F, 0F);
        
        this.bindTexture(PAINTING_WOOD);
        paintingTemplate.frame.render(SCALE_BASE);
        paintingTemplate.back.render(SCALE_BASE);
        
        ResourceLocation tex = paintingEntity.getCurrentTexture();
        if (tex != null) {
            this.bindTexture(tex);
            paintingTemplate.painting.render(SCALE_BASE);
        }
    }
    
    private void setFacing(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                break;
            case SOUTH:
                break;
            case WEST:
                GlStateManager.rotate(90F, 0F, 1F, 0F);
                break;
            case EAST:
                GlStateManager.rotate(-90F, 0F, 1F, 0F);
                break;
        }
    }
    
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        if (entity instanceof EntityPaintingTemplate) {
            return ((EntityPaintingTemplate) entity).getCurrentTexture();
        }
        return PAINTING_WOOD;
    }
}