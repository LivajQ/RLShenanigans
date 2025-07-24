package rlshenanigans.handlers;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import rlshenanigans.packet.*;

public class RLSPacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("rlshenanigans");
    
    public static void init() {
        INSTANCE.registerMessage(ParticlePulsePacket.Handler.class, ParticlePulsePacket.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(ParasiteCommandPacket.Handler.class, ParasiteCommandPacket.class, 1, Side.SERVER);
        INSTANCE.registerMessage(OpenParasiteGuiPacket.Handler.class, OpenParasiteGuiPacket.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(RideParasiteHandler.Handler.class, RideParasitePacket.class, 7,Side.SERVER);
        INSTANCE.registerMessage(ParasiteTeleportPacket.Handler.class, ParasiteTeleportPacket.class, 8, Side.SERVER);
        INSTANCE.registerMessage(ParasiteShowTPListPacket.Handler.class, ParasiteShowTPListPacket.class, 9, Side.CLIENT);
        INSTANCE.registerMessage(ParasiteRequestTPListPacket.Handler.class, ParasiteRequestTPListPacket.class, 10, Side.SERVER);
    }
}