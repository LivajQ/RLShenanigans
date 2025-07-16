package rlshenanigans.packet;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.action.BehaviorParasiteHandler;
import rlshenanigans.action.ParasiteCommand;

public class ParasiteCommandPacket implements IMessage {
    
    private int entityId;
    private ParasiteCommand command;
    
    public ParasiteCommandPacket() {}
    
    public ParasiteCommandPacket(int entityId, ParasiteCommand command) {
        this.entityId = entityId;
        this.command = command;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.command = ParasiteCommand.values()[buf.readByte()];
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeByte((byte) command.ordinal());
    }
    
    public static class Handler implements IMessageHandler<ParasiteCommandPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ParasiteCommandPacket msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            player.getServerWorld().addScheduledTask(() -> {
                Entity entity = player.world.getEntityByID(msg.entityId);
                if (entity instanceof EntityParasiteBase) {
                    BehaviorParasiteHandler.execute((EntityParasiteBase) entity, msg.command, player);
                }
            });
            
            return null;
        }
    }
}