package rlshenanigans.handlers;

import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.item.ItemAmuletSinLust;
import rlshenanigans.item.ItemExampleArmor;
import rlshenanigans.item.ItemWeaponZweihander;
import rlshenanigans.potion.PotionPookie;
import rlshenanigans.potion.PotionStagger;
import rlshenanigans.recipe.RecipeExample;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ModRegistry {
        
        public static ItemArmor.ArmorMaterial EXAMPLE_ARMOR = EnumHelper.addArmorMaterial("example_armor", RLShenanigans.MODID + ":example_armor", 26, new int[]{2,4,6,2}, 10, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);

        public static Item exampleHelmet = new ItemExampleArmor("example_helmet", EXAMPLE_ARMOR, 2, EntityEquipmentSlot.HEAD);
        public static Item exampleChestplate = new ItemExampleArmor("example_chestplate", EXAMPLE_ARMOR, 1, EntityEquipmentSlot.CHEST);
        public static Item exampleLeggings = new ItemExampleArmor("example_leggings", EXAMPLE_ARMOR, 2, EntityEquipmentSlot.LEGS);
        public static Item exampleBoots = new ItemExampleArmor("example_boots", EXAMPLE_ARMOR, 1, EntityEquipmentSlot.FEET);

        public static PotionType pookiePotion = new PotionType("Pookie", new PotionEffect(PotionPookie.INSTANCE, 1200)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "Pookie"));
        public static PotionType staggerPotion = new PotionType("Stagger", new PotionEffect(PotionStagger.INSTANCE, 1)).setRegistryName(new ResourceLocation(RLShenanigans.MODID, "Stagger"));
        
        public static Item sinPendantLust = new ItemAmuletSinLust();
        
        public static Item weaponZweihander = new ItemWeaponZweihander("weapon_zweihander");
        
        public static void init() {
        }

        @SubscribeEvent
        public static void registerItemEvent(RegistryEvent.Register<Item> event) {
                event.getRegistry().registerAll(
                        //exampleHelmet,
                        //exampleChestplate,
                        //exampleLeggings,
                        //exampleBoots,
                        sinPendantLust,
                        weaponZweihander
                );
        }

        @SubscribeEvent
        public static void registerRecipeEvent(RegistryEvent.Register<IRecipe> event) {
                event.getRegistry().register(new RecipeExample().setRegistryName(new ResourceLocation(RLShenanigans.MODID, "example")));
        }

        @SubscribeEvent
        public static void registerPotionEvent(RegistryEvent.Register<Potion> event) {
                event.getRegistry().register(PotionPookie.INSTANCE);
                event.getRegistry().register(PotionStagger.INSTANCE);
        }

        @SubscribeEvent
        public static void registerPotionTypeEvent(RegistryEvent.Register<PotionType> event) {
                event.getRegistry().register(pookiePotion);
                event.getRegistry().register(staggerPotion);
                //PotionHelper.addMix(PotionTypes.THICK, Items.DIAMOND, ModRegistry.pookiePotion);
        }
}