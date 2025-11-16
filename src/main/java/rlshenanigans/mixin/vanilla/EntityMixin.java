package rlshenanigans.mixin.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    
    @Inject(method = "updatePassenger", at = @At("TAIL"))
    private void offsetLongarmsRider(Entity passenger, CallbackInfo ci) {
        Entity mount = passenger.getRidingEntity();
        if (mount == null) return;
        
        if ("com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted"
                .equals(mount.getClass().getName())) {
            
            Vec3d look = mount.getLookVec();
            Vec3d offset = look.scale(0.15D);
            
            double newX = mount.posX + offset.x;
            double newY = passenger.posY - 1.45F;
            double newZ = mount.posZ + offset.z;
            
            passenger.setPosition(newX, newY, newZ);
        }
    }
}