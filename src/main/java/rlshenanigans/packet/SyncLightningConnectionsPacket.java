package rlshenanigans.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.entity.spell.ISpellLightning;

import java.util.*;
import java.util.stream.Collectors;

public class SyncLightningConnectionsPacket implements IMessage {
    private int entityId;
    private Map<Integer, List<Integer>> connections = new HashMap<>();
    
    public SyncLightningConnectionsPacket() {}
    
    public SyncLightningConnectionsPacket(int entityId, Map<EntityLivingBase, Set<EntityLivingBase>> source) {
        this.entityId = entityId;
        if (source != null) {
            for (Map.Entry<EntityLivingBase, Set<EntityLivingBase>> entry : source.entrySet()) {
                EntityLivingBase parent = entry.getKey();
                if (parent == null) continue;
                
                List<Integer> childIds = entry.getValue().stream()
                        .filter(Objects::nonNull)
                        .map(Entity::getEntityId)
                        .collect(Collectors.toList());
                
                connections.put(parent.getEntityId(), childIds);
            }
        }
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(connections.size());
        for (Map.Entry<Integer, List<Integer>> entry : connections.entrySet()) {
            buf.writeInt(entry.getKey());
            buf.writeInt(entry.getValue().size());
            for (int id : entry.getValue()) buf.writeInt(id);
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        int parentCount = buf.readInt();
        connections = new HashMap<>();
        for (int i = 0; i < parentCount; i++) {
            int parentId = buf.readInt();
            int childCount = buf.readInt();
            List<Integer> childIds = new ArrayList<>(childCount);
            for (int j = 0; j < childCount; j++) childIds.add(buf.readInt());
            connections.put(parentId, childIds);
        }
    }
    
    public static class Handler implements IMessageHandler<SyncLightningConnectionsPacket, IMessage> {
        @Override
        public IMessage onMessage(SyncLightningConnectionsPacket msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Entity entity = world.getEntityByID(msg.entityId);
                if (entity instanceof ISpellLightning) {
                    ISpellLightning spell = (ISpellLightning) entity;
                    Map<EntityLivingBase, Set<EntityLivingBase>> map = new HashMap<>();
                    
                    for (Map.Entry<Integer, List<Integer>> entry : msg.connections.entrySet()) {
                        Entity parent = world.getEntityByID(entry.getKey());
                        if (!(parent instanceof EntityLivingBase)) continue;
                        
                        Set<EntityLivingBase> children = entry.getValue().stream()
                                .map(world::getEntityByID)
                                .filter(e -> e instanceof EntityLivingBase)
                                .map(e -> (EntityLivingBase) e)
                                .collect(Collectors.toSet());
                        
                        map.put((EntityLivingBase) parent, children);
                    }
                    
                    spell.getConnections().clear();
                    spell.getConnections().putAll(map);
                }
            });
            return null;
        }
    }
}