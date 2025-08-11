package rlshenanigans.mixin.srparasites;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ParasiteEventEntity.class)
public class ParasiteEventEntityMixin {
    
    @Inject(method = "spawnNext", at = @At("HEAD"), cancellable = true, remap = false)
    private static void checkConversionAllowed(EntityParasiteBase entityin, EntityParasiteBase entityout, boolean effects, boolean thunder, CallbackInfo ci) {
        boolean isTamed = entityin.getEntityData().getBoolean("Tamed");
        
        if (isTamed) {
            ci.cancel();
        }
    }
    
    @Inject(method = "spawnM", at = @At("HEAD"), cancellable = true, remap = false)
    private static void checkConversionAllowed(EntityParasiteBase entityin, String[] out, int particle, boolean cannotDespawn, String name, CallbackInfo ci) {
        boolean isTamed = entityin.getEntityData().getBoolean("Tamed");
        
        if (isTamed) {
            ci.cancel();
        }
    }
    
    @Inject(method = "summonM", at = @At("HEAD"), cancellable = true, remap = false)
    private static void checkConversionAllowed(EntityParasiteBase entityin, String[] out, int range, @Nullable EntityLivingBase target, CallbackInfo ci) {
        boolean isTamed = entityin.getEntityData().getBoolean("Tamed");
        
        if (isTamed) {
            ci.cancel();
        }
    }
    
    @Inject(method = "summonM", at = @At("HEAD"), cancellable = true, remap = false)
    private static void checkConversionAllowed(EntityParasiteBase entityin, String[] out, int range, double tx, double ty, double tz, @Nullable EntityLivingBase target, boolean pod, CallbackInfo ci) {
        boolean isTamed = entityin.getEntityData().getBoolean("Tamed");
        
        if (isTamed) {
            ci.cancel();
        }
    }
}