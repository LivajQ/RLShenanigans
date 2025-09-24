package rlshenanigans.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderRLSModel<T extends EntityLiving> extends RenderLiving<T> {
    private final ResourceLocation texture;
    
    public RenderRLSModel(RenderManager renderManager, ModelBase model, float shadowSize, ResourceLocation texture) {
        super(renderManager, model, shadowSize);
        this.texture = texture;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture;
    }
}