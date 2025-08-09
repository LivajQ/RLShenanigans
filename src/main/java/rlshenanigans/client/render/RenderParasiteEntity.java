package rlshenanigans.client.render;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;

import net.minecraft.util.ResourceLocation;
import rlshenanigans.handlers.ForgeConfigHandler;

public class RenderParasiteEntity extends RenderLiving<EntityParasiteBase> {
    private final String parasiteNameLower;
    
    public RenderParasiteEntity(RenderManager manager, ModelBase model, float shadowSize, String parasiteName) {
        super(manager, model, shadowSize);
        this.parasiteNameLower = parasiteName.toLowerCase();
    }
    @Override
    protected ResourceLocation getEntityTexture(EntityParasiteBase entity) {
        String name = parasiteNameLower;
        String folder = entity.hasCustomName() ? "thh" : "normal";
        if(!ForgeConfigHandler.client.thhEnabled) folder = "normal";
        
        String suffix = "";
        switch (entity.getSkin()) {
            case 1: suffix = "sp1"; break; //tyrant longarms, armored haunter etc.
            case 2: suffix = ""; break; //
            case 3: suffix = ""; break; // some skin rng for humans and villagers, buggy and frankly whatever
            case 5: suffix = "v"; break; // viral
            case 6: suffix = "b"; break; // bleed
            case 7: suffix = "h"; break; // breacher
            
            default: suffix = "";
        }
        
        String path = "srparasites:textures/entity/monster/" + folder + "/" + name + suffix + ".png";
        
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