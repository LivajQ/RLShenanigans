package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.util.TamedParasiteInfo;
import rlshenanigans.util.TamedParasiteRegistry;

import java.util.List;

public class ParasiteRequestTPListPacket implements IMessage
{
    public ParasiteRequestTPListPacket() {}
    
    @Override public void toBytes(ByteBuf buf) {}
    @Override public void fromBytes(ByteBuf buf) {}
    
    public static class Handler implements IMessageHandler<ParasiteRequestTPListPacket, IMessage> {
        @Override
        public IMessage onMessage(ParasiteRequestTPListPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            List<TamedParasiteInfo> infos = TamedParasiteRegistry.getOwnedBy(player.getUniqueID());
            return new ParasiteShowTPListPacket(infos);
        }
    }
}