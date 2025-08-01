package rlshenanigans.packet;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.mixin.vanilla.EntityMixin;
import rlshenanigans.util.SizeMultiplierHelper;
import rlshenanigans.util.TamedParasiteInfo;
import rlshenanigans.util.TamedParasiteRegistry;

import java.util.List;
import java.util.UUID;

public class ParasiteTeleportPacket implements IMessage {
    private UUID mobUUID;
    
    public ParasiteTeleportPacket() {}
    
    public ParasiteTeleportPacket(UUID mobUUID) {
        this.mobUUID = mobUUID;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, mobUUID.toString());
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        mobUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }
    
    public static class Handler implements IMessageHandler<ParasiteTeleportPacket, IMessage> {
        @Override
        public IMessage onMessage(ParasiteTeleportPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            MinecraftServer server = player.getServer();
            WorldServer targetWorld = player.getServerWorld();
            
            targetWorld.addScheduledTask(() -> {
                TamedParasiteInfo info = null;
                for (WorldServer dim : server.worlds) {
                    List<TamedParasiteInfo> entries = TamedParasiteRegistry.getOwnedBy(player.getUniqueID());
                    info = entries.stream()
                            .filter(m -> m.mobUUID.equals(message.mobUUID))
                            .findFirst()
                            .orElse(info);
                    if (info != null) break;
                }
                
                if (info == null) return;
                
                
                try {
                    for (WorldServer dim : server.worlds) {
                        Entity oldEntity = dim.getEntityFromUuid(info.mobUUID);
                        TamedParasiteRegistry.untrack(info.mobUUID);
                        if (oldEntity instanceof EntityParasiteBase) {
                            oldEntity.setDead();
                        }
                    }
                    
                    EntityParasiteBase newMob = info.mobClass.getConstructor(World.class).newInstance(targetWorld);
                    newMob.enablePersistence();
                    newMob.setPosition(player.posX + 1.5, player.posY, player.posZ + 1.5);
                    targetWorld.spawnEntity(newMob);
                    
                    newMob.setCustomNameTag(info.name);
                    newMob.setAlwaysRenderNameTag(true);
                    newMob.getEntityData().setBoolean("Tamed", true);
                    newMob.getEntityData().setBoolean("PersistenceRequired", true);
                    newMob.getEntityData().setBoolean("parasitedespawn", false);
                    newMob.getEntityData().setBoolean("ParasiteDespawn", false);
                    newMob.getEntityData().setBoolean("AllowConverting", false);
                    newMob.getEntityData().setBoolean("Waiting", false);
                    newMob.setSkin(info.skin);
                    newMob.getEntityData().setUniqueId("OwnerUUID", player.getUniqueID());
                    newMob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(info.maxHealth);
                    newMob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(info.attackDamage);
                    newMob.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(info.armor);
                    newMob.setHealth((float) info.maxHealth);
                    newMob.getEntityData().setFloat("BaseWidth", info.baseWidth);
                    newMob.getEntityData().setFloat("BaseHeight", info.baseHeight);
                    if(info.sizeMultiplier < 0.25F) newMob.getEntityData().setFloat("SizeMultiplier", 0.25F);
                    else if(info.sizeMultiplier > 8.0F) newMob.getEntityData().setFloat("SizeMultiplier", 8.0F);
                    else newMob.getEntityData().setFloat("SizeMultiplier", info.sizeMultiplier);
                    float baseWidth = newMob.getEntityData().getFloat("BaseWidth");
                    float baseHeight = newMob.getEntityData().getFloat("BaseHeight");
                    float sizeMultiplier = newMob.getEntityData().getFloat("SizeMultiplier");
                    
                    if (player instanceof EntityPlayerMP) {
                        SizeMultiplierHelper.resizeEntity(newMob.getEntityWorld(), newMob.getEntityId(), (EntityPlayerMP) player,
                                sizeMultiplier,baseWidth, baseHeight, true);
                    }
                    
                    TamedParasiteRegistry.track(newMob, player);
                    
                } catch (Exception e) {
                    System.err.println("[Teleport] Failed: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            return null;
        }
    }
}