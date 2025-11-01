package rlshenanigans.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class ItemSpellBase extends Item {
    protected final int manaCost;
    protected final int castTime;
    protected final int stackSize;
    
    public ItemSpellBase(String registryName, int manaCost, int castTime, int stackSize) {
        this.manaCost = manaCost;
        this.castTime = castTime;
        this.stackSize = stackSize;
        this.setMaxStackSize(stackSize);
        this.setRegistryName(registryName);
        this.setTranslationKey(registryName);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for (int i = 0; i < 10; i++) {
            String key = getTranslationKey() + ".tooltip." + i;
            String line = I18n.format(key);
            if (!line.equals(key)) {
                tooltip.add(line);
            } else {
                break;
            }
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        world.playSound(null, player.posX, player.posY, player.posZ , SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 0.1f, 2f);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return castTime;
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (timeLeft == 0) castSpell(entity);
    }
    
    public abstract void castSpell(EntityLivingBase caster);
}