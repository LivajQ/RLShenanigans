package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.entity.item.EntityPaintingTemplate;
import rlshenanigans.handlers.RLSPacketHandler;

public class PaintingResizePacket implements IMessage {
    private int entityId;
    private int width;
    private int height;
    
    public PaintingResizePacket() {}
    
    public PaintingResizePacket(int entityId, int width, int height) {
        this.entityId = entityId;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        width = buf.readInt();
        height = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(width);
        buf.writeInt(height);
    }
    
    public static class Handler implements IMessageHandler<PaintingResizePacket, IMessage> {
        @Override
        public IMessage onMessage(PaintingResizePacket message, MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.world;
                Entity entity = world.getEntityByID(message.entityId);
                if (entity instanceof EntityPaintingTemplate) {
                    EntityPaintingTemplate painting = (EntityPaintingTemplate) entity;
                    painting.setWidth(message.width);
                    painting.setHeight(message.height);
                    RLSPacketHandler.INSTANCE.sendToAllTracking(
                            new PaintingSizeSyncPacket(painting.getEntityId(), message.width, message.height),
                            painting
                    );
                }
            });
            return null;
        }
    }
}