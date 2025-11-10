package rlshenanigans.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.potion.PotionPowerWithin;

import javax.vecmath.Color3f;
import java.util.List;

public class ItemSpellPowerWithin extends ItemSpellBase {
    
    public ItemSpellPowerWithin(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
       super.addInformation(stack, worldIn, tooltip, flagIn);
        String key = getTranslationKey() + ".tooltip.";
        tooltip.add(TextFormatting.ITALIC + I18n.format(key + "desc.2"));
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
        caster.addPotionEffect(new PotionEffect(PotionPowerWithin.INSTANCE, 1200));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Color3f getParticleColor() {
        return new Color3f(1.0F, 0.0F, 0.0F);
    }
}