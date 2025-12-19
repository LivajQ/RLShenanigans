package rlshenanigans.proxy;

import com.charles445.rltweaker.config.ModConfig;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.CreatureConfig;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureSpawnConfig;
import com.lycanitesmobs.core.info.ModInfo;
import com.tmtravlr.potioncore.PotionCoreEffects;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.spartanweaponry.PropertyInjector;
import rlshenanigans.util.TameableMiscWhitelist;
import rlshenanigans.util.WeaponRegistry;

import java.io.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CommonProxy {
    public static ModInfo modInfo;
    
    public void preInit(FMLPreInitializationEvent event) {
        modInfo = new ModInfo(RLShenanigans.instance, "RLShenanigans", 999);
        ObjectManager.setCurrentModInfo(modInfo);
        CreatureManager.getInstance().startup(modInfo);
        if (!RLShenanigans.marker.exists()) copyLycaniteCreatureConfigs(event);
        if (!ForgeConfigHandler.misc.flightPotionsEnabled) PotionCoreEffects.POTIONS.remove("flight");
    }
    
    public void init(FMLInitializationEvent event) {
        com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler.enchantments.vampirismLevel = 3;
    }
    
    public void postInit(FMLPostInitializationEvent event) {
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
    
    //replaces stale lycanite config files on first launch if loadDefault is set to false (which is indeed false in rlcraft) to prevent NPE
    private void copyLycaniteCreatureConfigs(FMLPreInitializationEvent event) {
        File targetDir = new File(event.getModConfigurationDirectory(), "lycanitesmobs/creatures");
        targetDir.mkdirs();
        
        File jarFile = event.getSourceFile();
        if (!jarFile.isFile()) return;
        
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                
                if (!name.startsWith("assets/rlshenanigans/creatures/")) continue;
                if (!name.endsWith(".json")) continue;
                if (entry.isDirectory()) continue;
                
                File outFile = new File(targetDir, name.substring(name.lastIndexOf('/') + 1));
                
                try (InputStream in = jar.getInputStream(entry);
                     OutputStream out = Files.newOutputStream(outFile.toPath())) {
                    
                     org.apache.commons.io.IOUtils.copy(in, out);
                }
            }
            
            CreatureManager.getInstance().reload();
        } catch (IOException e) {
            System.err.println("Failed copying creature configs from JAR");
            e.printStackTrace();
        }
    }
    
    public void registerRenderers() {
    }
}