package rlshenanigans.loot;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.ModRegistry;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class LootTableInjector {
    
    private static final ResourceLocation SIMPLE_DUNGEON = new ResourceLocation("minecraft", "chests/simple_dungeon");
    private static final ResourceLocation NETHER_BRIDGE = new ResourceLocation("minecraft", "chests/nether_bridge");
    private static final Item LUST_PENDANT = ModRegistry.sinPendantLust;
    
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        
        if (name.equals(SIMPLE_DUNGEON)) addTable(LUST_PENDANT, 1, 0, 0.03F, "rlshenanigans_pendant_simple", event);
        if (name.equals(NETHER_BRIDGE)) addTable(LUST_PENDANT, 1, 0, 0.05F, "rlshenanigans_pendant_simple", event);
    }
    
    private static void addTable(Item item, int weightIn, int qualityIn, float chanceIn, String entryName, LootTableLoadEvent event) {
        LootEntryItem entry = new LootEntryItem(
                item,
                weightIn,
                qualityIn,
                new LootFunction[0],
                new LootCondition[] { new RandomChance(chanceIn) },
                entryName + "_entry"
        );
        
        LootPool pool = new LootPool(
                new LootEntryItem[] { entry },
                new LootCondition[0],
                new RandomValueRange(1, 1),
                new RandomValueRange(0, 0),
                entryName + "_pool"
        );
        event.getTable().addPool(pool);
    }
}