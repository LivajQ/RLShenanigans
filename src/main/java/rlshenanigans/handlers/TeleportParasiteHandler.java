package rlshenanigans.handlers;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.packet.ParasiteRequestTPListPacket;
import rlshenanigans.proxy.ClientProxy;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
public class TeleportParasiteHandler {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        
        if (mc.inGameHasFocus && ClientProxy.keyTeleport.isPressed() && mc.player != null && mc.currentScreen == null && !mc.player.isRiding()) {
            RLSPacketHandler.INSTANCE.sendToServer(new ParasiteRequestTPListPacket());
        }
    }
}