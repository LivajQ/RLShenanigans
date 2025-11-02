package rlshenanigans.handlers;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.item.ItemSpellList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RLSClientHandler {
    
    public static void init() {}
    
    @SubscribeEvent
    public static void modelRegisterEvent(ModelRegistryEvent event) {
        List<Item> allItems = new ArrayList<>();
        
        allItems.add(RLSItemHandler.sinPendantLust);
        allItems.add(RLSItemHandler.weaponZweihander);
        allItems.add(RLSItemHandler.pocketPetHolderEmpty);
        allItems.add(RLSItemHandler.pocketPetHolderFilled);
        allItems.add(RLSItemHandler.trinketFixedHeart);
        allItems.add(RLSItemHandler.musicDiscLavaChicken);
        
        Collections.addAll(allItems, ItemSpellList.getAllSpells());
        
        registerModels(allItems.toArray(new Item[0]));
    }
    
    private static void registerModels(Item... values) {
        for(Item entry : values) {
            ModelLoader.setCustomModelResourceLocation(entry, 0, new ModelResourceLocation(entry.getRegistryName(), "inventory"));
        }
    }
}