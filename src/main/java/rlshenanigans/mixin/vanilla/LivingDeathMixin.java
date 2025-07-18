package rlshenanigans.mixin.vanilla;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface LivingDeathMixin{
    @Invoker("dropLoot")
    void invokeDropLoot(boolean wasRecentlyHit, int lootingLevel, DamageSource source);
}