package rlshenanigans;

import com.lycanitesmobs.core.info.CreatureConfig;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureSpawnConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rlshenanigans.handlers.*;
import rlshenanigans.proxy.CommonProxy;

@Mod(modid = RLShenanigans.MODID, version = RLShenanigans.VERSION, name = RLShenanigans.NAME,
        dependencies = "required-after:fermiumbooter;required-after:srparasites;required-after:bountifulbaubles;required-after:baubles;" +
                "required-after:spartanweaponry;required-after:iceandfire;required-after:lycanitesmobs;required-after:variedcommodities")
public class RLShenanigans
{
    public static final String MODID = "rlshenanigans";
    public static final String VERSION = "0.1";
    public static final String NAME = "RLShenanigans";
    public static final Logger LOGGER = LogManager.getLogger();
    
    @SidedProxy(clientSide = "rlshenanigans.proxy.ClientProxy", serverSide = "rlshenanigans.proxy.CommonProxy")
    public static CommonProxy PROXY;
    
    @Instance(MODID)
    public static RLShenanigans instance;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModRegistry.init();
        RLSEntityHandler.init();
        RLSSoundHandler.init();
        RLShenanigans.PROXY.preInit();
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        RLShenanigans.PROXY.init();
        RLSPacketHandler.init();
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        CreatureSpawnConfig spawnConfig = CreatureManager.getInstance().spawnConfig;
        CreatureConfig config = CreatureManager.getInstance().config;

        spawnConfig.dimensionList = new int[0];
        spawnConfig.dimensionListWhitelist = false;
        config.soulboundDimensionList = new int[0];
        config.soulboundDimensionListWhitelist = false;
        config.summonDimensionList = new int[0];
        config.summonDimensionListWhitelist = false;
    }
}
