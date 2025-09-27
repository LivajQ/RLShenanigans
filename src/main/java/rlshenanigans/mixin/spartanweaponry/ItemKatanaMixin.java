package rlshenanigans.mixin.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.item.ItemKatana;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlshenanigans.spartanweaponry.RLSWeaponProperties;

@Mixin(ItemKatana.class)
public abstract class ItemKatanaMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectKatanaProperties(String unlocName, ToolMaterialEx material, CallbackInfo ci) {
        ((ItemSwordBase)(Object)this).addWeaponProperty(RLSWeaponProperties.BLEED_BUILDUP_1);
        ((ItemSwordBase)(Object)this).addWeaponProperty(WeaponProperties.DAMAGE_ABSORB_25);
    }
}