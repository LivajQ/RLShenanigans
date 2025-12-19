package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import rlshenanigans.handlers.ForgeConfigHandler;

public class ItemSpellMagicWeapon extends ItemSpellBase {
    
    public ItemSpellMagicWeapon(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
    
    }
}