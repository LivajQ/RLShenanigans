package rlshenanigans.proxy;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import net.minecraft.client.model.*;
import net.minecraft.item.Item;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import rlshenanigans.client.render.RenderParasiteEntity;
import rlshenanigans.client.visual.ParticlePulseScheduler;
import rlshenanigans.handlers.ModRegistry;
import rlshenanigans.handlers.ParasiteMovementListener;
import rlshenanigans.handlers.RideParasiteHandler;
import rlshenanigans.handlers.TeleportParasiteHandler;
import rlshenanigans.models.ModelExampleArmor;
import rlshenanigans.util.ParasiteRegistry;

import java.util.HashMap;
import java.util.Map;

import static rlshenanigans.util.ParasiteRegistry.PARASITES;

public class ClientProxy extends CommonProxy {
    
    private static final ModelExampleArmor exampleArmor = new ModelExampleArmor(1.0F);
    private static final ModelExampleArmor exampleArmorLegs = new ModelExampleArmor(0.5F);
    
    private static final Map<Item, ModelBiped> exampleArmorModels = new HashMap<>();
    
    @Override
    public void registerRenderers() {
        for (ParasiteRegistry mob : PARASITES) {
            try {
                String entityClassName = "com.dhanantry.scapeandrunparasites.entity.monster." + mob.category + ".Entity" + mob.name;
                String modelClassName  = "com.dhanantry.scapeandrunparasites.client.model.entity." + mob.category + ".Model" + mob.name;
                String texturePath = "srparasites:textures/entity/monster/normal/" + mob.name.toLowerCase() + ".png";
                
                Class<? extends EntityParasiteBase> entityClass = (Class<? extends EntityParasiteBase>) Class.forName(entityClassName);
                Class<? extends ModelBase> modelClass = (Class<? extends ModelBase>) Class.forName(modelClassName);
                ModelBase modelInstance = modelClass.getDeclaredConstructor().newInstance();
                
                RenderingRegistry.registerEntityRenderingHandler(entityClass, manager ->
                        new RenderParasiteEntity(manager, modelInstance, 0.5F, mob.name)
                );
                
                
            } catch (Exception e) {
                System.err.println("Renderer failed for: " + mob.name + " → " + e.getMessage());
            }
        }
        
    }
    
    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(ParticlePulseScheduler.class);
        MinecraftForge.EVENT_BUS.register(new ParasiteMovementListener());
        ClientRegistry.registerKeyBinding(RideParasiteHandler.keyAscend);
        ClientRegistry.registerKeyBinding(RideParasiteHandler.keyDescend);
        ClientRegistry.registerKeyBinding(RideParasiteHandler.keyProjectile);
        ClientRegistry.registerKeyBinding(TeleportParasiteHandler.keyTeleport);
    }

    @Override
    public void preInit() {
        
        registerRenderers();
        // Register custom armor models
        exampleArmorModels.put(ModRegistry.exampleHelmet, exampleArmor);
        exampleArmorModels.put(ModRegistry.exampleChestplate, exampleArmor);
        exampleArmorModels.put(ModRegistry.exampleLeggings, exampleArmorLegs);
        exampleArmorModels.put(ModRegistry.exampleBoots, exampleArmor);
    }
    
    
    @Override
    public Map<Item, ModelBiped> getExampleArmor() {
        return exampleArmorModels;
    }
}