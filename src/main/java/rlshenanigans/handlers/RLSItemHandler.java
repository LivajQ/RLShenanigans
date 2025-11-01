package rlshenanigans.handlers;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.item.*;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RLSItemHandler {
    
    public static void init(){}
    
    public static Item sinPendantLust = new ItemAmuletSinLust();
    public static Item trinketFixedHeart = new ItemTrinketFixedHeart();
    public static Item weaponZweihander = new ItemWeaponZweihander("weapon_zweihander");
    public static Item pocketPetHolderEmpty = new ItemPocketPetHolderEmpty();
    public static Item pocketPetHolderFilled = new ItemPocketPetHolderFilled();
    public static Item musicDiscLavaChicken = new ItemMusicDisc("musicdisc_lavachicken", RLSSoundHandler.DISC_LAVACHICKEN);
    
    @SubscribeEvent
    public static void registerItemEvent(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                sinPendantLust,
                weaponZweihander,
                pocketPetHolderEmpty,
                pocketPetHolderFilled,
                trinketFixedHeart,
                musicDiscLavaChicken
        );
    }
}
