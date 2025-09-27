package rlshenanigans.handlers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.npc.EntityNPCInvader;
import rlshenanigans.entity.npc.EntityNPCSummon;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class NPCInvasionHandler {
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (!ForgeConfigHandler.npc.invasionsEnabled
                || player.world.isRemote
                || player.capabilities.isCreativeMode
                || player.ticksExisted < 6000
                || player.ticksExisted % 200 != 0
                || event.phase == TickEvent.Phase.START) return;
        
        NBTTagCompound data = player.getEntityData();
        long now = player.world.getTotalWorldTime();
        if (!data.hasKey("NextInvasion")) data.setLong("NextInvasion", now + rollNextInvasion());
        if (data.getLong("NextInvasion") < now && nearbyInvaders(player).isEmpty()) spawnInvader(player, 100);
    }
    
    @SubscribeEvent
    public static void onInvaderDeath(LivingDeathEvent event) {
        if (!ForgeConfigHandler.npc.invasionsEnabled) return;
        if(!(event.getEntity() instanceof EntityNPCInvader)) return;
        EntityNPCInvader invader = (EntityNPCInvader) event.getEntity();
        if (invader.getInvadedPlayerUUID() == null) return;
        EntityPlayer player = invader.world.getPlayerEntityByUUID(invader.getInvadedPlayerUUID());
        if (player == null) return;
        
        double distanceSq = invader.getDistanceSq(player);
        if (distanceSq > 128 * 128) player.getEntityData().setLong("NextInvasion", player.world.getTotalWorldTime() + rollNextInvasion());
    }
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!ForgeConfigHandler.npc.invasionsEnabled) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntity();
        long now = player.world.getTotalWorldTime();
        if (player.getEntityData().getLong("NextInvasion") >= now) return;
        
        List<EntityNPCInvader> invaders = nearbyInvaders(player);
        boolean matched = invaders.stream().anyMatch(invader -> player.getUniqueID().equals(invader.getInvadedPlayerUUID()));
        if (matched) player.getEntityData().setLong("NextInvasion", now + rollNextInvasion());
    }
    
    @SubscribeEvent
    public static void onSetAttackTarget(LivingSetAttackTargetEvent event) {
        if(!(event.getEntity() instanceof EntityLiving)) return;
        EntityLiving attacker = (EntityLiving) event.getEntity();
        if (!(event.getTarget() instanceof EntityNPCInvader)) return;
        EntityNPCInvader invader = (EntityNPCInvader) event.getTarget();
        UUID invadedUUID = invader.getInvadedPlayerUUID();
        if (invadedUUID == null) return;
        EntityPlayer player = invader.world.getPlayerEntityByUUID(invadedUUID);
        if (player == null) return;
        
        boolean canTargetInvader = attacker.getEntityData().hasUniqueId("OwnerUUID");
        
        if (attacker instanceof EntityNPCSummon) canTargetInvader = true;
        if (CombatAssistHandler.isEntityTamed(attacker)) canTargetInvader = true;
        
        if(!canTargetInvader) attacker.setAttackTarget(null);
    }
    
    public static void spawnInvader(EntityPlayer player, int phantomFadeTime) {
        EntityNPCInvader invader = new EntityNPCInvader(player.world, player.getUniqueID(), phantomFadeTime);
        BlockPos playerPos = new BlockPos(player);
        if (!invader.spawnInRadius(invader.world, playerPos, 24, 32)) return;
        DifficultyInstance difficulty = player.world.getDifficultyForLocation(playerPos);
        invader.onInitialSpawn(difficulty, null);
    }
    
    private static List<EntityNPCInvader> nearbyInvaders(EntityPlayer player) {
        AxisAlignedBB checkArea = new AxisAlignedBB(
                player.posX - 128, player.posY - 64, player.posZ - 128,
                player.posX + 128, player.posY + 64, player.posZ + 128
        );
        
        return player.world.getEntitiesWithinAABB(EntityNPCInvader.class, checkArea);
    }
    
    private static long rollNextInvasion() {
        int base = ForgeConfigHandler.npc.invasionCooldown;
        int offset = ForgeConfigHandler.npc.invasionOffset;
        int min = Math.max(0, base - offset);
        int max = base + offset;
        return min + new Random().nextInt(max - min + 1);
    }
}