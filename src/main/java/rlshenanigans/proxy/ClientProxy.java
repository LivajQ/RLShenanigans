package rlshenanigans.proxy;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import com.lycanitesmobs.client.model.creature.ModelAmalgalich;
import com.lycanitesmobs.client.model.creature.ModelAsmodeus;
import com.lycanitesmobs.client.model.creature.ModelRahovart;
import com.lycanitesmobs.client.renderer.RenderRegister;
import net.minecraft.client.model.*;
import net.minecraft.client.settings.KeyBinding;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import org.lwjgl.input.Keyboard;

import rlshenanigans.client.model.ModelCube;
import rlshenanigans.client.model.creature.ModelDrJr;
import rlshenanigans.client.render.*;
import rlshenanigans.client.visual.ParticlePulseScheduler;
import rlshenanigans.entity.EntitySpellInvulnerability;
import rlshenanigans.entity.creature.EntityAmalgalichTamed;
import rlshenanigans.entity.creature.EntityAsmodeusTamed;
import rlshenanigans.entity.creature.EntityDrJr;
import rlshenanigans.entity.creature.EntityRahovartTamed;
import rlshenanigans.entity.item.EntityPaintingTemplate;
import rlshenanigans.entity.npc.EntityNPCBase;
import rlshenanigans.handlers.RLSEntityHandler;
import rlshenanigans.item.ItemPaintingSpawner;
import rlshenanigans.util.ParasiteRegistry;

import static rlshenanigans.util.ParasiteRegistry.PARASITES;

public class ClientProxy extends CommonProxy {
    
    public static final KeyBinding keyAscend = new KeyBinding("key.rls.ascend", Keyboard.KEY_SPACE, "key.categories.rls");
    public static final KeyBinding keyDescend = new KeyBinding("key.rls.descend", Keyboard.KEY_X, "key.categories.rls");
    public static final KeyBinding keyProjectile = new KeyBinding("key.rls.projectile", Keyboard.KEY_R, "key.categories.rls");
    public static final KeyBinding keyTeleport = new KeyBinding("key.rls.teleport", Keyboard.KEY_N, "key.categories.rls");
    
    @SuppressWarnings("unchecked")
    @Override
    public void registerRenderers() {
        for (ParasiteRegistry mob : PARASITES) {
            try {
                String entityClassName = "com.dhanantry.scapeandrunparasites.entity.monster." + mob.category + ".Entity" + mob.name;
                String modelClassName  = "com.dhanantry.scapeandrunparasites.client.model.entity." + mob.category + ".Model" + mob.name;
                if (mob.name.equals("Crux")) modelClassName += "A";
                
                Class<? extends EntityParasiteBase> entityClass = (Class<? extends EntityParasiteBase>) Class.forName(entityClassName);
                Class<? extends ModelBase> modelClass = (Class<? extends ModelBase>) Class.forName(modelClassName);
                ModelBase modelInstance = modelClass.getDeclaredConstructor().newInstance();
                
                RenderingRegistry.registerEntityRenderingHandler(entityClass, manager ->
                        new RenderParasiteEntity(manager, modelInstance, 0.5F, mob.texture)
                );
                
                
            } catch (Exception e) {
                System.err.println("Renderer failed for: " + mob.name + " → " + e.getMessage());
            }
        }
        
        RenderingRegistry.registerEntityRenderingHandler(EntityPaintingTemplate.class, RenderRLSEntityItem::new);
        
        RenderingRegistry.registerEntityRenderingHandler(EntityDrJr.class, manager ->
                new RenderRLSModel<>(manager, new ModelDrJr(), 0.5F,
                        new ResourceLocation("rlshenanigans", "textures/entity/creature/drjr.png")
                )
        );
        
        RenderingRegistry.registerEntityRenderingHandler(EntityNPCBase.class, manager ->
                new RenderNPC(manager, new ModelPlayer(0, false), 0.5F,
                        new ResourceLocation("rlshenanigans", "textures/entity/creature/dummy.png")
                )
        );
        
        RenderingRegistry.registerEntityRenderingHandler(EntityRahovartTamed.class, manager ->
                new RenderRLSModel<>(manager, new ModelRahovart(), 0.5F,
                        new ResourceLocation("lycanitesmobs", "textures/entity/rahovart.png")  //maybe sth custom later idk
                )
        );
        
        RenderingRegistry.registerEntityRenderingHandler(EntityAmalgalichTamed.class, manager ->
                new RenderRLSModel<>(manager, new ModelAmalgalich(), 0.5F,
                        new ResourceLocation("lycanitesmobs", "textures/entity/amalgalich.png")
                )
        );
        
        RenderingRegistry.registerEntityRenderingHandler(EntityAsmodeusTamed.class, manager ->
                new RenderRLSModel<>(manager, new ModelAsmodeus(), 0.5F,
                        new ResourceLocation("lycanitesmobs", "textures/entity/asmodeus.png")
                )
        );
        
        RenderingRegistry.registerEntityRenderingHandler(EntitySpellInvulnerability.class, manager ->
                new RenderSpellEntity<>(manager, new ModelCube(), new ResourceLocation("rlshenanigans",  "textures/misc/spell_invulnerability.png"))
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
        for (ItemPaintingSpawner item : RLSEntityHandler.PAINTING_ITEMS.values()) {
            item.setTileEntityItemStackRenderer(new RenderRLSItemPainting());
        }
    }

    @Override
    public void preInit() {
        super.preInit();
        RenderRegister renderRegister = new RenderRegister(modInfo);
        renderRegister.registerRenderFactories();
        registerRenderers();
    }
}