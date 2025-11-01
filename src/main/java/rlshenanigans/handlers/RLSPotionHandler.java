package rlshenanigans.handlers;

import cursedflames.bountifulbaubles.item.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.potion.*;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RLSPotionHandler {
    
    public static PotionType pookiePotion = new PotionType("Pookie", new PotionEffect(PotionPookie.INSTANCE, 1200)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "Pookie"));
    public static PotionType staggerPotion = new PotionType("Stagger", new PotionEffect(PotionStagger.INSTANCE, 1200)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "Stagger"));
    public static PotionType dragonBadPotion = new PotionType("DragonBad", new PotionEffect(PotionDragonBad.INSTANCE, 1200)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "DragonBad"));
    public static PotionType golemBadPotion = new PotionType("GolemBad", new PotionEffect(PotionGolemBad.INSTANCE, 1200)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "GolemBad"));
    public static PotionType bloodthirstyPotion = new PotionType("Bloodthirsty", new PotionEffect(PotionBloodthirsty.INSTANCE, 1200)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "Bloodthirsty"));
    
    public static void init(){}
    
    @SubscribeEvent
    public static void registerPotionEvent(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(PotionPookie.INSTANCE);
        event.getRegistry().register(PotionStagger.INSTANCE);
        event.getRegistry().register(PotionDragonBad.INSTANCE);
        event.getRegistry().register(PotionGolemBad.INSTANCE);
        event.getRegistry().register(PotionBloodthirsty.INSTANCE);
    }
    
    @SubscribeEvent
    public static void registerPotionTypeEvent(RegistryEvent.Register<PotionType> event) {
        event.getRegistry().register(pookiePotion);
        event.getRegistry().register(staggerPotion);
        event.getRegistry().register(dragonBadPotion);
        event.getRegistry().register(golemBadPotion);
        event.getRegistry().register(bloodthirstyPotion);
        
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                new ItemStack(ModItems.potionWormhole),
                new ItemStack(Items.CHORUS_FRUIT),
                new ItemStack(ModItems.potionRecall)));
        
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
                new ItemStack(ModItems.potionRecall),
                new ItemStack(Items.CHORUS_FRUIT),
                new ItemStack(ModItems.potionWormhole)));
    }
}