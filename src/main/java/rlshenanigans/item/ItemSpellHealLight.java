package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;

public class ItemSpellHealLight extends ItemSpellBase {
    public ItemSpellHealLight(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, stackSize, castTime);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
    
    }
}
