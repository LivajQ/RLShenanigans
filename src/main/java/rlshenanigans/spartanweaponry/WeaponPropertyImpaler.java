package rlshenanigans.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyWithCallback;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import rlshenanigans.RLShenanigans;

import java.util.List;

public class WeaponPropertyImpaler extends WeaponPropertyWithCallback {
    
    public WeaponPropertyImpaler(String propType, String propModId) {
        super(propType, propModId);
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
        
        //because why would .motion work
        double baseSpeed = attacker.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
        if (attacker.isSprinting()) baseSpeed *= 1.538D;

        return (float)(baseDamage * baseSpeed * 10);
    }
}