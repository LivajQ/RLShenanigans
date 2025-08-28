package rlshenanigans.handlers;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.creature.EntityDrJr;
import rlshenanigans.tileentity.TileEntityPaintingTemplate;

public class RLSEntityHandler
{
    public static final int drJrEntityID = 200;
    public static void init() {
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "drjr"), EntityDrJr.class,
                "drjr", drJrEntityID, RLShenanigans.instance, 64, 3, true);
        EntityRegistry.registerEgg(new ResourceLocation(RLShenanigans.MODID, "drjr"), 0x00AA00, 0x005500);
        
        for (Biome biome : Biome.REGISTRY) {
            if(ForgeConfigHandler.misc.drJrEnabled) EntityRegistry.addSpawn(EntityDrJr.class, 5, 1, 1, EnumCreatureType.MONSTER, biome);
        }
        
        GameRegistry.registerTileEntity(TileEntityPaintingTemplate.class, new ResourceLocation("rlshenanigans", "painting_template_tile"));
    }
}