package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import rlshenanigans.entity.spell.EntitySpellEagleEye;
import rlshenanigans.handlers.ForgeConfigHandler;

public class ItemSpellEagleEye extends ItemSpellBase {

    public ItemSpellEagleEye(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.25F);
        EntitySpellEagleEye eagle = new EntitySpellEagleEye(caster.world, caster, 6000);
        eagle.setPosition(caster.posX, caster.posY, caster.posZ);
        caster.world.spawnEntity(eagle);
    }
}