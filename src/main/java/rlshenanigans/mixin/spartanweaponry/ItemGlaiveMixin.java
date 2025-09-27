package rlshenanigans.mixin.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.item.ItemGlaive;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGlaive.class)
public class ItemGlaiveMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectGlaiveProperties(String unlocName, ToolMaterialEx material, CallbackInfo ci) {
        ((ItemSwordBase)(Object)this).addWeaponProperty(WeaponProperties.BLOCK_MELEE);
    }
}
