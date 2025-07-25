package rlshenanigans.loot;

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
    
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        
        LootEntryItem pendantEntrySimple = new LootEntryItem(
                ModRegistry.sinPendantLust,
                1,
                0,
                new LootFunction[0],
                new LootCondition[] { new RandomChance(0.03F) },
                "rlshenanigans_pendant_entry_simple"
        );
        
        LootEntryItem pendantEntryNether = new LootEntryItem(
                ModRegistry.sinPendantLust,
                1,
                0,
                new LootFunction[0],
                new LootCondition[] { new RandomChance(0.05F) },
                "rlshenanigans_pendant_entry_nether"
        );
        
        if (name.equals(SIMPLE_DUNGEON)) {
            LootPool pool = new LootPool(
                    new LootEntryItem[] { pendantEntrySimple },
                    new LootCondition[0],
                    new RandomValueRange(1, 1),
                    new RandomValueRange(0, 0),
                    "rlshenanigans_pendant_pool_simple"
            );
            event.getTable().addPool(pool);
        } else if (name.equals(NETHER_BRIDGE)) {
            LootPool pool = new LootPool(
                    new LootEntryItem[] { pendantEntryNether },
                    new LootCondition[0],
                    new RandomValueRange(1, 1),
                    new RandomValueRange(0, 0),
                    "rlshenanigans_pendant_pool_nether"
            );
            event.getTable().addPool(pool);
        }
    }
}