package rlshenanigans.mixin.vanilla;

import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin {
    
    @Inject(method = "setEquipmentBasedOnDifficulty", at = @At("HEAD"), cancellable = true)
    private void cancelBowEquip(DifficultyInstance difficulty, CallbackInfo ci) {
        AbstractSkeleton self = (AbstractSkeleton) (Object) this;
        
        if (self.getEntityData().getBoolean("RLSCustomMob")) {
            ci.cancel();
        }
    }
}