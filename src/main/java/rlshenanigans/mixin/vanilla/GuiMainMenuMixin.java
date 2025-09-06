package rlshenanigans.mixin.vanilla;

import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.util.SplashTextEntries;

import java.util.Random;

@Mixin(value = GuiMainMenu.class, priority = 3000)
public abstract class GuiMainMenuMixin {
    @Shadow private String splashText;
    private static final Random RAND = new Random();
    
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectCustomSplash(CallbackInfo ci) {
        if (RAND.nextDouble() * 100 < ForgeConfigHandler.client.splashTextChance) this.splashText = SplashTextEntries.getRandomSplash();
    }
}