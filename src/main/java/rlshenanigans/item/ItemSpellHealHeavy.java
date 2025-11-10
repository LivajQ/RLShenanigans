package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import rlshenanigans.handlers.RLSSoundHandler;

public class ItemSpellHealHeavy extends ItemSpellBase {
    public ItemSpellHealHeavy(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        float healAmount = caster.getMaxHealth() * 0.5F;
        caster.heal(healAmount);
        
        this.playCastSound(caster, RLSSoundHandler.SPELL_HEAL, 1.0F, 1.0F);
        this.spawnCastParticle(caster, getTextureIndexFromEnum(EnumParticleTypes.VILLAGER_HAPPY), 40, 0.5D);
    }
    
}