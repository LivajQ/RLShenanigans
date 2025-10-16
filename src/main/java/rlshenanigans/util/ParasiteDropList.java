package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.*;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import rlshenanigans.handlers.RLSEntityHandler;
import rlshenanigans.item.ItemPaintingSpawner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class ParasiteDropList {
    private static final List<ItemPaintingSpawner> PAINTINGS = new ArrayList<>(RLSEntityHandler.PAINTING_ITEMS.values());
    
    private static final List<DropEntry> SPECIAL_DROPS = Arrays.asList(
            new DropEntry("srparasites", "itemedevolve", 10),
            new DropEntry("srparasites", "itemassimilate", 20),
            new DropEntry("srparasites", "itemevolve", 34),
            new DropEntry("srparasites", "itemvariant", 100),
            new DropEntry("rlshenanigans", "amulet_sin_lust", 100)
    );
    
    private static class DropEntry {
        final String modid;
        final String id;
        final int oneInX;
        
        DropEntry(String modid, String id, int oneInX) {
            this.modid = modid;
            this.id = id;
            this.oneInX = oneInX;
        }
    }
    
    public static ItemStack getDrops(EntityParasiteBase parasite) {
        for (DropEntry entry : SPECIAL_DROPS) {
            if (RLSRAND.nextInt(entry.oneInX) == 0) return drop(entry.modid, entry.id, 1);
        }
        
        if (RLSRAND.nextInt(20) == 0 && !PAINTINGS.isEmpty()) {
            ItemPaintingSpawner painting = PAINTINGS.get(RLSRAND.nextInt(PAINTINGS.size()));
            return new ItemStack(painting);
        }
        
        if (parasite instanceof EntityOronco || parasite instanceof EntityTerla || parasite instanceof EntityInfDragonE)
            return drop("contenttweaker", "blood_tear", 1);
        if (parasite instanceof EntityPInfected || parasite instanceof EntityPFeral)
            return drop("srparasites", "assimilated_flesh", 1);
        if (parasite instanceof EntityShyco || parasite instanceof EntityShycoAdapted)
            return drop("srparasites", "ada_longarms_drop", 1);
        if (parasite instanceof EntityCanra || parasite instanceof EntityCanraAdapted)
            return drop("srparasites", "ada_summoner_drop", 1);
        if (parasite instanceof EntityNogla || parasite instanceof EntityNoglaAdapted)
            return drop("srparasites", "ada_reeker_drop", 1);
        if (parasite instanceof EntityHull || parasite instanceof EntityHullAdapted)
            return drop("srparasites", "ada_manducater_drop", 1);
        if (parasite instanceof EntityEmana || parasite instanceof EntityEmanaAdapted)
            return drop("srparasites", "ada_yelloweye_drop", 1);
        
        return null;
    }
    
    private static Item get(String modid, String id) {
        return Item.REGISTRY.getObject(new ResourceLocation(modid, id));
    }
    
    private static ItemStack drop(String modid, String id, int count) {
        Item item = get(modid, id);
        return item != null ? new ItemStack(item, count) : ItemStack.EMPTY;
    }
}