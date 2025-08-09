package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class MiscParasiteHandler
{
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityLiving)) return;
        
        if (event.getWorld().provider.getDimension() != 0) return;
        
        EntityLiving entity = (EntityLiving) event.getEntity();
        
        if (entity instanceof EntityPStationaryArchitect) {
            List<EntityPlayer> nearbyPlayers = entity.world.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    entity.getEntityBoundingBox().grow(10.0D, 3.0D, 10.0D)
            );
            
            if (nearbyPlayers.isEmpty()) {
                event.setCanceled(true);
            }
        }
    }
}
