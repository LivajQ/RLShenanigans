package rlshenanigans.client.render;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;

import net.minecraft.util.ResourceLocation;

public class RenderParasiteEntity extends RenderLiving<EntityParasiteBase> {
    
    private final String parasiteName;
    
    public RenderParasiteEntity(RenderManager manager, ModelBase model, float shadowSize, String parasiteName) {
        super(manager, model, shadowSize);
        this.parasiteName = parasiteName;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityParasiteBase entity) {
        String name = parasiteName.toLowerCase();
        String folder = entity.hasCustomName() ? "thh" : "normal";
        
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
        if (entity instanceof EntityShycoAdapted) {
            GlStateManager.scale(0.4F, 0.4F, 0.4F); // test shrink
        }
    }
}