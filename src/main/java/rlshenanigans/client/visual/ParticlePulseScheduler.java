package rlshenanigans.client.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ParticlePulseScheduler {
    
    private static final List<ScheduledPulse> pulses = new ArrayList<>();
    
    public static void scheduleBurst(int entityId, EnumParticleTypes type, int durationTicks, int burstSize) {
        pulses.add(new ScheduledPulse(entityId, type, durationTicks, burstSize));
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (Minecraft.getMinecraft().world == null) return;
        
        Iterator<ScheduledPulse> it = pulses.iterator();
        while (it.hasNext()) {
            ScheduledPulse pulse = it.next();
            if (pulse.ticksRemaining % 20 == 0) {
                spawnParticles(pulse);
            }
            
            pulse.ticksRemaining--;
            if (pulse.ticksRemaining < 0) it.remove();
        }
    }
    
    private static void spawnParticles(ScheduledPulse pulse) {
        World world = Minecraft.getMinecraft().world;
        Entity entity = world.getEntityByID(pulse.entityId);
        if (entity == null) return;
        
        Random rand = new Random();
        
        double baseX = entity.posX;
        double baseY = entity.posY + entity.height * 0.5;
        double baseZ = entity.posZ;
        
        for (int i = 0; i < pulse.burstSize; i++) {
            double mx = rand.nextGaussian() * 0.02D;
            double my = rand.nextGaussian() * 0.02D;
            double mz = rand.nextGaussian() * 0.02D;
            
            double ox = rand.nextDouble() * 4.0D - 2.0D;
            double oy = rand.nextDouble() * 2.0D;
            double oz = rand.nextDouble() * 4.0D - 2.0D;
            
            world.spawnParticle(pulse.type,
                    baseX + ox,
                    baseY + oy,
                    baseZ + oz,
                    mx, my, mz
            );
        }
    }
    
    private static class ScheduledPulse {
        final int entityId;
        final EnumParticleTypes type;
        final int burstSize;
        int ticksRemaining;
        
        ScheduledPulse(int entityId, EnumParticleTypes type, int durationTicks, int burstSize) {
            this.entityId = entityId;
            this.type = type;
            this.ticksRemaining = durationTicks;
            this.burstSize = burstSize;
        }
    }
}