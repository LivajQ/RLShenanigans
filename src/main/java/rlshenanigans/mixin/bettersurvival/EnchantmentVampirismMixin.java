package rlshenanigans.mixin.bettersurvival;

import com.mujmajnkraft.bettersurvival.enchantments.EnchantmentVampirism;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rlshenanigans.potion.PotionBloodthirsty;

@Mixin(EnchantmentVampirism.class)
public class EnchantmentVampirismMixin {
    
    @Inject(method = "onEntityDamaged", at = @At("HEAD"), cancellable = true)
    private void onEntityDamagedInject(EntityLivingBase user, Entity target, int level, CallbackInfo ci) {
        if (!(target instanceof EntityLivingBase)) return;
        EntityLivingBase targetBase = (EntityLivingBase) target;
        if (user == null || user.world.isRemote) return;
        
        PotionEffect effect = user.getActivePotionEffect(PotionBloodthirsty.INSTANCE);
        
        if (user.world.rand.nextFloat() < 0.2F + (level - 1) * 0.05F) {
            if (effect != null) {
                int amplifier = effect.getAmplifier() + 1;
                if (amplifier < level) user.addPotionEffect(new PotionEffect(PotionBloodthirsty.INSTANCE, 105, amplifier));
                
                user.heal(amplifier);
            }
            else user.addPotionEffect(new PotionEffect(PotionBloodthirsty.INSTANCE, 105, 0));
        }
        if (effect != null) {
            int amplifier = effect.getAmplifier();
            Potion effectBleed = ForgeRegistries.POTIONS.getValue(new ResourceLocation("srparasites", "bleed"));
            if (effectBleed != null) targetBase.addPotionEffect(new PotionEffect(effectBleed, 105, amplifier));
        }
        
        ci.cancel();
    }
}