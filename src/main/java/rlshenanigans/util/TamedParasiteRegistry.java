package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import rlshenanigans.data.TamedParasiteData;

import java.util.*;
import java.util.stream.Collectors;

public class TamedParasiteRegistry {
    
    public static void track(EntityParasiteBase mob, EntityPlayer owner) {
        TamedParasiteData data = getOverworldData();
        
        data.getAll().removeIf(info ->
                info.mobUUID.equals(mob.getUniqueID()) ||
                        (info.ownerId.equals(owner.getUniqueID()) && info.name.equalsIgnoreCase(mob.getCustomNameTag()))
        );
        
        data.getAll().add(new TamedParasiteInfo(mob, owner));
        data.markDirty();
    }
    
    public static void untrack(UUID mobId) {
        TamedParasiteData data = getOverworldData();
        data.getAll().removeIf(info -> info.mobUUID.equals(mobId));
        data.markDirty();
    }
    
    public static List<TamedParasiteInfo> getOwnedBy(UUID playerId) {
        TamedParasiteData data = getOverworldData();
        return data.getAll().stream()
                .filter(info -> info.ownerId != null && info.ownerId.equals(playerId))
                .collect(Collectors.toList());
    }
    
    public static void clear(UUID playerId) {
        TamedParasiteData data = getOverworldData();
        data.getAll().removeIf(info -> info.ownerId != null && info.ownerId.equals(playerId));
        data.markDirty();
    }
    
    private static TamedParasiteData getOverworldData() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        World overworld = server != null ? server.getWorld(0) : null;
        if (overworld == null) {
            //throw new IllegalStateException("Overworld is not loaded or server is unavailable.");
        }
        return TamedParasiteData.get(overworld);
    }
}