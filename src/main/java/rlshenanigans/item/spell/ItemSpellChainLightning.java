package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.spell.EntitySpellChainLightning;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.handlers.RLSSoundHandler;

import javax.vecmath.Color3f;

public class ItemSpellChainLightning extends ItemSpellBase {

    public ItemSpellChainLightning(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, RLSSoundHandler.SPELL_CHAIN_LIGHTNING, 1.0F, 1.0F);
        EntitySpellChainLightning lightning = new EntitySpellChainLightning(caster.world, caster, 300);
        lightning.setPosition(caster.posX, caster.posY, caster.posZ);
        caster.world.spawnEntity(lightning);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Color3f getParticleColor() {
        return new Color3f(1.0F, 1.0F, 0.0F);
    }
}