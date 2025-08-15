package rlshenanigans.mixin.srparasites;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(EntityParasiteBase.class)
public abstract class EntityParasiteBaseMixin {
    
    @Inject(method = "attackEntityAsMobMinimum", at = @At("HEAD"), cancellable = true, remap = false)
    private void preventFriendlyFire(EntityLivingBase target, CallbackInfoReturnable<Boolean> cir)
    {
        EntityParasiteBase self = (EntityParasiteBase) (Object) this;
        NBTTagCompound nbt = self.getEntityData();
        
        if (!nbt.hasUniqueId("OwnerUUID")) return;
        
        UUID ownerUUID = nbt.getUniqueId("OwnerUUID");
        UUID targetUUID = target.getUniqueID();
        UUID targetOwnerUUID = null;
        
        NBTTagCompound targetNbt = target.getEntityData();
        
        if (targetNbt.hasUniqueId("OwnerUUID")) targetOwnerUUID = targetNbt.getUniqueId("OwnerUUID");
        
        if (ownerUUID.equals(targetUUID) || ownerUUID.equals(targetOwnerUUID)) cir.setReturnValue(false);
    }
}