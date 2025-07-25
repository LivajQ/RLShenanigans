package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import rlshenanigans.RLShenanigans;
import rlshenanigans.packet.RideParasitePacket;
import rlshenanigans.proxy.ClientProxy;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID, value = Side.CLIENT)
public class ParasiteMovementListener {
    @SubscribeEvent
    public static void onInput(InputUpdateEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || !player.isRiding()) return;
        
        Entity mount = player.getRidingEntity();
        if (mount instanceof EntityParasiteBase)  {
            float forward = event.getMovementInput().moveForward;
            float strafe = event.getMovementInput().moveStrafe;
            boolean jump = event.getMovementInput().jump;
            boolean ascend = ClientProxy.keyAscend.isKeyDown();
            boolean descend = ClientProxy.keyDescend.isKeyDown();
            boolean projectile = ClientProxy.keyProjectile.isKeyDown();
            boolean sprinting = player.isSprinting();
            
            RLSPacketHandler.INSTANCE.sendToServer(new RideParasitePacket(forward, strafe, jump, sprinting, ascend, descend, projectile));
        }
    }
}