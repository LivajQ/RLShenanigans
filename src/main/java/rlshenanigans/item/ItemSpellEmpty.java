package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;

public class ItemSpellEmpty extends ItemSpellBase{
    public ItemSpellEmpty(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, stackSize, castTime);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
    
    }
}