package rlshenanigans.mixin.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.item.ItemBattleaxe;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlshenanigans.spartanweaponry.RLSWeaponProperties;

@Mixin(ItemBattleaxe.class)
public class ItemBattleaxeMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectBattleaxeProperties(String unlocName, ToolMaterialEx material, CallbackInfo ci) {
        ((ItemSwordBase)(Object)this).addWeaponProperty(RLSWeaponProperties.CRAZY_AXE_MAN);
        ((ItemSwordBase)(Object)this).addWeaponProperty(WeaponProperties.SWEEP_DAMAGE_FULL);
    }
}
