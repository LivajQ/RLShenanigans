package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import rlshenanigans.entity.projectile.ProjectileSpellImplosion;
import rlshenanigans.handlers.ForgeConfigHandler;

public class ItemSpellImplosion extends ItemSpellBase {

    public ItemSpellImplosion(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        ProjectileSpellImplosion implosion = new ProjectileSpellImplosion(caster.world, caster, 10, 1.0F, 0.0015F);
        implosion.shoot(caster, caster.rotationPitch, caster.rotationYaw, 0.0F, 0.15F, 0.0F);
        caster.world.spawnEntity(implosion);
    }
}