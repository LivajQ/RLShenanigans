package rlshenanigans.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

@Config(modid = RLShenanigans.MODID)
public class ForgeConfigHandler
{
	
	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	public static final ClientConfig client = new ClientConfig();
	
	@Config.Comment("Parasite-related settings")
	@Config.Name("Parasite Settings")
	public static final ParasiteConfig parasite = new ParasiteConfig();
	
	@Config.Comment("Spawn odds for custom mobs")
	@Config.Name("Custom Mobs Spawn Chance")
	public static final CustomMobSpawnConfig customMobSpawn = new CustomMobSpawnConfig();
	
	@Config.Comment("Misc options")
	@Config.Name("Misc")
	public static final MiscConfig misc = new MiscConfig();
	
	
	
	
	
	public static class ParasiteConfig {
		
		@Config.Comment("Set to false to disable parasite taming (monster)")
		@Config.Name("Parasite Taming")
		public boolean parasiteTamingEnabled = true;
		
		@Config.Comment("Which item should be used for taming parasites")
		@Config.Name("Parasite Taming Item")
		public String parasiteTamingItem = "minecraft:golden_apple";
		
		@Config.Comment({"Use if an item has metadata (e.g. golden apple 0 = normal, 1 = enchanted), otherwise leave at 0"})
		@Config.Name("Parasite Taming Item Metadata")
		public int parasiteTamingItemMetadata = 1;
		
		@Config.Comment({"Set to true to allow resummoning of tamed parasites after death"})
		@Config.Name("Parasites Permanent Taming")
		public boolean parasiteDeathResummonEnabled = false;
		
		@Config.Comment({"Set to true to let tamed parasites attack untamed ones. Not a reliable feature as they're often unable to damage each other"})
		@Config.Name("Parasite on Parasite Violence")
		public boolean parasiteOnParasiteViolence = false;
	}
	
	
	
	
	
	public static class CustomMobSpawnConfig {
		
		@Config.Comment("% chance for 'Strength Main' to spawn")
		@Config.Name("Strength Main")
		public double strengthMainSpawnChance = 1.0D;
		
		@Config.Comment("% chance for 'I Lost My Hive :<' to spawn")
		@Config.Name("I Lost My Hive :<")
		public double lostMyHiveChance = 1.0D;
		
		@Config.Comment("% chance for 'COLUMN' to spawn")
		@Config.Name("COLUMN")
		public double columnChance = 1.0D;
		
		@Config.Comment("% chance for 'Freakyberian' to spawn")
		@Config.Name("Freakyberian")
		public double freakyberianChance = 5.0D;
	}
	
	
	
	
	
	public static class MiscConfig {
		
		@Config.Comment("Set to false to disable set bonus effects for armor sets")
		@Config.Name("Armor Set Buffs")
		public boolean setBonusEnabled = true;
		
		@Config.Comment("Set to false to disable debuffs for bad armor (if you don't know what this means you already failed)")
		@Config.Name("Armor Set Debuffs")
		public boolean badArmorDebuffsEnabled = true;
		
		@Config.Comment("Dr. Jr.")
		@Config.Name("Dr. Jr.")
		public boolean drJrEnabled = true;
		
		@Config.Comment("Set to false to disable misc mob taming")
		@Config.Name("Tameable Misc Enabled")
		public boolean miscTamingEnabled = true;
		
		@Config.Comment({"List of mobs that should be made tameable. Use it for non-parasites and non-lycanites. This feature is terrifyingly janky",
				"Pattern: MobID;Item;Item Metadata",
				"Example: iceandfire:seaserpent;minecraft:fish;2"})
		@Config.Name("Tameable Misc Whitelist")
		public String[] tameableMiscEntries = new String[]{
				"iceandfire:seaserpent;minecraft:fish;2",
				"iceandfire:gorgon;minecraft:fermented_spider_eye;0",
				"iceandfire:if_hydra;lycanitesmobs:poisongland;0"
		};
	}
	
	

	
	
	public static class ClientConfig {

		@Config.Comment("Set to false to disable THH textures for parasites (even bigger monster)")
		@Config.Name("Tamed Parasite THH Textures")
		public boolean thhEnabled = true;
		
		@Config.Comment("Set to false to stop parasites from talking when tamed")
		@Config.Name("Parasite Speech")
		public boolean parasiteSpeechEnabled = true;
		
		@Config.Comment("Set to false to disable custom parasite death messages")
		@Config.Name("Parasite Death Messages")
		public boolean parasiteDeathMessagesEnabled = true;
	}

	
	
	
	
	@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
	private static class EventHandler{

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(RLShenanigans.MODID)) {
				ConfigManager.sync(RLShenanigans.MODID, Config.Type.INSTANCE);
			}
		}
	}
}