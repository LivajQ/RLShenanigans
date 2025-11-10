package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import rlshenanigans.entity.projectile.EntitySpellFireball;

public class ItemSpellFireball extends ItemSpellBase {
    
    public ItemSpellFireball(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.6F);
        EntitySpellFireball fireball = new EntitySpellFireball(caster.world, caster);
        fireball.shoot(caster, caster.rotationPitch, caster.rotationYaw, 0.0F, 1.5F, 0.0F);
        caster.world.spawnEntity(fireball);
    }
}