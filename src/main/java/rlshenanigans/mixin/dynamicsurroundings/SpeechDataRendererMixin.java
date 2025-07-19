package rlshenanigans.mixin.dynamicsurroundings;

import org.orecruncher.dsurround.client.renderer.SpeechDataRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SpeechDataRenderer.class)
public abstract class SpeechDataRendererMixin {
    
    @ModifyArg(
            method = "doRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"
            ),
            index = 1
    )
    private static float modifyY(float originalY) {
        return originalY + 0.35F;
    }
}