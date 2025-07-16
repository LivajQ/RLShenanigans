package rlshenanigans.mixin.vanilla;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlshenanigans.RLShenanigans;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Inject(
            method = "attackEntityFrom",
            at = @At("HEAD")
    )
    public void rlshenanigans_vanillaEntityPlayer_attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        RLShenanigans.LOGGER.log(Level.INFO, "Player attacked");
        
    }
}