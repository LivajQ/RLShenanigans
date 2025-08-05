package rlshenanigans.handlers;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.item.ItemAmuletSinLust;
import rlshenanigans.item.ItemWeaponZweihander;
import rlshenanigans.potion.PotionDragonBad;
import rlshenanigans.potion.PotionGolemBad;
import rlshenanigans.potion.PotionPookie;
import rlshenanigans.potion.PotionStagger;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ModRegistry {

        public static PotionType pookiePotion = new PotionType("Pookie", new PotionEffect(PotionPookie.INSTANCE, 1200)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "Pookie"));
        public static PotionType staggerPotion = new PotionType("Stagger", new PotionEffect(PotionStagger.INSTANCE, 1)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "Stagger"));
        public static PotionType dragonBadPotion = new PotionType("DragonBad", new PotionEffect(PotionDragonBad.INSTANCE, 1)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "DragonBad"));
        public static PotionType golemBadPotion = new PotionType("GolemBad", new PotionEffect(PotionGolemBad.INSTANCE, 1)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "GolemBad"));
        
        
        public static Item sinPendantLust = new ItemAmuletSinLust();
        public static Item weaponZweihander = new ItemWeaponZweihander("weapon_zweihander");
        
        public static void init() {
        }

        @SubscribeEvent
        public static void registerItemEvent(RegistryEvent.Register<Item> event) {
                event.getRegistry().registerAll(
                        sinPendantLust,
                        weaponZweihander
                );
        }

        @SubscribeEvent
        public static void registerRecipeEvent(RegistryEvent.Register<IRecipe> event) {
                //event.getRegistry().register(new RecipeZweihander().setRegistryName(new ResourceLocation(RLShenanigans.MODID, "zweihander")));
        }

        @SubscribeEvent
        public static void registerPotionEvent(RegistryEvent.Register<Potion> event) {
                event.getRegistry().register(PotionPookie.INSTANCE);
                event.getRegistry().register(PotionStagger.INSTANCE);
                event.getRegistry().register(PotionDragonBad.INSTANCE);
                event.getRegistry().register(PotionGolemBad.INSTANCE);
        }

        @SubscribeEvent
        public static void registerPotionTypeEvent(RegistryEvent.Register<PotionType> event) {
                event.getRegistry().register(pookiePotion);
                event.getRegistry().register(staggerPotion);
                event.getRegistry().register(dragonBadPotion);
                event.getRegistry().register(golemBadPotion);
                //PotionHelper.addMix(PotionTypes.THICK, Items.DIAMOND, ModRegistry.pookiePotion);
        }
}