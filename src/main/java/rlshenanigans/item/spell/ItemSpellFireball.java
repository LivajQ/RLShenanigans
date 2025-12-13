package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import rlshenanigans.entity.projectile.ProjectileSpellFireball;
import rlshenanigans.handlers.ForgeConfigHandler;

public class ItemSpellFireball extends ItemSpellBase {

    public ItemSpellFireball(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.6F);
        ProjectileSpellFireball fireball = new ProjectileSpellFireball(caster.world, caster, 5.0F, 1.0F, 2.0F);
        fireball.shoot(caster, caster.rotationPitch, caster.rotationYaw, 0.0F, 1.5F, 0.0F);
        caster.world.spawnEntity(fireball);
    }
}