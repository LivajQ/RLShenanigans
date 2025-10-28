package rlshenanigans.mixin.vanilla;

import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlshenanigans.spartanweaponry.RLSWeaponProperties;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
    
    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void preventItemDrop(boolean dropAll, CallbackInfoReturnable<ItemStack> cir) {
        EntityPlayer player = (EntityPlayer)(Object)this;
        Item mainHandItem = player.getHeldItemMainhand().getItem();
        ItemStack droppedItem = player.inventory.getCurrentItem();
        ItemStack mainHandStack = player.getHeldItemMainhand();
        
        if (!(mainHandItem instanceof ItemSwordBase)) return;
        ItemSwordBase spartanWeapon = (ItemSwordBase)mainHandItem;
        if (!spartanWeapon.hasWeaponProperty(RLSWeaponProperties.STRONG_GRIP)) return;
        
        if (ItemStack.areItemStacksEqual(droppedItem, mainHandStack)) cir.setReturnValue(null);
    }
}
