package rlshenanigans.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyWithCallback;
import com.oblivioussp.spartanweaponry.item.ItemWeaponBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class WeaponPropertyCrazyAxeMan extends WeaponPropertyWithCallback {
    
    public WeaponPropertyCrazyAxeMan(String propType, String propModId) {
        super(propType, propModId);
    }
    
    public WeaponProperty.PropertyQuality getQuality() {
        return PropertyQuality.POSITIVE;
    }
    
    @Override
    protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
        tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, new Object[]{this.magnitude * 100.0F}));
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) return;
        EntityLivingBase killer = (EntityLivingBase) event.getSource().getTrueSource();
        ItemStack mainHand = killer.getHeldItemMainhand();
        Item item = mainHand.getItem();
        if (!(item instanceof ItemWeaponBase)) return;
        ItemWeaponBase weapon = (ItemWeaponBase) item;
        if (!weapon.hasWeaponProperty(RLSWeaponProperties.CRAZY_AXE_MAN)) return;
        
        PotionEffect strength = killer.getActivePotionEffect(MobEffects.STRENGTH);
        if (strength == null) killer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 0));
        else if (strength.getAmplifier() < 4)
            killer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, strength.getAmplifier() + 1));
        else killer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 4));
    }
}