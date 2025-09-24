package rlshenanigans.handlers;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.creature.EntityDrJr;
import rlshenanigans.entity.item.EntityPaintingTemplate;
import rlshenanigans.entity.npc.EntityNPCGeneric;
import rlshenanigans.entity.npc.EntityNPCInvader;
import rlshenanigans.entity.npc.EntityNPCSummon;
import rlshenanigans.item.ItemPaintingSpawner;
import rlshenanigans.proxy.CommonProxy;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RLSEntityHandler
{
    public static final int drJrEntityID = 200;
    public static final int npcGenericID = 201;
    public static final int npcSummonID = 202;
    public static final int npcInvaderID = 203;
    public static final int paintingID = 300;
    
    //texture path, frame count, internal name
    private static final Object[][] PAINTING_INFO = new Object[][] {
            {"textures/entity/item/painting_template_parasite1", 1, "parasite_1"},
            {"textures/entity/item/painting_template_parasite2", 1, "parasite_2"},
            {"textures/entity/item/painting_template_parasite3", 1, "parasite_3"},
            {"textures/entity/item/painting_template_parasite4", 1, "parasite_4"},
            {"textures/entity/item/painting_template_parasite5", 1, "parasite_5"},
            {"textures/entity/item/painting_template_animated/grueshake/painting_template_grueshake", 79, "grueshake"},
            {"textures/entity/item/painting_template_animated/gruenod/painting_template_gruenod", 63, "gruenod"},
            {"textures/entity/item/painting_template_animated/lgruenod/painting_template_lgruenod", 63, "lgruenod"}
    };
    
    public static final Map<String, ItemPaintingSpawner> PAINTING_ITEMS = new HashMap<>();
    
    public static void init() {
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "drjr"), EntityDrJr.class,
                "drjr", drJrEntityID, RLShenanigans.instance, 64, 3, true);
        EntityRegistry.registerEgg(new ResourceLocation(RLShenanigans.MODID, "drjr"), 0x00AA00, 0x005500);
        
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "npc_generic"), EntityNPCGeneric.class,
                "npc_generic", npcGenericID, RLShenanigans.instance, 64, 3, true);
        EntityRegistry.registerEgg(new ResourceLocation(RLShenanigans.MODID, "npc_generic"), 0x708090, 0x505050);
        
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "npc_summon"), EntityNPCSummon.class,
                "npc_summon", npcSummonID, RLShenanigans.instance, 64, 3, true);
        EntityRegistry.registerEgg(new ResourceLocation(RLShenanigans.MODID, "npc_summon"), 0xFFD700, 0xFFFFAA);
        
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "npc_invader"), EntityNPCInvader.class,
                "npc_invader", npcInvaderID, RLShenanigans.instance, 64, 3, true);
        EntityRegistry.registerEgg(new ResourceLocation(RLShenanigans.MODID, "npc_invader"), 0xAA0000, 0x330000);
        
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "painting_template"), EntityPaintingTemplate.class,
                "painting_template", paintingID, RLShenanigans.instance, 64, 3, false);
        
        for (Biome biome : Biome.REGISTRY) {
            if (ForgeConfigHandler.misc.drJrEnabled) EntityRegistry.addSpawn(EntityDrJr.class, 5, 1, 1, EnumCreatureType.MONSTER, biome);
            if (ForgeConfigHandler.npc.npcEnabled) EntityRegistry.addSpawn(EntityNPCGeneric.class, 2, 1, 1, EnumCreatureType.CREATURE, biome);
        }
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Object[] objects : PAINTING_INFO) {
            String texture = (String) objects[0];
            int frames = (Integer) objects[1];
            String suffix = (String) objects[2];
            
            ItemPaintingSpawner item = new ItemPaintingSpawner(texture, frames, suffix);
            item.setRegistryName(new ResourceLocation(RLShenanigans.MODID, "painting_" + suffix));
            item.setTranslationKey("painting_" + suffix);
            event.getRegistry().register(item);
            PAINTING_ITEMS.put(suffix, item);
        }
    }
    
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (ItemPaintingSpawner item : RLSEntityHandler.PAINTING_ITEMS.values()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                    new ModelResourceLocation(RLShenanigans.MODID + ":painting_template", "inventory"));
        }
    }
    
    @SubscribeEvent
    public static void registerLycaniteEntities(RegistryEvent.Register<EntityEntry> event) {
        ModInfo modInfo = CommonProxy.modInfo;
        for (CreatureInfo creatureInfo : CreatureManager.getInstance().creatures.values()) {
            if (creatureInfo.modInfo != modInfo) continue;
            try {
                EntityEntry entityEntry = EntityEntryBuilder.create()
                        .entity(creatureInfo.entityClass)
                        .id(creatureInfo.getEntityId(), CreatureManager.getInstance().getNextCreatureNetworkId())
                        .name(creatureInfo.getName())
                        .tracker(creatureInfo.isBoss() ? 160 : 80, 3, false)
                        .build();
                event.getRegistry().register(entityEntry);
            } catch (Exception e) {
                LycanitesMobs.logWarning("", "Unable to find entity class for: " + creatureInfo.getName() + ".");
                throw e;
            }
        }
    }
}