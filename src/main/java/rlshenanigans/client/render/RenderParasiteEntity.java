package rlshenanigans.client.render;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.handlers.ParasiteRandomRenderHandler;

public class RenderParasiteEntity extends RenderLiving<EntityParasiteBase> {
    private final String texture;
    
    public RenderParasiteEntity(RenderManager manager, ModelBase model, float shadowSize, String texture) {
        super(manager, model, shadowSize);
        this.texture = texture;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityParasiteBase entity) {
        
        String suffix = getSuffixForSkin(entity);

        String path = "srparasites:textures/entity/monster/" + texture + suffix + ".png";
        return new ResourceLocation(path);
    }
    
    @Override
    protected void preRenderCallback(EntityParasiteBase entity, float partialTickTime) {
        if (!entity.hasCustomName()) return;
        float sizeMultiplier = entity.getEntityData().getFloat("SizeMultiplier");
        if(sizeMultiplier < 0.25F) sizeMultiplier = 1.0F;
        GlStateManager.scale(sizeMultiplier, sizeMultiplier, sizeMultiplier);
    }
    
    @Override
    protected void renderModel(EntityParasiteBase parasite, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        ParasiteRandomRenderHandler.init(parasite, this.mainModel);
        
        if (ForgeConfigHandler.client.rainbowThighs == 2 || ForgeConfigHandler.client.rainbowThighs == 3)
            applyRainbowColor(parasite, ageInTicks, 0.0F);
        
        super.renderModel(parasite, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
    
        GlStateManager.color(1,1,1,1);
    }
        
    public String getSuffixForSkin(EntityParasiteBase entity) {
        String suffix;
        
        switch (entity.getSkin()) {
        case 1: suffix = "sp1"; break; // armored
        case 2: suffix = "1"; break; //
        case 3: suffix = "2"; break; //
        case 4: suffix = ""; break; // some skin rng for humans and villagers
        case 5: suffix = "v"; break; // viral
        case 6: suffix = "b"; break; // bleed
        case 7: suffix = "h"; break; // breacher
        
        default: suffix = "";
    }
    
    if (texture.equals("shycoa") && entity.getSkin() == 1) suffix = "tyrant";
    else if (texture.equals("mes") && entity.getSkin() == 1) suffix = "1";
    else if (texture.equals("human") && entity.getSkin() == 1) suffix = "1";
    else if (texture.equals("squid") && entity.getSkin() == 7) suffix = "";
    
    return suffix;
    }
    
    public String getTextureName() {
        return this.texture;
    }
    
    public static void applyRainbowColor(EntityParasiteBase entity, float partialTicks, float offset) {
        if (ForgeConfigHandler.client.rainbowThighs == 0) return;
        
        float time = (entity.ticksExisted + partialTicks) / 20.0F;
        
        float red   = 0.5F + 0.5F * (float)Math.sin(time + offset);
        float green = 0.5F + 0.5F * (float)Math.sin(time + 2.0F + offset);
        float blue  = 0.5F + 0.5F * (float)Math.sin(time + 4.0F + offset);
        
        GlStateManager.color(red, green, blue, 1.0F);
    }
}