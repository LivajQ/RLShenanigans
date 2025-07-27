package rlshenanigans.handlers;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.creature.EntityDrJr;

public class RLSEntityHandler
{
    public static final int drJrEntityID = 200;
    public static void init()
    {
        
        EntityRegistry.registerModEntity(new ResourceLocation(RLShenanigans.MODID, "drjr"), EntityDrJr.class,
                "drjr", drJrEntityID, RLShenanigans.instance, 64, 3, true);
        EntityRegistry.registerEgg(new ResourceLocation(RLShenanigans.MODID, "drjr"), 0x00AA00, 0x005500);
        
        for (Biome biome : Biome.REGISTRY)
        {
            EntityRegistry.addSpawn(EntityDrJr.class, 5, 1, 1, EnumCreatureType.MONSTER, biome);
        }
    }
}