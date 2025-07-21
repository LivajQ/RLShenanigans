package rlshenanigans.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class PotionStagger extends PotionBase {
    
    public static final PotionStagger INSTANCE = new PotionStagger();
    
    public PotionStagger() {
        super("Stagger", true, 0x000000);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        //PotionEventHandler
        living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2, 10));
    }
    
}