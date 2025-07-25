package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.visual.ParticlePulseScheduler;

public class ParticlePulsePacket implements IMessage
{

    private String particleTypeName;
    private int durationTicks;
    private int burstSize;
    private int entityId;
    
    public ParticlePulsePacket() {}
    
    public ParticlePulsePacket(Entity entity, EnumParticleTypes type, int duration, int burstSize) {
        this.entityId = entity.getEntityId();
        this.particleTypeName = type.getParticleName();
        this.durationTicks = duration;
        this.burstSize = burstSize;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeUTF8String(buf, particleTypeName);
        buf.writeInt(durationTicks);
        buf.writeInt(burstSize);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        particleTypeName = ByteBufUtils.readUTF8String(buf);
        durationTicks = buf.readInt();
        burstSize = buf.readInt();
    }
    
    public static class Handler implements IMessageHandler<ParticlePulsePacket, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(ParticlePulsePacket msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                
                EnumParticleTypes type = EnumParticleTypes.getByName(msg.particleTypeName);
                if (type != null) {
                    ParticlePulseScheduler.scheduleBurst(
                            msg.entityId,
                            type,
                            msg.durationTicks,
                            msg.burstSize
                    );
                }
            });
            return null;
        }
    }
}