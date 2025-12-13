package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.potion.PotionArcaneShield;

public class ItemSpellArcaneShield extends ItemSpellBase {

    public ItemSpellArcaneShield(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.EVOCATION_ILLAGER_CAST_SPELL, 1.0F, 1.3F);
        caster.addPotionEffect(new PotionEffect(PotionArcaneShield.INSTANCE, 12000));
    }
}