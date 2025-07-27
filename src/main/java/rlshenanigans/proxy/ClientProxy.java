package rlshenanigans.proxy;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import com.lycanitesmobs.client.model.creature.ModelRahovart;
import net.minecraft.client.model.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import org.lwjgl.input.Keyboard;
import rlshenanigans.client.model.creature.ModelDrJr;
import rlshenanigans.client.render.RenderParasiteEntity;
import rlshenanigans.client.render.RenderRLSModel;
import rlshenanigans.client.visual.ParticlePulseScheduler;
import rlshenanigans.entity.creature.EntityDrJr;
import rlshenanigans.entity.creature.EntityRahovartTamed;
import rlshenanigans.handlers.ModRegistry;
import rlshenanigans.models.ModelExampleArmor;
import rlshenanigans.util.ParasiteRegistry;

import java.util.HashMap;
import java.util.Map;

import static rlshenanigans.util.ParasiteRegistry.PARASITES;

public class ClientProxy extends CommonProxy {
    
    public static final KeyBinding keyAscend = new KeyBinding("key.rls.ascend", Keyboard.KEY_SPACE, "key.categories.rls");
    public static final KeyBinding keyDescend = new KeyBinding("key.rls.descend", Keyboard.KEY_X, "key.categories.rls");
    public static final KeyBinding keyProjectile = new KeyBinding("key.rls.projectile", Keyboard.KEY_R, "key.categories.rls");
    public static final KeyBinding keyTeleport = new KeyBinding("key.rls.teleport", Keyboard.KEY_N, "key.categories.rls");
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
        
        RenderingRegistry.registerEntityRenderingHandler(EntityDrJr.class, manager ->
                new RenderRLSModel<>(manager, new ModelDrJr(), 0.5F,
                        new ResourceLocation("rlshenanigans", "textures/entity/creature/drjr.png")
                )
        );
        
        RenderingRegistry.registerEntityRenderingHandler(EntityRahovartTamed.class, manager ->
                new RenderRLSModel<>(manager, new ModelRahovart(), 0.5F,
                        new ResourceLocation("rlshenanigans", "textures/entity/creature/rahovarttamed.png")  //maybe sth custom later idk
                )
        );
    }
    
    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(ParticlePulseScheduler.class);
        ClientRegistry.registerKeyBinding(keyAscend);
        ClientRegistry.registerKeyBinding(keyDescend);
        ClientRegistry.registerKeyBinding(keyProjectile);
        ClientRegistry.registerKeyBinding(keyTeleport);
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