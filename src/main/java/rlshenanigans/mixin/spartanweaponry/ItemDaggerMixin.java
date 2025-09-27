package rlshenanigans.mixin.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.item.ItemDagger;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlshenanigans.spartanweaponry.RLSWeaponProperties;

@Mixin(ItemDagger.class)
public abstract class ItemDaggerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectDaggerProperties(String unlocName, ToolMaterialEx material, CallbackInfo ci) {
        ((ItemSwordBase)(Object)this).addWeaponProperty(RLSWeaponProperties.BLEED_BUILDUP_2);
    }
}