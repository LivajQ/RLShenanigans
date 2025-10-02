package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.npc.EntityNPCPhantom;

public class NPCPhantomSyncFadePacket implements IMessage {
    private int entityId;
    private int fadeTime;
    
    public NPCPhantomSyncFadePacket() {}
    public NPCPhantomSyncFadePacket(EntityNPCPhantom entity) {
        this.entityId = entity.getEntityId();
        this.fadeTime = entity.getPhantomFadeTime();
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.fadeTime = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(fadeTime);
    }
    
    public static class Handler implements IMessageHandler<NPCPhantomSyncFadePacket, IMessage> {
        @Override
        public IMessage onMessage(NPCPhantomSyncFadePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                if (entity instanceof EntityNPCPhantom) {
                    ((EntityNPCPhantom) entity).setPhantomFadeTime(message.fadeTime);
                }
            });
            return null;
        }
    }
}
