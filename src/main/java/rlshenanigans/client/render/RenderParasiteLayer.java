package rlshenanigans.client.render;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import rlshenanigans.handlers.ForgeConfigHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RenderParasiteLayer implements LayerRenderer<EntityParasiteBase> {
    private final RenderParasiteEntity renderer;
    private static final Map<ResourceLocation, Boolean> layerPresent = new HashMap<>();
    
    public RenderParasiteLayer(RenderParasiteEntity renderer) {
        this.renderer = renderer;
    }
    
    private ResourceLocation getLayerTexture(EntityParasiteBase entity) {
        if (!ForgeConfigHandler.client.thhEnabled || !entity.hasCustomName()) return null;
        
        String suffix = renderer.getSuffixForSkin(entity);
        String path = "srparasites:textures/entity/monster/thh/" + renderer.getTextureName() + suffix + "_layer.png";
        return new ResourceLocation(path);
    }
    
    @Override
    public void doRenderLayer(EntityParasiteBase entity, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entity.hasCustomName()) return;
        
        ResourceLocation layerTex = getLayerTexture(entity);
        if (layerTex == null) return;
        
        Boolean exists = layerPresent.get(layerTex);
        if (exists == null) {
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(layerTex);
                layerPresent.put(layerTex, true);
                exists = true;
            } catch (IOException e) {
                layerPresent.put(layerTex, false);
                exists = false;
            }
        }
        if (!exists) return;
        
        this.renderer.bindTexture(layerTex);
        
        if (ForgeConfigHandler.client.rainbowThighs == 1 || ForgeConfigHandler.client.rainbowThighs == 3)
            RenderParasiteEntity.applyRainbowColor(entity, partialTicks, 1.5F);
        
        
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1.0F, -10.0F);
        
        this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount,
                ageInTicks, netHeadYaw, headPitch, scale);
        
        GlStateManager.disablePolygonOffset();
        GlStateManager.color(1,1,1,1);
    }
    
    @Override
    public boolean shouldCombineTextures() { return false; }
}

// TODO Pri arachnida, Ada longarms viral, Ada arachnida nails, Warden, Marauder base, Grunt
