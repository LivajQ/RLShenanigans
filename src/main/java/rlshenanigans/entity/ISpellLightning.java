package rlshenanigans.entity;

import net.minecraft.entity.EntityLivingBase;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.SyncLightningConnectionsPacket;

import javax.vecmath.Color4f;
import java.util.Map;
import java.util.Set;

public interface ISpellLightning {
    Map<EntityLivingBase, Set<EntityLivingBase>> getConnections();
    
    Color4f getColor();
    
    default void syncConnections(EntitySpellBase spellEntity) {
        RLSPacketHandler.INSTANCE.sendToAllTracking(new SyncLightningConnectionsPacket(spellEntity.getEntityId(), getConnections()), spellEntity);
    }
}