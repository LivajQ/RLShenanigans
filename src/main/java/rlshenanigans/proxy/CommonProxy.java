package rlshenanigans.proxy;

import com.charles445.rltweaker.config.ModConfig;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.CreatureConfig;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureSpawnConfig;
import com.lycanitesmobs.core.info.ModInfo;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.spartanweaponry.PropertyInjector;
import rlshenanigans.util.TameableMiscWhitelist;
import rlshenanigans.util.WeaponRegistry;

public class CommonProxy {
    public static ModInfo modInfo;
    
    public void preInit() {
        modInfo = new ModInfo(RLShenanigans.instance, "RLShenanigans", 999);
        ObjectManager.setCurrentModInfo(modInfo);
        CreatureManager.getInstance().startup(modInfo);
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
        
        WeaponRegistry.scanWeapons();

        PropertyInjector.injectProperties();
    }
    
    public void registerRenderers() {
    }
}