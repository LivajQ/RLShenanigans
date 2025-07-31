package rlshenanigans.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PotionDragonBad extends PotionBase {
    
    public static final PotionDragonBad INSTANCE = new PotionDragonBad();
    
    public PotionDragonBad() {
        super("DragonBad", false, 0xFFC0CB);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living.world.isRemote || !(living instanceof EntityPlayer)) return;
        living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 2, true, true));
        living.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 2, true, true));
        living.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 40, 2, true, true));
        living.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 40, 2, true, true));
        living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 40, 2, true, true));
        living.addPotionEffect(new PotionEffect(MobEffects.POISON, 40, 15, true, true));
        living.addPotionEffect(new PotionEffect(MobEffects.UNLUCK, 40, 99, true, true));
        addPotionCoreEffect(living, "brokenshield", 40, 0, true, true);
        addPotionCoreEffect(living, "perplexity", 40, 0, true, true);
        addPotionCoreEffect(living, "corrosion", 40, 1, true, true);
        addPotionCoreEffect(living, "weight", 40, 2, true, true);
        addPotionCoreEffect(living, "spinning", 40, 0, true, true);
    }
    
    public static void addPotionCoreEffect(EntityLivingBase living, String effectId, int duration, int amplifier, boolean ambient, boolean showParticles) {
        Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation("potioncore", effectId));
        if (potion == null) return;
        living.addPotionEffect(new PotionEffect(potion, duration, amplifier, ambient, showParticles));
    }
}