package rlshenanigans.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.item.ItemSpellBase;
import rlshenanigans.item.ItemSpellList;
import rlshenanigans.packet.SpellParticlePacket;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class PotionPowerWithin extends PotionBase{
    public static final PotionPowerWithin INSTANCE = new PotionPowerWithin();
    private static final String MOVEMENT_SPEED_UUID = "e1b2c3d4-e5f6-7890-abcd-1234567890ab";
    private static final String ATTACK_SPEED_UUID = "f1e2d3c4-b5a6-7890-cdef-0987654321ba";
    
    public PotionPowerWithin() {
        super("Power_Within", false, 0xFFC0CB);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, 0.4D, 2);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, ATTACK_SPEED_UUID, 0.3D, 2);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.ticksExisted % 5 == 0) spawnParticle(entity);
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) return;
        if (entity.ticksExisted % 20 == 0) entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, entity.getMaxHealth() / 30.0F);
    }
    
    public void spawnParticle(EntityLivingBase entity) {
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        
        double x = bb.minX - 0.2 + (bb.maxX - bb.minX + 0.4) * RLSRAND.nextDouble();
        double y = bb.minY - 0.1 + (bb.maxY - bb.minY + 0.2) * RLSRAND.nextDouble();
        double z = bb.minZ - 0.2 + (bb.maxZ - bb.minZ + 0.4) * RLSRAND.nextDouble();
        
        double motionX = (RLSRAND.nextDouble() - 0.5D) * 0.05D;
        double motionY = 0.05D + RLSRAND.nextDouble() * 0.05D;
        double motionZ = (RLSRAND.nextDouble() - 0.5D) * 0.05D;
        
        RLSPacketHandler.INSTANCE.sendToAll(
                new SpellParticlePacket(
                        ItemSpellList.SPELL_POWER_WITHIN,
                        ItemSpellBase.getTextureIndexFromEnum(EnumParticleTypes.CRIT_MAGIC),
                        (float) x, (float) y, (float) z,
                        (float) motionX, (float) motionY, (float) motionZ,
                        1, 40
                )
        );
    }
    
    @Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
    static class Handler {
        
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingHurt(LivingHurtEvent event) {
            Entity attacker = event.getSource().getTrueSource();
            if (!(attacker instanceof EntityLivingBase)) return;
            if (!((EntityLivingBase)attacker).isPotionActive(PotionPowerWithin.INSTANCE)) return;

            event.setAmount(event.getAmount() * 1.4F);
        }
    }
}