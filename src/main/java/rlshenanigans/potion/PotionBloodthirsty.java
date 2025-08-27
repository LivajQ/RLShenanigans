package rlshenanigans.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

import java.util.UUID;

public class PotionBloodthirsty extends PotionBase {
    public static final UUID BLOODTHIRSTY_SPEED_UUID = UUID.fromString("c3f9a7e2-4b1d-4e8f-9a6f-2d8a3c7b1e90");
    public static final PotionBloodthirsty INSTANCE = new PotionBloodthirsty();
    
    public PotionBloodthirsty() {
        super("Bloodthirsty", false, 0x8B0000);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        IAttributeInstance speedAttr = living.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
        if (speedAttr == null) return;
        
        speedAttr.removeModifier(BLOODTHIRSTY_SPEED_UUID);
        
        amplifier++;
        double expectedBoost = 0.05 + amplifier * 0.05;
            
        AttributeModifier speedMod = new AttributeModifier(
                BLOODTHIRSTY_SPEED_UUID,
                "Bloodthirsty attack speed boost",
                expectedBoost,
                1
        );

        speedAttr.applyModifier(speedMod);
    }
    
    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMap, int amplifier) {
        super.removeAttributesModifiersFromEntity(entity, attributeMap, amplifier);
        
        IAttributeInstance speedAttr = attributeMap.getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
        if (speedAttr != null && speedAttr.getModifier(BLOODTHIRSTY_SPEED_UUID) != null) {
            speedAttr.removeModifier(BLOODTHIRSTY_SPEED_UUID);
        }
    }
    
    @Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
    static class Handler {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) return;
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            
            if (attacker.isPotionActive(PotionBloodthirsty.INSTANCE)) {
                PotionEffect effect = attacker.getActivePotionEffect(PotionBloodthirsty.INSTANCE);
                if (effect != null) {
                    int amplifier = effect.getAmplifier() + 1;
                    float multiplier = 1.05F + amplifier * 0.05F;
                    event.setAmount(event.getAmount() * multiplier);
                }
            }
        }
    }
}