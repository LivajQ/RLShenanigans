package rlshenanigans.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.client.particle.ParticleCustomizable;
import rlshenanigans.packet.ParticleItemBuffHitPacket;

import java.util.Collection;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ItemBuffHandler {
    
    public enum BuffTypes {
        NONE(0xFFFFFFFF),
        FIRE(0xFFD9420C),
        LIGHTNING(0xFF0000FF),
        MAGIC(0xFF1D4ED8);
        
        private final int color;
        
        BuffTypes(int color) {
            this.color = color;
        }
        
        public int getColor() {
            return color;
        }
    }
    
    public static void addBuffedItem(ItemStack stack, BuffTypes type) {
        addBuffedItem(stack, type, 1.0F);
    }
    
    public static void addBuffedItem(ItemStack stack, BuffTypes type, float amplifier) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        
        tag.setInteger("RLSBuffType", type.ordinal());
        tag.setFloat("RLSBuffAmplifier", amplifier);
    }
    
    public static BuffTypes getBuffTypeForItem(ItemStack stack) {
        if (stack == null) return BuffTypes.NONE;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("RLSBuffType")) return BuffTypes.NONE;
        
        int raw = tag.getInteger("RLSBuffType");
        
        BuffTypes[] values = BuffTypes.values();
        if (raw < 0 || raw >= values.length) return BuffTypes.NONE;
        
        return values[raw];
    }
    
    public static float getBuffAmplifierForItem(ItemStack stack) {
        if (stack == null) return 1.0F;
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null || !tag.hasKey("RLSBuffAmplifier") ? 1.0F : tag.getFloat("RLSBuffAmplifier");
    }
    
    @SideOnly(Side.CLIENT)
    public static void createParticleForBuffType(BuffTypes type, EntityLivingBase target) {
        createParticleForBuffType(type, target.world, target.posX, target.posY, target.posZ);
    }
    
    @SideOnly(Side.CLIENT)
    public static ParticleCustomizable createParticleForBuffType(BuffTypes type, World world, double posX, double posY, double posZ) {
        if (world == null) return null;
        
        switch (type) {
            case FIRE: return new ParticleCustomizable(world, 48, posX,  posY, posZ);
            case LIGHTNING: return new ParticleCustomizable(world, 81, posX,  posY, posZ);
            case MAGIC: return new ParticleCustomizable(world, 66, posX,  posY, posZ).color(type.color);
            default: return null;
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        float damage = event.getAmount();
        if (!hasBuffedWeaponForEvent(event.getSource().getTrueSource(), victim)) return;
        EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
        
        ItemStack stack = attacker.getHeldItemMainhand();
        float amplifier = getBuffAmplifierForItem(stack);
        if (amplifier <= 0.0F) amplifier = 1.0F;
        
        BuffTypes type = getBuffTypeForItem(stack);
        switch (type) {
            case FIRE:
                double physicalFraction = Math.pow(0.5, amplifier);
                double fireFraction = 1.0 - physicalFraction;
                double physicalDamage = damage * physicalFraction;
                double fireDamage = damage * fireFraction;
                
                victim.setFire((int)(10 * amplifier));
                event.setAmount((float) physicalDamage);
                
                victim.hurtResistantTime = 0;
                victim.attackEntityFrom(EntityDamageSource.ON_FIRE, (float) fireDamage);
                break;
                
            case MAGIC:
                event.setCanceled(true);
                victim.hurtResistantTime = 0;
                victim.attackEntityFrom(EntityDamageSource.MAGIC, event.getAmount());
                break;
            
            default: return;
        }
        
        ParticleItemBuffHitPacket packet = new ParticleItemBuffHitPacket(victim, type, 10);
        RLSPacketHandler.INSTANCE.sendToAllTracking(packet, victim);
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (!hasBuffedWeaponForEvent(event.getSource().getTrueSource(), victim)) return;
        EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
        
        ItemStack stack = attacker.getHeldItemMainhand();
        float amplifier = getBuffAmplifierForItem(stack);
        if (amplifier <= 0.0F) amplifier = 1.0F;
        
        BuffTypes type = getBuffTypeForItem(stack);
        switch (type) {
            case MAGIC:
                final int maxEffects = 10;
                Collection<PotionEffect> positiveEffects = attacker.getActivePotionEffects()
                        .stream()
                        .filter(e -> !e.getPotion().isBadEffect())
                        .collect(Collectors.toList());
                        
                if (positiveEffects.size() < maxEffects) {
                    PotionEffect effect = getRandomEffect(amplifier);
                    attacker.addPotionEffect(effect != null ? effect : new PotionEffect(MobEffects.REGENERATION, 100, 0));
                }
                break;
            
            default: return;
        }
    }
    
    private static boolean hasBuffedWeaponForEvent(Entity attacker, EntityLivingBase victim) {
        if (!(attacker instanceof EntityLivingBase) || victim == null) return false;
        ItemStack stack = ((EntityLivingBase)attacker).getHeldItemMainhand();
        return !stack.isEmpty() && getBuffTypeForItem(stack) != BuffTypes.NONE;
    }
    
    private static PotionEffect getRandomEffect(float amplifier) {
        MagicBuffPotions[] vals = MagicBuffPotions.values();
        MagicBuffPotions chosen = vals[RLShenanigans.RLSRAND.nextInt(vals.length)];
        return chosen.getPotionEffect(amplifier);
    }
    
    private enum MagicBuffPotions {
        STRENGTH(new ResourceLocation("minecraft", "strength"), 0.5F),
        HASTE(new ResourceLocation("minecraft", "haste"), 0.3F),
        SPEED(new ResourceLocation("minecraft", "speed"), 0.4F),
        REJUVENATION(new ResourceLocation("lycanitesmobs", "rejuvenation"), 0.5F),
        MAGIC_FOCUS(new ResourceLocation("potioncore", "magic_focus"), 0.6F);
        
        private final ResourceLocation potionId;
        private final float amplifierScaling;
        
        MagicBuffPotions(ResourceLocation potionId, float amplifierScaling) {
            this.potionId = potionId;
            this.amplifierScaling = amplifierScaling;
        }
        
        private PotionEffect getPotionEffect(float amplifier) {
            Potion potion = ForgeRegistries.POTIONS.getValue(potionId);
            if (potion == null) return null;
            return new PotionEffect(potion,
                    MathHelper.clamp((int) (amplifier * 200), 100, 1200),
                    MathHelper.clamp((int) (amplifier / amplifierScaling) - 1, 0, 4));
        }
    }
}