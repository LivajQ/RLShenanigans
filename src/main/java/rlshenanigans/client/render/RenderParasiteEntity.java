package rlshenanigans.client.render;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;

import net.minecraft.util.ResourceLocation;
import rlshenanigans.handlers.ForgeConfigHandler;

public class RenderParasiteEntity extends RenderLiving<EntityParasiteBase> {
    private final String texture;
    
    public RenderParasiteEntity(RenderManager manager, ModelBase model, float shadowSize, String texture) {
        super(manager, model, shadowSize);
        this.texture = texture;
    }
    @Override
    protected ResourceLocation getEntityTexture(EntityParasiteBase entity) {
        String folder = entity.hasCustomName() ? "thh/" : "";
        if(!ForgeConfigHandler.client.thhEnabled) folder = "";
        
        String suffix = "";
        switch (entity.getSkin()) {
            case 1: suffix = "sp1"; break; //armored
            case 2: suffix = "1"; break; //
            case 3: suffix = "2"; break; // some skin rng for humans and villagers
            case 5: suffix = "v"; break; // viral
            case 6: suffix = "b"; break; // bleed
            case 7: suffix = "h"; break; // breacher
            
            default: suffix = "";
        }
        
        if (texture.equals("shycoa") && entity.getSkin() == 1) suffix = "tyrant";
        if (texture.equals("mes") && entity.getSkin() == 1) suffix = "1";
        
        String path = "srparasites:textures/entity/monster/" + folder + texture + suffix + ".png";
        
        return new ResourceLocation(path);
    }
    
    @Override
    protected void preRenderCallback(EntityParasiteBase entity, float partialTickTime) {
        if (!entity.hasCustomName()) return;
        float sizeMultiplier = entity.getEntityData().getFloat("SizeMultiplier");
        if(sizeMultiplier < 0.25F) sizeMultiplier = 1.0F;
        GlStateManager.scale(sizeMultiplier, sizeMultiplier, sizeMultiplier);
    }
}