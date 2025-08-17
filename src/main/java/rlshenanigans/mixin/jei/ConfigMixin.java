package rlshenanigans.mixin.jei;

import mezz.jei.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(value = Config.class, remap = false)
public class ConfigMixin {
    
    @Shadow(remap = false)
    private static Set<String> itemBlacklist;
    
    @Inject(
            method = "syncItemBlacklistConfig",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collections;addAll(Ljava/util/Collection;[Ljava/lang/Object;)Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = false,
            remap = false
    )
    private static void removeItemFromBlacklist(CallbackInfoReturnable<Boolean> cir) {
        if (itemBlacklist != null) {
            itemBlacklist.remove("lycanitesmobs:saddle_elemental");
        }
    }
}