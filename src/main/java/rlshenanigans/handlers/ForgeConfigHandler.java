package rlshenanigans.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

@Config(modid = RLShenanigans.MODID)
public class ForgeConfigHandler {
	
	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	public static final ClientConfig client = new ClientConfig();
	
	@Config.Comment("Parasite-related settings")
	@Config.Name("Parasite Settings")
	public static final ParasiteConfig parasite = new ParasiteConfig();
	
	@Config.Comment("NPC-related settings")
	@Config.Name("NPC Settings")
	public static final NPCConfig npc = new NPCConfig();
	
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
	
	
	
	
	
	public static class NPCConfig {
		
		@Config.Comment("Set to false to disable NPC spawning")
		@Config.Name("NPC Spawning")
		public boolean npcEnabled = true;
		
		@Config.Comment("Base reach for NPCs")
		@Config.Name("NPC Base Reach")
		@Config.RangeDouble(min = 1.0D, max = 50.0D)
		public double baseReach = 4.5D;
		
		@Config.Comment("How much more health ALL NPCs should have on average")
		@Config.Name("Global Health Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double globalHealthMultiplier = 1.0D;
		
		@Config.Comment("How much more damage ALL NPCs should deal on average")
		@Config.Name("Global Damage Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double globalDamageMultiplier = 1.0D;
		
		@Config.Comment("How much more armor ALL NPCs should have on average")
		@Config.Name("Global Armor Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double globalArmorMultiplier = 1.0D;
		
		@Config.Comment("How much more armor toughness ALL NPCs should have on average")
		@Config.Name("Global Armor Toughness Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double globalArmorToughnessMultiplier = 1.0D;
		
		@Config.Comment("Multiplier for the Game Stage AKA how fast NPCs will scale in the world")
		@Config.Name("Game Stage Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double gameStageMultiplier = 1.0D;
		
		@Config.Comment("Set to false to disable invasions when NPCs are enabled")
		@Config.Name("Invasions")
		public boolean invasionsEnabled = true;
		
		@Config.Comment("Cooldown (in ticks) before next invasion can occur")
		@Config.Name("Invasion cooldown")
		public int invasionCooldown = 324000;
		
		@Config.Comment({"Offset (in ticks) for the base cooldown",
				"The actual cooldown will be randomly chosen between [base - offset] and [base + offset]"
		})
		@Config.Name("Invasion offset")
		public int invasionOffset = 108000;
		
		@Config.Comment("Maximum health % invaders can lose per second")
		@Config.Name("Invader Damage Cap")
		@Config.RangeDouble(min = 0, max = 100)
		public double invaderDamageCapPercent = 10.0D;
		
		@Config.Comment("Additional statistics scaling for invaders")
		@Config.Name("Invader Statistics Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double invaderStatisticsMultiplier = 3.0D;
		
		@Config.Comment({"Randomized modifier applied to invader statistics after base scaling.",
				"Final multiplier will be randomly chosen between [1 / factor] and [factor]."
		})
		@Config.Name("Invader Statistics Random Factor")
		@Config.RangeDouble(min = 1.0D, max = 100.0D)
		public double invaderStatisticsRandomFactor = 1.2D;
		
		@Config.Comment("Minimum amount of extra enchantments invaders can receive on top of the base formula")
		@Config.Name("Invader Extra Enchantments Min")
		@Config.RangeInt(min = -50, max = 50)
		public int invaderExtraEnchantmentMin = 1;
		
		@Config.Comment("Maximum amount of extra enchantments invaders can receive on top of the base formula")
		@Config.Name("Invader Extra Enchantments Max")
		@Config.RangeInt(min = -50, max = 50)
		public int invaderExtraEnchantmentMax = 4;
		
		@Config.Comment("Enchantability multiplier for invaders weapons. Basically the bigger the multiplier, the faster they can get high tier enchantments")
		@Config.Name("Invader Enchantability Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double invaderEnchantabilityMultiplier = 1.5D;
		
		@Config.Comment("Additional statistics scaling for summons")
		@Config.Name("Summon Statistics Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double summonStatisticsMultiplier = 1.5D;
		
		@Config.Comment({"Randomized modifier applied to summon statistics after base scaling.",
				"Final multiplier will be randomly chosen between [1 / factor] and [factor]."
		})
		@Config.Name("Summon Statistics Random Factor")
		@Config.RangeDouble(min = 1.0D, max = 100.0D)
		public double summonStatisticsRandomFactor = 1.25D;
		
		@Config.Comment("Minimum amount of extra enchantments summons can receive on top of the base formula")
		@Config.Name("Summon Extra Enchantments Min")
		@Config.RangeInt(min = -50, max = 50)
		public int summonExtraEnchantmentMin = 1;
		
		@Config.Comment("Maximum amount of extra enchantments summons can receive on top of the base formula")
		@Config.Name("Summon Extra Enchantments Max")
		@Config.RangeInt(min = -50, max = 50)
		public int summonExtraEnchantmentMax = 2;
		
		@Config.Comment("Enchantability multiplier for summons weapons. Basically the bigger the multiplier, the faster they can get high tier enchantments")
		@Config.Name("Summon Enchantability Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double summonEnchantabilityMultiplier = 1.0D;
		
		@Config.Comment("Additional statistics scaling for generic NPCs")
		@Config.Name("Generic NPC Statistics Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double genericStatisticsMultiplier = 1.0D;
		
		@Config.Comment({"Randomized modifier applied to generic NPC statistics after base scaling.",
				"Final multiplier will be randomly chosen between [1 / factor] and [factor]."
		})
		@Config.Name("Generic NPC Statistics Random Factor")
		@Config.RangeDouble(min = 1.0D, max = 100.0D)
		public double genericStatisticsRandomFactor = 1.2D;
		
		@Config.Comment("Minimum amount of extra enchantments generic NPCs can receive on top of the base formula")
		@Config.Name("Generic NPC Extra Enchantments Min")
		@Config.RangeInt(min = -50, max = 50)
		public int genericExtraEnchantmentMin = -1;
		
		@Config.Comment("Maximum amount of extra enchantments generic NPCs can receive on top of the base formula")
		@Config.Name("Generic NPC Extra Enchantments Max")
		@Config.RangeInt(min = -50, max = 50)
		public int genericExtraEnchantmentMax = 1;
		
		@Config.Comment("Enchantability multiplier for generic NPC weapons. Basically the bigger the multiplier, the faster they can get high tier enchantments")
		@Config.Name("Generic NPC Enchantability Multiplier")
		@Config.RangeDouble(min = 0.01D, max = 100.0D)
		public double genericEnchantabilityMultiplier = 1.0D;
		
		@Config.Comment({"Chance for an NPC to turn into John Minecraft."
		})
		@Config.Name("John Minecraft Chance")
		@Config.RangeDouble(min = 0.0D, max = 100.0D)
		public double johnMinecraftChance = 0.9;
	}
	
	
	
	
	
	public static class CustomMobSpawnConfig {
		
		@Config.Comment("% chance for 'Strength Main' to spawn")
		@Config.Name("Strength Main")
		@Config.RangeDouble(min = 0.0D, max = 100.0D)
		public double strengthMainChance = 1.0D;
		
		@Config.Comment("% chance for 'I Lost My Hive :<' to spawn")
		@Config.Name("I Lost My Hive :<")
		@Config.RangeDouble(min = 0.0D, max = 100.0D)
		public double lostMyHiveChance = 1.0D;
		
		@Config.Comment("% chance for 'COLUMN' to spawn")
		@Config.Name("COLUMN")
		@Config.RangeDouble(min = 0.0D, max = 100.0D)
		public double columnChance = 1.0D;
		
		@Config.Comment("% chance for 'Freakyberian' to spawn")
		@Config.Name("Freakyberian")
		@Config.RangeDouble(min = 0.0D, max = 100.0D)
		public double freakyberianChance = 5.0D;
		
		@Config.Comment("% chance for 'Heisenberg' to spawn")
		@Config.Name("Heisenberg")
		@Config.RangeDouble(min = 0.0D, max = 100.0D)
		public double heisenbergChance = 1.0D;
	}
	
	
	
	
	
	public static class MiscConfig {
		
		@Config.Comment("Set to false to disable set bonus effects for armor sets")
		@Config.Name("Armor Set Buffs")
		public boolean setBonusEnabled = true;
		
		@Config.Comment("Set to false to disable debuffs for bad armor (if you don't know what this means you already failed)")
		@Config.Name("Armor Set Debuffs")
		public boolean badArmorDebuffsEnabled = true;
		
		@Config.Comment("When set to false, prevents owners from hurting tamed creatures")
		@Config.Name("Friendly fire")
		public boolean friendlyFire = false;
		
		@Config.Comment("Dr. Jr.")
		@Config.Name("Dr. Jr.")
		public boolean drJrEnabled = true;
		
		@Config.Comment("Should flight potions be enabled (includes potions of dragon transformation)")
		@Config.Name("Flight Potions")
		public boolean flightPotionsEnabled = false;
		
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
				"iceandfire:if_hydra;lycanitesmobs:poisongland;0",
				"battletowers:golem;minecraft:tnt;0"
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
		
		@Config.Comment("Chance to display alternative splash text in the main menu")
		@Config.Name("Alternative Splash Text Chance")
		@Config.RangeDouble(min = 0.0D, max = 100.0D)
		public double splashTextChance = 25.0D;
        
        @Config.Comment({"Which parasite parts should be affected by the 'Rainbow Thighs' option",
        "0: off, 1: clothing, 2: body, 3: everything"})
        @Config.Name("Rainbow Thighs")
        @Config.RangeInt(min = 0, max = 3)
        public int rainbowThighs = 1;
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