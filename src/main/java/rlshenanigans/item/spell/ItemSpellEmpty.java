package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public class ItemSpellEmpty extends ItemSpellBase{
    public ItemSpellEmpty(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        if (caster instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)caster;
            player.sendStatusMessage(new TextComponentString("A spell needs to be added before this scroll can be used"), true);
        }
    }
    
    @Override
    protected boolean infiniteUses() {
        return true;
    }
}