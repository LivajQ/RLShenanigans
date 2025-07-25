package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.packet.OpenParasiteGuiPacket;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ParasiteInteractionListener {
    
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        EntityPlayer player = event.getEntityPlayer();
        Entity target = event.getTarget();
        
        if(player.world.isRemote) return;
        
        if (!(target instanceof EntityParasiteBase)) return;
        EntityParasiteBase parasite = (EntityParasiteBase) target;
        
        if (!parasite.getEntityData().getBoolean("Tamed")) return;
        UUID ownerId = parasite.getEntityData().getUniqueId("OwnerUUID");
        if (ownerId == null || !ownerId.equals(player.getUniqueID())) return;
        
        RLSPacketHandler.INSTANCE.sendTo(new OpenParasiteGuiPacket(parasite.getEntityId()), (EntityPlayerMP) player);
        
        event.setCanceled(true);
    }
}