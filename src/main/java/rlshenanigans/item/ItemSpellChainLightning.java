package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import rlshenanigans.entity.EntitySpellChainLightning;

public class ItemSpellChainLightning extends ItemSpellBase {
    
    public ItemSpellChainLightning(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        EntitySpellChainLightning lightning = new EntitySpellChainLightning(caster.world, caster, 300);
        lightning.setPosition(caster.posX, caster.posY, caster.posZ);
        caster.world.spawnEntity(lightning);
    }
}
