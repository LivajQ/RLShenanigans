package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.client.particle.ParticleCustomizable;
import rlshenanigans.handlers.ItemBuffHandler;

public class ParticleItemBuffHitPacket implements IMessage {
    
    private int entityId;
    private int typeOrdinal;
    private int count;
    
    public ParticleItemBuffHitPacket() {}
    
    public ParticleItemBuffHitPacket(Entity target, ItemBuffHandler.BuffTypes type, int count) {
        this.entityId = target.getEntityId();
        this.typeOrdinal = type.ordinal();
        this.count = count;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(typeOrdinal);
        buf.writeInt(count);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        typeOrdinal = buf.readInt();
        count = buf.readInt();
    }
    
    public static class Handler implements IMessageHandler<ParticleItemBuffHitPacket, IMessage> {
        
        @Override
        public IMessage onMessage(ParticleItemBuffHitPacket msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                
                World world = Minecraft.getMinecraft().world;
                if (world == null) return;
                
                Entity e = world.getEntityByID(msg.entityId);
                if (!(e instanceof EntityLivingBase)) return;
                
                ItemBuffHandler.BuffTypes type = ItemBuffHandler.BuffTypes.values()[msg.typeOrdinal];
                
                spawnClientParticles((EntityLivingBase)e, type, msg.count);
            });
            
            return null;
        }
    }
    
    private static void spawnClientParticles(EntityLivingBase target, ItemBuffHandler.BuffTypes type, int count) {
        
        World world = target.world;
        if (world == null) return;
        
        double cx = target.posX;
        double cy = target.posY + target.height * 0.5;
        double cz = target.posZ;
        
        for (int i = 0; i < count; i++) {
            ParticleCustomizable p = ItemBuffHandler.createParticleForBuffType(type, world, cx, cy, cz);
            
            if (p == null) continue;

            Minecraft.getMinecraft().effectRenderer.addEffect(p);
        }
    }
    
}
