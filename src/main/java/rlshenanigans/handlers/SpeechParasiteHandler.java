package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.packet.ParasiteSpeakPacket;
import rlshenanigans.util.ParasiteSpeech;

import java.util.Random;
@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class SpeechParasiteHandler {
    
    private static final Random RAND = new Random();
    private static final int COOLDOWN_BASE = 1200;
    private static final int COOLDOWN_WOUNDED = 100;
    private static final int COOLDOWN_KILL = 200;
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityParasiteBase)) return;
        EntityParasiteBase parasite = (EntityParasiteBase) event.getEntityLiving();
        if (!canSpeak(parasite, COOLDOWN_BASE)) return;
        if (parasite.ticksExisted % 20 != 0) return;
        
        if (RAND.nextFloat() < 0.2f) {
            RLSPacketHandler.INSTANCE.sendToAll(new ParasiteSpeakPacket(
                    parasite, ParasiteSpeech.getRandomQuote(ParasiteSpeech.QuoteType.BASE), 240)
            );
            parasite.getEntityData().setLong("LastSpeechTick", parasite.world.getTotalWorldTime());
        }
    }
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityParasiteBase)) return;
        EntityParasiteBase parasite = (EntityParasiteBase) event.getEntityLiving();
        if (!canSpeak(parasite, COOLDOWN_WOUNDED)) return;
        if (parasite.getMaxHealth() <= 0.0F) return;
   
        if (parasite.getHealth() / parasite.getMaxHealth() < 0.3F) {
            RLSPacketHandler.INSTANCE.sendToAll(new ParasiteSpeakPacket(
                    parasite, ParasiteSpeech.getRandomQuote(ParasiteSpeech.QuoteType.WOUNDED), 240)
            );
            parasite.getEntityData().setLong("LastSpeechTick", parasite.world.getTotalWorldTime());
        }
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getTrueSource();
        if (!(killer instanceof EntityParasiteBase)) return;
        EntityParasiteBase parasite = (EntityParasiteBase) killer;
        if (!canSpeak(parasite, COOLDOWN_KILL)) return;
        
        RLSPacketHandler.INSTANCE.sendToAll(new ParasiteSpeakPacket(
                parasite, ParasiteSpeech.getRandomQuote(ParasiteSpeech.QuoteType.KILL), 240)
        );
        parasite.getEntityData().setLong("LastSpeechTick", parasite.world.getTotalWorldTime());
        
    }
    
    private static boolean canSpeak(EntityParasiteBase parasite, int cooldown) {
        boolean isOnCooldown = parasite.world.getTotalWorldTime() - parasite.getEntityData().getLong("LastSpeechTick") < cooldown;
        return parasite.hasCustomName() && ForgeConfigHandler.client.parasiteSpeechEnabled && !isOnCooldown && !parasite.world.isRemote;
    }
}