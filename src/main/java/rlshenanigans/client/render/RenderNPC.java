package rlshenanigans.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import rlshenanigans.entity.npc.EntityNPCBase;
import rlshenanigans.entity.npc.EntityNPCPhantom;

public class RenderNPC extends RenderLiving<EntityNPCBase> {
    
    public RenderNPC(RenderManager renderManager, ModelBase model, float shadowSize, ResourceLocation texture) {
        super(renderManager, model, shadowSize);
        
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityNPCBase entity) {
        return entity.getSkin();
    }
    
    @Override
    public void doRender(EntityNPCBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelPlayer model = (ModelPlayer) this.getMainModel();
        model.leftArmPose = entity.isBlocking() ? ModelBiped.ArmPose.BLOCK : ModelBiped.ArmPose.EMPTY;
        
        boolean isPhantom = entity instanceof EntityNPCPhantom;
        if (isPhantom) {
            float fade = ((EntityNPCPhantom) entity).getFadeLevel();
            Vec3d glowColor = ((EntityNPCPhantom) entity).getPhantomGlowColor();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color((float) glowColor.x, (float) glowColor.y, (float) glowColor.z, 0.5F * fade);
        }
        
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        
        if (isPhantom) {
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    
    @Override
    protected void renderLayers(EntityNPCBase entity, float limbSwing, float limbSwingAmount,
                                float partialTicks, float ageInTicks, float netHeadYaw,
                                float headPitch, float scale) {
        if (entity instanceof EntityNPCPhantom) {
            float fade = ((EntityNPCPhantom) entity).getFadeLevel();
            if (fade < 0.5F) return;
        }
        
        super.renderLayers(entity, limbSwing, limbSwingAmount, partialTicks,
                ageInTicks, netHeadYaw, headPitch, scale);
    }
}