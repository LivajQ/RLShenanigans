package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
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
        if (event.getWorld().provider.getDimension() != 0) return;
        if(!(event.getEntity() instanceof EntityParasiteBase)) return;
        EntityParasiteBase parasite = (EntityParasiteBase) event.getEntity();
        
        if (parasite instanceof EntityPStationaryArchitect) {
            List<EntityPlayer> nearbyPlayers = parasite.world.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    parasite.getEntityBoundingBox().grow(10.0D, 3.0D, 10.0D)
            );
            
            if (nearbyPlayers.isEmpty()) {
                event.setCanceled(true);
            }
        }
    }
}
