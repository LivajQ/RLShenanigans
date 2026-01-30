package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.handlers.ItemBuffHandler;

public class ItemSpellMagicWeapon extends ItemSpellBase {
    
    public ItemSpellMagicWeapon(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        
        ItemStack main = caster.getHeldItemMainhand();
        ItemStack off  = caster.getHeldItemOffhand();
        boolean spellInMain = caster.getHeldItemMainhand().getItem() == this;
        
        ItemStack target;
        
        if (spellInMain) target = off;
        else target = main;
        
        if (target.isEmpty() || !(target.getItem() instanceof ItemSword)) {
            castFailed = true;
            return;
        }
        
        ItemBuffHandler.addBuffedItem(target, ItemBuffHandler.BuffTypes.MAGIC);
    }
    
}