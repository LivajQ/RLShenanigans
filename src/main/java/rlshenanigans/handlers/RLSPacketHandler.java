package rlshenanigans.handlers;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import rlshenanigans.packet.*;

public class RLSPacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("rlshenanigans");
    
    public static void init() {
        int id = 0;
        
        INSTANCE.registerMessage(ParticlePulsePacket.Handler.class, ParticlePulsePacket.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(ParasiteCommandPacket.Handler.class, ParasiteCommandPacket.class, id++, Side.SERVER);
        INSTANCE.registerMessage(OpenParasiteGuiPacket.Handler.class, OpenParasiteGuiPacket.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(RideMobHandler.Handler.class, RideMobPacket.class, id++,Side.SERVER);
        INSTANCE.registerMessage(ParasiteTeleportPacket.Handler.class, ParasiteTeleportPacket.class, id++, Side.SERVER);
        INSTANCE.registerMessage(ParasiteShowTPListPacket.Handler.class, ParasiteShowTPListPacket.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(ParasiteRequestTPListPacket.Handler.class, ParasiteRequestTPListPacket.class, id++, Side.SERVER);
        INSTANCE.registerMessage(SizeMultiplierPacket.Handler.class, SizeMultiplierPacket.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(ParasiteSpeakPacket.Handler.class, ParasiteSpeakPacket.class, id++, Side.CLIENT);
        INSTANCE.registerMessage(PaintingResizePacket.Handler.class, PaintingResizePacket.class, id++, Side.SERVER);
        INSTANCE.registerMessage(PaintingSizeSyncPacket.Handler.class, PaintingSizeSyncPacket.class, id++, Side.CLIENT);
    }
}