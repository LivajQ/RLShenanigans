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

import java.util.Random;

public class ParasiteDropList {
    private static final Random rand = new Random();
    
    public static ItemStack getDrops(EntityParasiteBase parasite) {
        if (rand.nextInt(10) == 0) return drop("srparasites", "itemedevolve", 1);
        if (rand.nextInt(20) == 0) return drop("srparasites", "itemassimilate", 1);
        if (rand.nextInt(34) == 0) return drop("srparasites", "itemevolve", 1);
        if (rand.nextInt(100) == 0) return drop("srparasites", "itemvariant", 1);
        if (rand.nextInt(100) == 0) return drop("rlshenanigans", "amulet_sin_lust", 1);
        
        if (parasite instanceof EntityOronco || parasite instanceof EntityTerla || parasite instanceof EntityInfDragonE) return drop("contenttweaker", "blood_tear", 1);
        if (parasite instanceof EntityPInfected) return drop("srparasites", "assimilated_flesh", 1);
        if (parasite instanceof EntityPFeral) return drop("srparasites", "assimilated_flesh", 1);
        if (parasite instanceof EntityShyco || parasite instanceof EntityShycoAdapted) return drop("srparasites", "ada_longarms_drop", 1);
        if (parasite instanceof EntityCanra || parasite instanceof EntityCanraAdapted) return drop("srparasites", "ada_summoner_drop", 1);
        if (parasite instanceof EntityNogla || parasite instanceof EntityNoglaAdapted) return drop("srparasites", "ada_reeker_drop", 1);
        if (parasite instanceof EntityHull || parasite instanceof EntityHullAdapted) return drop("srparasites", "ada_manducater_drop", 1);
        if (parasite instanceof EntityEmana || parasite instanceof EntityEmanaAdapted) return drop("srparasites", "ada_yelloweye_drop", 1);
        
        return null;
    }
    
    public static Item get(String modid, String id) {
        return Item.REGISTRY.getObject(new ResourceLocation(modid, id));
    }
    
    public static ItemStack drop(String modid, String id, int count) {
        Item item = get(modid, id);
        return item != null ? new ItemStack(item, count) : ItemStack.EMPTY;
    }
}