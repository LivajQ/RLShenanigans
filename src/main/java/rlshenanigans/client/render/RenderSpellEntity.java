package rlshenanigans.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import rlshenanigans.entity.spell.EntitySpellBase;

@SideOnly(Side.CLIENT)
public class RenderSpellEntity<T extends EntitySpellBase> extends Render<T> {
    
    protected static final ResourceLocation WHITE_TEXTURE =
            new ResourceLocation("minecraft:textures/blocks/concrete_white.png");
    protected final ResourceLocation texture;
    protected final ModelBase model;
    
    public RenderSpellEntity(RenderManager renderManager, ModelBase model, ResourceLocation texture) {
        super(renderManager);
        this.texture = texture;
        this.model = model;
    }
    
    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        
        GlStateManager.translate(x, y - entity.height * 1.5F, z);
        GlStateManager.scale(entity.width, entity.height, entity.width);
        
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        GlStateManager.disableLighting();
        
        GlStateManager.color(entity.red, entity.green, entity.blue, entity.alpha);
        
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        if (entity.movingTexture()) {
            float time = entity.ticksExisted + partialTicks;
            GlStateManager.translate(time * 0.05F, 0.0F, 0.0F);
        }
        float textureScale = entity.textureScale();
        GlStateManager.scale(textureScale, textureScale, textureScale);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.disableCull();
        
        this.bindEntityTexture(entity);
        this.model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
        
        GlStateManager.enableCull();
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean shouldRender(T entity, ICamera camera, double camX, double camY, double camZ) {
        return true;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return texture != null ? texture : WHITE_TEXTURE;
    }
}