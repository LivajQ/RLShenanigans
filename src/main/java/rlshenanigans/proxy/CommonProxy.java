package rlshenanigans.proxy;

import com.charles445.rltweaker.config.ModConfig;
import com.lycanitesmobs.core.info.CreatureConfig;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureSpawnConfig;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.util.TameableMiscWhitelist;

public class CommonProxy {

    public void preInit() {
    }
    public void init() {
        com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler.enchantments.vampirismLevel = 3;
    }
    
    public void postInit() {
        CreatureSpawnConfig spawnConfig = CreatureManager.getInstance().spawnConfig;
        CreatureConfig config = CreatureManager.getInstance().config;
        
        spawnConfig.dimensionList = new int[0];
        spawnConfig.dimensionListWhitelist = false;
        config.soulboundDimensionList = new int[0];
        config.soulboundDimensionListWhitelist = false;
        config.summonDimensionList = new int[0];
        config.summonDimensionListWhitelist = false;
        
        ModConfig.server.srparasites.parasitesDimensionBlacklistEnabled = false;
        
        TameableMiscWhitelist.load(ForgeConfigHandler.misc.tameableMiscEntries);
    }
    
    public void registerRenderers() {
    }
}