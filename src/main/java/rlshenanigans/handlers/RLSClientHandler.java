package rlshenanigans.handlers;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.item.spell.ItemSpellList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RLSClientHandler {
    public static TextureAtlasSprite PARTICLE_RAIN_TRANSPARENT;
    
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
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        PARTICLE_RAIN_TRANSPARENT = event.getMap().registerSprite(new ResourceLocation("rlshenanigans:particle/rain_transparent"));
    }
    
    private static void registerModels(Item... values) {
        for(Item entry : values) {
            ModelLoader.setCustomModelResourceLocation(entry, 0, new ModelResourceLocation(entry.getRegistryName(), "inventory"));
        }
    }
}