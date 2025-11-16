package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import rlshenanigans.handlers.RLSSoundHandler;

public class ItemSpellHealLight extends ItemSpellBase {
    public ItemSpellHealLight(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        float healAmount = caster.getMaxHealth() * 0.25F;
        caster.heal(healAmount);
        
        this.playCastSound(caster, RLSSoundHandler.SPELL_HEAL, 1.0F, 1.0F);
        for (int x = 1; x <= 20; x++) {
            this.spawnCastParticle(caster, getTextureIndexFromEnum(EnumParticleTypes.VILLAGER_HAPPY), 1, 20, 0.3D);
        }
    }
    
}