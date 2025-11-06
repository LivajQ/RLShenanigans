package rlshenanigans.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.EntitySpellBase;

@SideOnly(Side.CLIENT)
public class RenderSpellEntity<T extends EntitySpellBase> extends RenderLivingBase<T> {
    
    private static final ResourceLocation WHITE_TEXTURE =
            new ResourceLocation("minecraft:textures/blocks/concrete_white.png");
    private final ResourceLocation texture;
    
    public RenderSpellEntity(RenderManager renderManager, ModelBase model, ResourceLocation texture) {
        super(renderManager, model, 0.0F);
        this.texture = texture;
    }
    
    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y - entity.height / 2.0, z);
        GlStateManager.scale(entity.width, entity.height, entity.width);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        GlStateManager.disableLighting();
        
        GlStateManager.color(entity.red, entity.green, entity.blue, entity.alpha);
        
        super.doRender(entity, 0, 0, 0, entityYaw, partialTicks);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean shouldRender(T entity, ICamera camera, double camX, double camY, double camZ) {
        return true;
    }
    
    @Override
    protected boolean canRenderName(T entity) {
        return false;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture != null ? texture : WHITE_TEXTURE;
    }
}