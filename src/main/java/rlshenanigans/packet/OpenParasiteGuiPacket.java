package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.client.gui.ParasiteContextMenu;

public class OpenParasiteGuiPacket implements IMessage {
    private int entityId;
    
    public OpenParasiteGuiPacket() {}
    
    public OpenParasiteGuiPacket(int entityId) {
        this.entityId = entityId;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
    }
    
    public static class Handler implements IMessageHandler<OpenParasiteGuiPacket, IMessage> {
        @Override
        public IMessage onMessage(OpenParasiteGuiPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft.getMinecraft().displayGuiScreen(new ParasiteContextMenu(message.entityId));
            });
            return null;
        }
    }
}