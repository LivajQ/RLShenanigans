package rlshenanigans.mixin.xat;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlshenanigans.handlers.ForgeConfigHandler;
import xzeroair.trinkets.items.potions.PotionObject;

@Mixin(PotionObject.class)
public abstract class PotionObjectMixin {
    
    @Inject(method = "registerType", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelDragonPotionRegistration(boolean addToInternalRegistry, CallbackInfoReturnable<PotionObject> cir) {
        if (ForgeConfigHandler.misc.flightPotionsEnabled) return;
        
        Potion potion = ((PotionObject)(Object)this).getPotion();
        ResourceLocation id = potion.getRegistryName();
        
        if (id != null && id.getNamespace().equals("xat") && id.getPath().equals("dragon")) {
            cir.setReturnValue((PotionObject)(Object)this);
        }
    }
}