package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;

public class ParasiteRandomRenderHandler {
    
    public static void init(EntityParasiteBase parasite, ModelBase model) {
        //if (model instanceof ModelInfEnderman) infEndermanSit((ModelInfEnderman) model);
    }
    
    public static void infEndermanSit(ModelInfEnderman model) {
        
        GlStateManager.translate(0.0F, 1.35F, 0.0F);
        
        model.jointLL0.rotateAngleX = (float)Math.toRadians(243.4F);
        model.jointLL1.rotateAngleX = (float)Math.toRadians(50.7F);
        model.jointLL2.rotateAngleX = (float)Math.toRadians(329.6F);
        
        model.jointRL0.rotateAngleX = (float)Math.toRadians(243.4F);
        model.jointRL1.rotateAngleX = (float)Math.toRadians(50.7F);
        model.jointRL2.rotateAngleX = (float)Math.toRadians(329.6F);
        
        model.jointLA0.rotateAngleX = (float)Math.toRadians(309.3F);
        model.jointLA1.rotateAngleX = 0.0F;
        model.jointLA2.rotateAngleX = (float)Math.toRadians(278.9F);
        
        model.jointLA0.rotateAngleY = 0.0F;
        model.jointLA1.rotateAngleY = (float)Math.toRadians(48.7F);
        model.jointLA2.rotateAngleY = 0.0F;
        
        model.jointLA0.rotateAngleZ = 0.0F;
        model.jointLA1.rotateAngleZ = 0.0F;
        model.jointLA2.rotateAngleZ = 0.0F;
        
        model.jointRA0.rotateAngleX = (float)Math.toRadians(309.3F);
        model.jointRA1.rotateAngleX = 0.0F;
        model.jointRA2.rotateAngleX = (float)Math.toRadians(278.9F);
        
        model.jointRA0.rotateAngleY = 0.0F;
        model.jointRA1.rotateAngleY = (float)Math.toRadians(294.1F);
        model.jointRA2.rotateAngleY = 0.0F;
        
        model.jointRA0.rotateAngleZ = 0.0F;
        model.jointRA1.rotateAngleZ = 0.0F;
        model.jointRA2.rotateAngleZ = 0.0F;
    }
}