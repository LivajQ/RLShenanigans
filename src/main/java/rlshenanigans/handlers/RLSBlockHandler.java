package rlshenanigans.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.block.BlockPaintingTemplate;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RLSBlockHandler {
    
    //texturePath, frameCount, internalNameSuffix
    private static final Object[][] PAINTING_INFO = new Object[][] {
            {"textures/blocks/painting_template_parasite1", 1, "parasite_1"},
            {"textures/blocks/painting_template_parasite2", 1, "parasite_2"},
            {"textures/blocks/painting_template_parasite3", 1, "parasite_3"},
            {"textures/blocks/painting_template_parasite4", 1, "parasite_4"},
            {"textures/blocks/painting_template_animated/grueshake/painting_template_grueshake", 79, "grueshake"}
    };
    
    private static final BlockPaintingTemplate[] PAINTINGS = new BlockPaintingTemplate[PAINTING_INFO.length];
    
    public static void init() {
        for (int i = 0; i < PAINTING_INFO.length; i++) {
            String texture = (String) PAINTING_INFO[i][0];
            int frames = (Integer) PAINTING_INFO[i][1];
            String suffix = (String) PAINTING_INFO[i][2];
            
            PAINTINGS[i] = new BlockPaintingTemplate(texture, "painting_template_" + suffix, frames);
        }
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (int i = 0; i < PAINTINGS.length; i++) {
            String suffix = (String) PAINTING_INFO[i][2];
            PAINTINGS[i].setRegistryName("painting_template_" + suffix);
            event.getRegistry().register(PAINTINGS[i]);
        }
    }
    
    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        for (BlockPaintingTemplate painting : PAINTINGS) {
            event.getRegistry().register(new ItemBlock(painting).setRegistryName(painting.getRegistryName()));
        }
    }
}