package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.potion.PotionPookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class PotionEventHandler
{
    private static final Map<UUID, Boolean> hadPookieEffect = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player.world.isRemote) return;
        
        UUID id = player.getUniqueID();
        boolean currentlyActive = player.isPotionActive(PotionPookie.INSTANCE);
        boolean previouslyActive = hadPookieEffect.getOrDefault(id, false);
        
        if (previouslyActive && !currentlyActive) {
            AxisAlignedBB box = player.getEntityBoundingBox().grow(48.0D);
            List<EntityParasiteBase> parasites = player.world.getEntitiesWithinAABB(EntityParasiteBase.class, box);
            
            for (EntityParasiteBase parasite : parasites) {
                NBTTagCompound tag = parasite.getEntityData();
                parasite.tasks.taskEntries.removeIf(entry ->
                        entry.action.getClass().getSimpleName().toLowerCase().contains("follow"));
                tag.setBoolean("PookieAffected", false);
            }
        }
        hadPookieEffect.put(id, currentlyActive);
    }
    
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        
        if (entity instanceof EntityParasiteBase) {
            NBTTagCompound nbt = entity.getEntityData();
            
            if (nbt.getBoolean("DropsGone")) {
                event.getDrops().clear();
                event.setCanceled(true);
            }
        }
    }
}