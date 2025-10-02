package rlshenanigans.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import rlshenanigans.entity.npc.EntityNPCSummon;

public class NPCSummonHandler {
    public static void spawnSummon(EntityPlayer player, int phantomFadeTime) {
        EntityNPCSummon summon = new EntityNPCSummon(player.world, player.getUniqueID(), phantomFadeTime);
        BlockPos playerPos = new BlockPos(player);
        if (!summon.spawnInRadius(summon.world, playerPos, 2, 5, false)) return;
        DifficultyInstance difficulty = player.world.getDifficultyForLocation(playerPos);
        summon.onInitialSpawn(difficulty, null);
    }
}