package rlshenanigans.mixin.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlshenanigans.handlers.ItemBuffHandler;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {
    
    @Invoker("renderEffect")
    protected abstract void rls$invokeRenderEffect(IBakedModel model);
    
    @Unique
    private ItemStack rls$capturedStack;
    
    
    
    @Redirect(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;hasEffect()Z"
            )
    )
    private boolean redirectHasEffect(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (ItemBuffHandler.getBuffTypeForItem(stack) != ItemBuffHandler.BuffTypes.NONE) return true;
        return stack.hasEffect();
    }
    
    
    
    @Inject(
            method = "renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD")
    )
    private void captureStack(IBakedModel model, int color, ItemStack stack, CallbackInfo ci) {
        if (stack == null || stack.isEmpty()) return;
        this.rls$capturedStack = stack;
        if (!stack.hasEffect()) this.rls$invokeRenderEffect(model);
    }
    
    
    
    @ModifyVariable(
            method = "renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;ILnet/minecraft/item/ItemStack;)V",
            at = @At(value = "HEAD", shift = At.Shift.AFTER),
            ordinal = 0
    )
    private int modifyColor(int originalColor) {
        if (this.rls$capturedStack == null) return originalColor;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return originalColor;
        
        ItemBuffHandler.BuffTypes type = ItemBuffHandler.getBuffTypeForItem(this.rls$capturedStack);
        if (type == ItemBuffHandler.BuffTypes.NONE) return originalColor;
        
        int baseColor = type.getColor();
        int r = (baseColor >> 16) & 0xFF;
        int g = (baseColor >> 8) & 0xFF;
        int b = baseColor & 0xFF;
        int a = (originalColor >> 24) & 0xFF;
        
        float pulseSpeed = 1.5f;
        float baseline = 0.75f;
        float amplitude = 0.25f;
        float pulse = baseline + amplitude * (0.5f * (float)Math.sin(player.ticksExisted / 20.0f * pulseSpeed) + 0.5f);
        
        r = Math.min(255, (int)(r * pulse));
        g = Math.min(255, (int)(g * pulse));
        b = Math.min(255, (int)(b * pulse));
        //a = Math.min(255, (int)(a * pulse));
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}