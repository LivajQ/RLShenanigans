package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import rlshenanigans.entity.spell.EntitySpellRainOfFire;
import rlshenanigans.handlers.ForgeConfigHandler;

public class ItemSpellRainOfFire extends ItemSpellBase {
    
    public ItemSpellRainOfFire(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        EntitySpellRainOfFire rain = new EntitySpellRainOfFire();
        rain.setPosition(caster.posX, caster.posY, caster.posZ);
        caster.world.spawnEntity(rain);
    }
}
