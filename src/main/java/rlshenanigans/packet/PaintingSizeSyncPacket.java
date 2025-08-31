package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.item.EntityPaintingTemplate;

public class PaintingSizeSyncPacket implements IMessage
{
    private int entityId;
    private int width;
    private int height;
    
    public PaintingSizeSyncPacket() {}
    
    public PaintingSizeSyncPacket(int entityId, int width, int height) {
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
    
    public static class Handler implements IMessageHandler<PaintingSizeSyncPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PaintingSizeSyncPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Entity entity = world.getEntityByID(message.entityId);
                if (entity instanceof EntityPaintingTemplate) {
                    EntityPaintingTemplate painting = (EntityPaintingTemplate) entity;
                    painting.setWidth(message.width);
                    painting.setHeight(message.height);
                }
            });
            return null;
        }
    }
}