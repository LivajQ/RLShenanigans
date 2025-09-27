package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.mixin.vanilla.EntityAccessor;

public class SizeMultiplierPacket implements IMessage
{
    private int entityId;
    private float sizeMultiplier;
    private float baseWidth;
    private float baseHeight;
    
    public SizeMultiplierPacket() {}
    
    public SizeMultiplierPacket(int entityId, float sizeMultiplier, float baseWidth, float baseHeight) {
        this.entityId = entityId;
        this.sizeMultiplier = sizeMultiplier;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(sizeMultiplier);
        buf.writeFloat(baseWidth);
        buf.writeFloat(baseHeight);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        sizeMultiplier = buf.readFloat();
        baseWidth = buf.readFloat();
        baseHeight = buf.readFloat();
    }
    
    public static class Handler implements IMessageHandler<SizeMultiplierPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(SizeMultiplierPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                if (entity instanceof EntityLivingBase) {
                    entity.getEntityData().setFloat("SizeMultiplier", message.sizeMultiplier);
                    ((EntityAccessor) entity).invokeSetSize(message.baseWidth * message.sizeMultiplier, message.baseHeight * message.sizeMultiplier);
                    float scaledWidth = message.baseWidth * message.sizeMultiplier;
                    float scaledHeight = message.baseHeight * message.sizeMultiplier;
                    
                    double halfWidth = scaledWidth / 2.0;
                    
                    double x = entity.posX;
                    double y = entity.posY;
                    double z = entity.posZ;
                    
                    AxisAlignedBB newBox = new AxisAlignedBB(
                            x - halfWidth,
                            y,
                            z - halfWidth,
                            x + halfWidth,
                            y + scaledHeight,
                            z + halfWidth
                    );
                    entity.setEntityBoundingBox(newBox);
                }
            });
            return null;
        }
    }
}