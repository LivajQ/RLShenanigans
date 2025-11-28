package rlshenanigans.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;

public class PotionArcaneShield extends PotionBase {
    public static final PotionArcaneShield INSTANCE = new PotionArcaneShield();
    
    public PotionArcaneShield() {
        super("Arcane_Shield", false, 0xFF00FF);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 10 == 0;
    }
    
    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living.world.isRemote) {
            double radius = 1.0;
            double[] heights = {
                    living.posY + 0.2,
                    living.posY + living.height * 0.5,
                    living.posY + living.height * 0.75,
                    living.posY + living.height
            };
            
            double[][] offsets = {
                    { radius, 0 },
                    { -radius, 0 },
                    { 0, radius },
                    { 0, -radius },
                    { radius, radius },
                    { radius, -radius },
                    { -radius, radius },
                    { -radius, -radius }
            };
            
            for (double y : heights) {
                for (double[] offset : offsets) {
                    double posX = living.posX + offset[0];
                    double posZ = living.posZ + offset[1];
                    
                    living.world.spawnParticle(EnumParticleTypes.SPELL_MOB, posX, y, posZ, 0.1, 0.0, 0.1);
                }
            }
        }
    }
    
    @Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
    static class Handler {
        
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingHurt(LivingHurtEvent event) {
            EntityLivingBase entity = event.getEntityLiving();
            if (!entity.isPotionActive(PotionArcaneShield.INSTANCE)) return;
            
            MagicStats magicStat = Capabilities.getMagicStats(entity);
            if (magicStat == null) return;
            
            if (magicStat.getMana() <= event.getAmount() / 2 + 1) {
                entity.world.playSound(null, entity.getPosition(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                entity.removePotionEffect(PotionArcaneShield.INSTANCE);
                magicStat.setMana(0);
            }
            else magicStat.spendMana(event.getAmount() / 2);
            
            event.setAmount(event.getAmount() * 0.7F);
        }
    }

}