package rlshenanigans.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

@Config(modid = RLShenanigans.MODID)
public class ForgeConfigHandler {
	
	@Config.Comment("Server-Side Options")
	@Config.Name("Server Options")
	public static final ServerConfig server = new ServerConfig();

	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	public static final ClientConfig client = new ClientConfig();

	public static class ServerConfig {

		@Config.Comment("Set to false to disable parasite taming (monster)")
		@Config.Name("01 - Parasite Taming")
		public boolean parasiteTamingEnabled = true;
		
		@Config.Comment("Which item should be used for taming parasites")
		@Config.Name("02 - Parasite Taming Item")
		public String parasiteTamingItem = "minecraft:golden_apple";
		
		@Config.Comment({"Use if an item has metadata (e.g. golden apple 0 = normal, 1 = enchanted), otherwise leave at 0"})
		@Config.Name("03 - Parasite Taming Item Metadata")
		public int parasiteTamingItemMetadata = 1;
		
		@Config.Comment({"Set to true to allow resummoning of tamed parasites after death"})
		@Config.Name("04 - Permanent Tamed Parasites")
		public boolean parasiteDeathResummonEnabled = false;
		
		@Config.Comment("Set to false to disable set bonus effects for armor sets")
		@Config.Name("04 - Set Bonuses")
		public boolean setBonusEnabled = true;
		
		@Config.Comment("Set to false to disable debuffs for bad armor (if you don't know what this means you already failed)")
		@Config.Name("05 - Bad Armor Debuffs >:|")
		public boolean badArmorDebuffsEnabled = true;
		
		@Config.Comment("Set to false to disable custom mob spawning")
		@Config.Name("06 - Custom Mobs")
		public boolean customMobsEnabled = true;
		
		@Config.Comment("Dr. Jr.")
		@Config.Name("Dr. Jr.")
		public boolean drJrEnabled = true;
	}

	public static class ClientConfig {

		@Config.Comment("Set to false to disable THH textures for parasites (even bigger monster)")
		@Config.Name("01 - THH Textures")
		public boolean thhEnabled = true;
		
		@Config.Comment("Set to false to stop parasites from talking when tamed")
		@Config.Name("02 - Parasite Speech")
		public boolean parasiteSpeechEnabled = true;
		
		@Config.Comment("Set to false to disable custom parasite death messages")
		@Config.Name("03 - Parasite Death Messages")
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