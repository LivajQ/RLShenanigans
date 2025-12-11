package rlshenanigans.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.EntitySpellShockWard;
import rlshenanigans.handlers.ForgeConfigHandler;

import javax.vecmath.Color3f;

public class ItemSpellShockWard extends ItemSpellBase {

    public ItemSpellShockWard(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        EntitySpellShockWard ward = new EntitySpellShockWard(caster.world, caster, 1200);
        ward.setPosition(caster.posX, caster.posY, caster.posZ);
        caster.world.spawnEntity(ward);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Color3f getParticleColor() {
        return new Color3f(1.0F, 1.0F, 0.0F);
    }
}
