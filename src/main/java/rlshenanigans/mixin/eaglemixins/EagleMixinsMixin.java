package rlshenanigans.mixin.eaglemixins;

import eaglemixins.handlers.SRParasitesHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SRParasitesHandler.class)
public class EagleMixinsMixin {
    
    public EagleMixinsMixin() {
    }
    
    @Redirect(
            method = "onLivingUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getTotalWorldTime()J",
                    ordinal = 0
            )
    )
    private static long eagleMixins_redirectTick(World world) {
        return 1L;
    }
}