package rlshenanigans.mixin.eaglemixins;

import eaglemixins.handlers.SRParasitesHandler;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SRParasitesHandler.class)
public class SRParasitesHandlerMixin {
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), remap = false, cancellable = true)
    private static void eagleMixins_redirectTick(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}