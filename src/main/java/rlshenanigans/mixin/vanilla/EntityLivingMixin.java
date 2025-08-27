package rlshenanigans.mixin.vanilla;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin {
    
    @Inject(method = "setEquipmentBasedOnDifficulty", at = @At("HEAD"), cancellable = true)
    private void cancelGear(DifficultyInstance difficulty, CallbackInfo ci) {
        EntityLiving self = (EntityLiving) (Object) this;
        
        if (self.getEntityData().getBoolean("RLSCustomMob")) {
            ci.cancel();
        }
    }
}