package rlshenanigans.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.item.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PropertyInjector {

    public static void injectProperties() {
        for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            if (!(item instanceof ItemSwordBase)) continue;

            ItemSwordBase spartanWeapon = (ItemSwordBase) item;

            if (spartanWeapon instanceof ItemBattleaxe) {
                spartanWeapon.addWeaponProperty(RLSWeaponProperties.CRAZY_AXE_MAN);
                spartanWeapon.addWeaponProperty(WeaponProperties.SWEEP_DAMAGE_FULL);
            }

            else if (spartanWeapon instanceof ItemDagger) {
                spartanWeapon.addWeaponProperty(RLSWeaponProperties.BLEED_BUILDUP_2);
            }

            else if (spartanWeapon instanceof ItemGlaive) {
                spartanWeapon.addWeaponProperty(RLSWeaponProperties.STRONG_GRIP);
            }

            else if (spartanWeapon instanceof ItemKatana) {
                spartanWeapon.addWeaponProperty(RLSWeaponProperties.BLEED_BUILDUP_1);
                spartanWeapon.addWeaponProperty(WeaponProperties.DAMAGE_ABSORB_25);
            }

            else if (spartanWeapon instanceof ItemLance) {
                spartanWeapon.addWeaponProperty(RLSWeaponProperties.IMPALER);
            }

        }
    }
}