package rlshenanigans.mixin.srparasites;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParasiteEventEntity.class)
public class ParasiteEventEntityMixin {
    
    @Inject(method = "spawnNext", at = @At("HEAD"), cancellable = true, remap = false)
    private static void checkConversionAllowed(EntityParasiteBase entityin, EntityParasiteBase entityout, boolean effects, boolean thunder, CallbackInfo ci) {
        boolean  blocksConverting = entityin.getEntityData().getBoolean("BlockConverting");
        boolean isTamed = entityin.getEntityData().getBoolean("Tamed");
        
        if (blocksConverting && isTamed) {
            ci.cancel();
        }
    }
}