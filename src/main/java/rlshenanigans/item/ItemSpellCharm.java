package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;

public class ItemSpellCharm extends ItemSpellBase {
    
    public ItemSpellCharm(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
    
    }
}