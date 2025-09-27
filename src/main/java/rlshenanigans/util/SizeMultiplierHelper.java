package rlshenanigans.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.mixin.vanilla.EntityAccessor;
import rlshenanigans.packet.SizeMultiplierPacket;

public final class SizeMultiplierHelper {
    private SizeMultiplierHelper() {}
    
    private static float sanitizeMultiplier(float m) {
        if (Float.isNaN(m) || m <= 0f) return 1.0f;
        if (m < 0.25f) return 0.25f;
        if (m > 8.0f) return 8.0f;
        return m;
    }
    
    public static void resizeEntity(World world, int entityId, EntityPlayerMP player, float sizeMultiplier, float baseWidth, float baseHeight, boolean global) {
        Entity entity = world.getEntityByID(entityId);
        if (!(entity instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) entity;
        
        sizeMultiplier = sanitizeMultiplier(sizeMultiplier);
        float scaledWidth = baseWidth * sizeMultiplier;
        float scaledHeight = baseHeight * sizeMultiplier;
        
        ((EntityAccessor) living).invokeSetSize(scaledWidth, scaledHeight);
        
        living.getEntityData().setFloat("SizeMultiplier", sizeMultiplier);
        
        living.setPosition(living.posX, living.posY, living.posZ);
        living.velocityChanged = true;
        
        double halfWidth = scaledWidth / 2.0;
        AxisAlignedBB newBox = new AxisAlignedBB(
                living.posX - halfWidth,
                living.posY,
                living.posZ - halfWidth,
                living.posX + halfWidth,
                living.posY + scaledHeight,
                living.posZ + halfWidth
        );
        living.setEntityBoundingBox(newBox);
        

        if (global) {
            RLSPacketHandler.INSTANCE.sendToAll(new SizeMultiplierPacket(living.getEntityId(), sizeMultiplier, baseWidth, baseHeight));
        }
        else if (player != null) RLSPacketHandler.INSTANCE.sendTo(new SizeMultiplierPacket(living.getEntityId(), sizeMultiplier, baseWidth, baseHeight), player);
    }
}