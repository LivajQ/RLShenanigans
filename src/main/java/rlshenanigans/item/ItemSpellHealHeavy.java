package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.handlers.RLSSoundHandler;

public class ItemSpellHealHeavy extends ItemSpellBase {

    public ItemSpellHealHeavy(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        float healAmount = caster.getMaxHealth() * 0.5F;
        caster.heal(healAmount);
        
        this.playCastSound(caster, RLSSoundHandler.SPELL_HEAL, 1.0F, 1.0F);
        for (int x = 1; x <= 40; x++) {
            this.spawnCastParticle(caster, getTextureIndexFromEnum(EnumParticleTypes.VILLAGER_HAPPY), 1, 60, 0.3D);
        }
    }
    
}