package rlshenanigans.spartanweaponry;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyWithCallback;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import rlshenanigans.handlers.RLSSoundHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeaponPropertyBleedBuildup extends WeaponPropertyWithCallback {
    private static final Cache<EntityLivingBase, Float> buildupCache = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .weakKeys()
            .build();
    
    public WeaponPropertyBleedBuildup(String propType, String propModId, int propLevel, float propMagnitude) {
        super(propType, propModId, propLevel, propMagnitude);
    }
    
    public WeaponProperty.PropertyQuality getQuality() {
        return PropertyQuality.POSITIVE;
    }
    
    @Override
    protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
        tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, new Object[]{this.magnitude * 100.0F}));
    }
    
    @Override
    public float modifyDamageDealt(ToolMaterialEx material, float baseDamage, float initialDamage, DamageSource source, EntityLivingBase attacker, EntityLivingBase victim) {
        if (victim == null || victim.isDead) return baseDamage;
        Float currentBuildupBoxed = buildupCache.getIfPresent(victim);
        float currentBuildup = currentBuildupBoxed != null ? currentBuildupBoxed : 0F;
        float threshold = victim.getMaxHealth() * 0.35F;
        currentBuildup += baseDamage * this.magnitude;
        
        if (currentBuildup >= threshold) {
            buildupCache.put(victim, currentBuildup - threshold);
            bleedEffect(victim);
            return baseDamage + (victim.getMaxHealth() * 0.3F);
        }
        else buildupCache.put(victim, currentBuildup);
        
        return baseDamage;
    }
    
    private static void bleedEffect(EntityLivingBase victim) {
        if (!(victim.world instanceof WorldServer)) return;
        WorldServer world = (WorldServer) victim.world;
        
        world.playSound(null, victim.getPosition(), RLSSoundHandler.BLEED_BUILDUP, SoundCategory.PLAYERS, 0.1F, 1.0F);
        
        world.spawnParticle(
                EnumParticleTypes.REDSTONE,
                victim.posX, victim.posY + victim.height * 0.5D, victim.posZ,
                100,
                victim.width * 0.5D,
                victim.height * 0.5D,
                victim.width * 0.5D,
                0.0D
        );
    }
}