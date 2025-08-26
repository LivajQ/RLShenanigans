package rlshenanigans.packet;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.client.speech.SpeechHelper;

public class ParasiteSpeakPacket implements IMessage
{
    private int parasiteId;
    private String message;
    private int duration;
    
    public ParasiteSpeakPacket() {}
    
    public ParasiteSpeakPacket(EntityParasiteBase parasite, String message, int duration) {
        this.parasiteId = parasite.getEntityId();
        this.message = message;
        this.duration = duration;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(parasiteId);
        ByteBufUtils.writeUTF8String(buf, message);
        buf.writeInt(duration);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        parasiteId = buf.readInt();
        message = ByteBufUtils.readUTF8String(buf);
        duration = buf.readInt();
    }
    
    public static class Handler implements IMessageHandler<ParasiteSpeakPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ParasiteSpeakPacket packet, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityParasiteBase parasite = (EntityParasiteBase) Minecraft.getMinecraft().world.getEntityByID(packet.parasiteId);
                if (parasite != null) {
                    SpeechHelper.trySpeak(parasite, packet.message, packet.duration);
                }
            });
            return null;
        }
    }
}