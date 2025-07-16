package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.packet.RideParasitePacket;

public class ParasiteMovementListener {
    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || !player.isRiding()) return;
        
        Entity mount = player.getRidingEntity();
        if (mount instanceof EntityParasiteBase)  {
            float forward = event.getMovementInput().moveForward;
            float strafe = event.getMovementInput().moveStrafe;
            boolean jump = event.getMovementInput().jump;
            boolean ascend = RideParasiteHandler.keyAscend.isKeyDown();
            boolean descend = RideParasiteHandler.keyDescend.isKeyDown();
            boolean projectile = RideParasiteHandler.keyProjectile.isKeyDown();
            boolean sprinting = player.isSprinting();
            
            RLSPacketHandler.INSTANCE.sendToServer(new RideParasitePacket(forward, strafe, jump, sprinting, ascend, descend, projectile));
        }
    }
}