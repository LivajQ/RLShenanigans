package rlshenanigans.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import rlshenanigans.RLShenanigans;
import rlshenanigans.packet.ParasiteRequestTPListPacket;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
public class TeleportParasiteHandler {
    public static final KeyBinding keyTeleport = new KeyBinding("key.rls.teleport", Keyboard.KEY_N, "key.categories.rls");
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        
        if (mc.inGameHasFocus && keyTeleport.isPressed() && mc.player != null && mc.currentScreen == null && !mc.player.isRiding()) {
            RLSPacketHandler.INSTANCE.sendToServer(new ParasiteRequestTPListPacket());
        }
    }
}