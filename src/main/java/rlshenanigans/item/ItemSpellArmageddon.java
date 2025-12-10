package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import rlshenanigans.entity.projectile.ProjectileSpellFireballCluster;

public class ItemSpellArmageddon extends ItemSpellBase {
    
    public ItemSpellArmageddon(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        ProjectileSpellFireballCluster meteor = new ProjectileSpellFireballCluster(caster.world, caster, 50.0F, 5.0F, 125.0F);
        this.playCastSound(meteor, SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.3F);
        meteor.shoot(caster, 65.0F, caster.rotationYaw, 0.0F, 1.0F, 0.0F);
        meteor.setPosition(meteor.posX, meteor.posY + 30.0F, meteor.posZ);
        caster.world.spawnEntity(meteor);
    }
    
}