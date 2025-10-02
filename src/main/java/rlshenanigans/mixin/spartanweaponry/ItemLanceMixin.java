package rlshenanigans.mixin.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.item.ItemLance;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlshenanigans.spartanweaponry.RLSWeaponProperties;

@Mixin(ItemLance.class)
public abstract class ItemLanceMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectLanceProperties(String unlocName, ToolMaterialEx material, CallbackInfo ci) {
        ((ItemSwordBase)(Object)this).addWeaponProperty(RLSWeaponProperties.IMPALER);
    }
}