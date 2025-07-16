package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.gui.ParasiteTeleportMenu;
import rlshenanigans.util.ParasiteBrief;
import rlshenanigans.util.TamedParasiteInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParasiteShowTPListPacket implements IMessage
{
    private List<ParasiteBrief> parasiteList = new ArrayList<>();
    
    public ParasiteShowTPListPacket() {}
    
    public ParasiteShowTPListPacket(List<TamedParasiteInfo> infos) {
        for (TamedParasiteInfo info : infos) {
            parasiteList.add(new ParasiteBrief(info));
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(parasiteList.size());
        for (ParasiteBrief brief : parasiteList) {
            ByteBufUtils.writeUTF8String(buf, brief.mobUUID.toString());
            ByteBufUtils.writeUTF8String(buf, brief.name);
            ByteBufUtils.writeUTF8String(buf, brief.className);
            buf.writeInt(brief.skin);
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        parasiteList = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            UUID mobUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            String name = ByteBufUtils.readUTF8String(buf);
            String className = ByteBufUtils.readUTF8String(buf);
            int skin = buf.readInt();
            parasiteList.add(new ParasiteBrief(mobUUID, name, className, skin));
        }
    }
    
    public static class Handler implements IMessageHandler<ParasiteShowTPListPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(ParasiteShowTPListPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                List<TamedParasiteInfo> reconstructed = new ArrayList<>();
                for (ParasiteBrief brief : message.parasiteList) {
                    reconstructed.add(new TamedParasiteInfo(brief.mobUUID, brief.name, brief.className, brief.skin));
                }
                Minecraft.getMinecraft().displayGuiScreen(new ParasiteTeleportMenu(reconstructed));
            });
            return null;
        }
    }
}