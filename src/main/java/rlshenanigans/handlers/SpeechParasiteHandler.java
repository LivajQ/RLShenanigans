package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import rlshenanigans.RLShenanigans;
import rlshenanigans.client.speech.SpeechHelper;
import rlshenanigans.util.ParasiteSpeech;

import java.util.Random;
@Mod.EventBusSubscriber(modid = RLShenanigans.MODID, value = Side.CLIENT)
public class SpeechParasiteHandler {
    
    private static final Random rand = new Random();
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        
        if (!(entity instanceof EntityParasiteBase)) return;
        
        if (!entity.hasCustomName()) return;
        
        if(!ForgeConfigHandler.client.parasiteSpeechEnabled) return;
        
        if (entity.ticksExisted % 1200 != 0) return;
        
        if (rand.nextFloat() < 0.5f) {
            SpeechHelper.trySpeak(entity, ParasiteSpeech.getRandomQuote(), 120);
        }
    }
}