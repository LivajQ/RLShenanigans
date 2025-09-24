package rlshenanigans.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;

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
        living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2, 10));
    }
    
    @Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
    static class Handler {
        @SubscribeEvent
        public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event){
            EntityLivingBase entity = event.getEntityLiving();
            
            if (!(entity instanceof EntityPlayer) && entity.isPotionActive(PotionStagger.INSTANCE)) {
                
                entity.motionX = 0;
                entity.motionZ = 0;
                if (entity.motionY > 0) entity.motionY = 0;
                entity.jumpMovementFactor = 0;
                entity.velocityChanged = true;
            }
        }
        
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
                if (attacker.isPotionActive(PotionStagger.INSTANCE)) {
                    event.setCanceled(true);
                }
            }
        }
        
        @SubscribeEvent
        public static void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getEntity().world.isRemote) return;
            if (event.getEntityPlayer().isPotionActive(PotionStagger.INSTANCE)) {
                event.setCanceled(true);
            }
        }
        
        @SubscribeEvent
        public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
            if (event.getEntityLiving() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getEntityLiving();
                
                if (player.isPotionActive(PotionStagger.INSTANCE)) {
                    player.motionY = 0;
                }
            }
        }
        
        @SubscribeEvent
        public static void onItemUse(LivingEntityUseItemEvent.Start event) {
            if (event.getEntityLiving() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)event.getEntityLiving();
                if (player != null) {
                    if (player.isPotionActive(PotionStagger.INSTANCE)){
                        event.setDuration(-1);
                        event.setCanceled(true);
                    }
                }
            }
        }
        
        @SubscribeEvent
        public static void onBlock(PlayerInteractEvent.RightClickBlock event)
        {
            EntityPlayer player = event.getEntityPlayer();
            if (player != null)
            {
                if (player.isPotionActive(PotionStagger.INSTANCE))
                {
                    event.setUseBlock(Event.Result.DENY);
                    event.setUseItem(Event.Result.DENY);
                }
            }
        }
        
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event)
        {
            EntityPlayer player = event.player;
            if (player.isPotionActive(PotionStagger.INSTANCE))
            {
                player.motionX = 0;
                player.motionZ = 0;
                if (player.motionY > 0) player.motionY = 0;
                player.jumpMovementFactor = 0;
                player.setVelocity(0, player.motionY, 0);
                player.swingProgress = 0;
                player.swingProgressInt = 0;
                player.isSwingInProgress = false;
            }
        }
    }
}