package rlshenanigans.handlers;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.creature.EntityDrJr;
import rlshenanigans.entity.item.EntityPaintingTemplate;
import rlshenanigans.item.ItemPaintingSpawner;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RLSEntityHandler
{
    public static final int drJrEntityID = 200;
    public static final int paintingID = 300;
    
    //texture path, frame count, internal name
    private static final Object[][] PAINTING_INFO = new Object[][] {
            {"textures/entity/item/painting_template_parasite1", 1, "parasite_1"},
            {"textures/entity/item/painting_template_parasite2", 1, "parasite_2"},
            {"textures/entity/item/painting_template_parasite3", 1, "parasite_3"},
            {"textures/entity/item/painting_template_parasite4", 1, "parasite_4"},
            {"textures/entity/item/painting_template_parasite5", 1, "parasite_5"},
            {"textures/entity/item/painting_template_animated/grueshake/painting_template_grueshake", 79, "grueshake"}
    };
    
    public static void init() {
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "drjr"), EntityDrJr.class,
                "drjr", drJrEntityID, RLShenanigans.instance, 64, 3, true);
        EntityRegistry.registerEgg(new ResourceLocation(RLShenanigans.MODID, "drjr"), 0x00AA00, 0x005500);
        
        for (Biome biome : Biome.REGISTRY) {
            if(ForgeConfigHandler.misc.drJrEnabled) EntityRegistry.addSpawn(EntityDrJr.class, 5, 1, 1, EnumCreatureType.MONSTER, biome);
        }
        
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "painting_template"), EntityPaintingTemplate.class,
                "painting_template", paintingID, RLShenanigans.instance, 64, 3, false);
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (int i = 0; i < PAINTING_INFO.length; i++) {
            String texture = (String) PAINTING_INFO[i][0];
            int frames = (Integer) PAINTING_INFO[i][1];
            String suffix = (String) PAINTING_INFO[i][2];
            
            ItemPaintingSpawner item = new ItemPaintingSpawner(texture, frames);
            item.setRegistryName(new ResourceLocation(RLShenanigans.MODID, "painting_" + suffix));
            item.setTranslationKey("painting_" + suffix);
            event.getRegistry().register(item);
        }
    }
}