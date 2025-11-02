package rlshenanigans.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.SpellParticlePacket;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;

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
        String key = getTranslationKey() + ".tooltip.";
        
        tooltip.add(TextFormatting.BLUE + "Mana Cost: " + manaCost);
        tooltip.add(TextFormatting.GOLD + "Cast Time: " + (castTime / 20.0F) + "s");
        
        if (I18n.hasKey(key + "desc")) {
            tooltip.add("");
            tooltip.add(TextFormatting.ITALIC + I18n.format(key + "desc"));
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
        return castTime + 1;
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
    
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (entity.world.isRemote || !(entity instanceof EntityPlayer)) return;
        int elapsed = getMaxItemUseDuration(stack) - count;
        if (elapsed >= castTime) {
            entity.stopActiveHand();
            EntityPlayer player = (EntityPlayer)entity;
            MagicStats magicStat = Capabilities.getMagicStats(player);
            if (magicStat == null || magicStat.getMana() < manaCost) {
                player.sendStatusMessage(new TextComponentString("Not enough mana!"), true);
                return;
            }
            
            magicStat.spendMana(manaCost);
            player.getCooldownTracker().setCooldown(this, 10);
            castSpell(entity);
            if (!this.infiniteUses()) stack.shrink(1);
        }
    }
    
    protected boolean infiniteUses() {
        return false;
    }
    
    public abstract void castSpell(EntityLivingBase caster);
    
    protected void spawnCastParticle(EntityLivingBase target, int textureIndex, int particleCount) {
        this.spawnCastParticle(target, textureIndex, particleCount, 0);
    }
    
    protected void spawnCastParticle(EntityLivingBase target, int textureIndex, int particleCount, double particleSpeed) {
        this.spawnCastParticle(textureIndex, particleCount, particleSpeed, target.posX, target.posY + target.height * 0.5D, target.posZ,
                target.width * 0.5D, target.height * 0.5D, target.width * 0.5D
                );
    }
    
    protected void spawnCastParticle(int textureIndex, int particleCount, double particleSpeed,
                                     double x, double y, double z, double motionX, double motionY, double motionZ) {
        
        RLSPacketHandler.INSTANCE.sendToAll(
                new SpellParticlePacket(this, textureIndex, x, y, z, motionX * particleSpeed, motionY * particleSpeed, motionZ * particleSpeed, particleCount)
        );
    }
    
    protected void playCastSound(EntityLivingBase target, SoundEvent sound, float volume, float pitch) {
        World world = target.world;
        world.playSound(null, target.getPosition(), sound, SoundCategory.PLAYERS, volume, pitch);
    }
    
    public Vec3d getParticleColor() {
        return new Vec3d(1.0D, 1.0D, 1.0D);
    }
}