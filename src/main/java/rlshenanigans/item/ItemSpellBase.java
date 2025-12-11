package rlshenanigans.item;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.particle.ParticleSpell;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.SpellParticlePacket;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;

import javax.vecmath.Color3f;
import java.awt.*;
import java.util.List;

public abstract class ItemSpellBase extends Item {
    protected final int manaCost;
    protected final int castTime;
    protected final int stackSize;
    protected boolean castFailed;

    public ItemSpellBase(String registryName, ForgeConfigHandler.SpellOptions options) {
        this(registryName, options.manaCost, options.castTime, options.stackSize);
    }
    
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
            
            castSpell(entity);
            if (this.castFailed) {
                player.sendStatusMessage(new TextComponentString("Cast Failed"), true);
                return;
            }
            
            magicStat.spendMana(manaCost);
            player.getCooldownTracker().setCooldown(this, 10);
            if (!this.infiniteUses() && !player.capabilities.isCreativeMode) stack.shrink(1);
        }
    }
    
    protected boolean infiniteUses() {
        return false;
    }
    
    public abstract void castSpell(EntityLivingBase caster);
    
    public void spawnCastParticle(Entity target, int textureIndex, int particleCount) {
        this.spawnCastParticle(target, textureIndex, particleCount, 20);
    }
    
    public void spawnCastParticle(Entity target, int textureIndex, int particleCount, int particleAge) {
        this.spawnCastParticle(target, textureIndex, particleCount, particleAge, 0);
    }
    
    public void spawnCastParticle(Entity target, int textureIndex, int particleCount, double particleSpeed) {
        this.spawnCastParticle(target, textureIndex, particleCount, 20, particleSpeed);
    }
    
    public void spawnCastParticle(Entity target, int textureIndex, int particleCount, int particleAge, double particleSpeed) {
        double xSpeed = (target.world.rand.nextDouble() * 2.0 - 1.0) * particleSpeed;
        double ySpeed = (target.world.rand.nextDouble() * 2.0 - 1.0) * particleSpeed;
        double zSpeed = (target.world.rand.nextDouble() * 2.0 - 1.0) * particleSpeed;
        
        this.spawnCastParticle(textureIndex, particleCount, particleAge, target.posX, target.posY + target.height * 0.5D, target.posZ, xSpeed, ySpeed, zSpeed);
    }
    
    public void spawnCastParticle(int textureIndex, int particleCount, int particleAge,
                                     double x, double y, double z, double motionX, double motionY, double motionZ) {
        
        RLSPacketHandler.INSTANCE.sendToAll(
                new SpellParticlePacket(this, textureIndex, x, y, z, motionX, motionY, motionZ, particleCount, particleAge)
        );
    }
    
    public void playCastSound(Entity target, SoundEvent sound, float volume, float pitch) {
        this.playCastSound(target, sound, volume, pitch, 0, 0, 0);
    }
    
    protected void playCastSound(Entity target, SoundEvent sound, float volume, float pitch, float offsetX, float offsetY, float offsetZ) {
        World world = target.world;
        world.playSound(null, target.posX + offsetX, target.posY + offsetY, target.posZ + offsetZ, sound, SoundCategory.PLAYERS, volume, pitch);
    }
    
    @SideOnly(Side.CLIENT)
    public Color3f getParticleColor() {
        return new Color3f(1.0F, 1.0F, 1.0F);
    }
    
    @SideOnly(Side.CLIENT)
    public float getParticleAlpha() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public Particle getParticle(World world, int textureIndex, int particleAge, double x, double y, double z, double motionX, double motionY, double motionZ) {
        return new ParticleSpell(this, world, textureIndex, particleAge, x, y, z, motionX, motionY, motionZ);
    }
    
    //I didn't even bother checking if all these are correct. magic number final boss
    //Note to self: use Minecraft.getMinecraft().getTextureManager().bindTexture() and override fxlayer to 2 for custom particles
    public static int getTextureIndexFromEnum(EnumParticleTypes type) {
        switch (type) {
            case EXPLOSION_NORMAL: return 0;
            case EXPLOSION_LARGE: return 1;
            case EXPLOSION_HUGE: return 2;
            case FIREWORKS_SPARK: return 3;
            case WATER_BUBBLE: return 4;
            case WATER_SPLASH: return 5;
            case WATER_WAKE: return 6;
            case SUSPENDED: return 7;
            case SUSPENDED_DEPTH: return 8;
            case CRIT: return 65;
            case CRIT_MAGIC: return 66;
            case SMOKE_NORMAL: return 50;
            case SMOKE_LARGE: return 51;
            case SPELL: return 128;
            case SPELL_INSTANT: return 129;
            case SPELL_MOB: return 144;
            case SPELL_MOB_AMBIENT: return 145;
            case SPELL_WITCH: return 146;
            case DRIP_WATER: return 18;
            case DRIP_LAVA: return 19;
            case VILLAGER_ANGRY: return 81;
            case VILLAGER_HAPPY: return 82;
            case TOWN_AURA: return 22;
            case NOTE: return 67;
            case PORTAL: return 73;
            case ENCHANTMENT_TABLE: return 78;
            case FLAME: return 48;
            case LAVA: return 49;
            case FOOTSTEP: return 28;
            case CLOUD: return 7;
            case REDSTONE: return 65;
            case SNOWBALL: return 31;
            case SNOW_SHOVEL: return 32;
            case SLIME: return 33;
            case HEART: return 64;
            case BARRIER: return 35;
            case ITEM_CRACK: return 36;
            case BLOCK_CRACK: return 37;
            case BLOCK_DUST: return 38;
            case WATER_DROP: return 39;
            case ITEM_TAKE: return 40;
            case MOB_APPEARANCE: return 41;
            case DRAGON_BREATH: return 42;
            case END_ROD: return 43;
            case DAMAGE_INDICATOR: return 44;
            case SWEEP_ATTACK: return 45;
            case FALLING_DUST: return 46;
            case TOTEM: return 47;
            case SPIT: return 48;
            default: return 0;
        }
    }
}