package rlshenanigans.potion;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.ai.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PotionPookie extends PotionBase {
    
    public static final PotionPookie INSTANCE = new PotionPookie();
    public static final double EFFECT_RADIUS = 48.0D;
    private static final Map<UUID, Boolean> hadPookieEffect = new HashMap<>();
    
    public PotionPookie() {
        super("Pookie", false, 0xFFC0CB);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 10 == 0;
    }
    
    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living.world.isRemote || !(living instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) living;
        World world = player.world;
        
        List<EntityParasiteBase> parasites = world.getEntitiesWithinAABB(
                EntityParasiteBase.class,
                player.getEntityBoundingBox().grow(EFFECT_RADIUS)
        );
        
        for (EntityParasiteBase parasite : parasites) {
            NBTTagCompound tag = parasite.getEntityData();
            
            if (tag.getBoolean("Tamed")) continue;
            
            parasite.targetTasks.taskEntries.removeIf(entry ->
            {
                String name = entry.action.getClass().getSimpleName().toLowerCase();
                return name.contains("near");
            });
            
            parasite.targetTasks.taskEntries.removeIf(entry ->
            {
                String name = entry.action.getClass().getSimpleName().toLowerCase();  //comment out to enable retaliation
                return name.contains("hurt");
            });
            
            boolean hasFollowTask = parasite.tasks.taskEntries.stream().anyMatch(entry ->
                    entry.action instanceof RLSEntityAIFollow
            );
            
           if(!hasFollowTask)
           {
               parasite.tasks.addTask(6, new RLSEntityAIFollow(parasite, player, 1.0D, 2.0F, 12.0F));
           }
            
            if (parasite.getAttackTarget() == player || parasite.getRevengeTarget() == player) {
                parasite.setAttackTarget(null);
                parasite.setRevengeTarget(null);
            }
            
            if (!tag.getBoolean("PookieAffected")) {
                tag.setBoolean("PookieAffected", true);
                tag.setBoolean("DropsGone", true);
            }
        }
    }
    
    @Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
    static class Handler {
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            EntityPlayer player = event.player;
            if (player.world.isRemote) return;
            
            UUID id = player.getUniqueID();
            boolean pookieCurrentlyActive = player.isPotionActive(PotionPookie.INSTANCE);
            boolean pookiePreviouslyActive = hadPookieEffect.getOrDefault(id, false);
            
            if (pookiePreviouslyActive && !pookieCurrentlyActive) {
                AxisAlignedBB box = player.getEntityBoundingBox().grow(48.0D);
                List<EntityParasiteBase> parasites = player.world.getEntitiesWithinAABB(EntityParasiteBase.class, box);
                
                for (EntityParasiteBase parasite : parasites) {
                    NBTTagCompound tag = parasite.getEntityData();
                    parasite.tasks.taskEntries.removeIf(entry ->
                            entry.action.getClass().getSimpleName().toLowerCase().contains("follow"));
                    if (tag.getBoolean("PookieAffected")) tag.setBoolean("PookieAffected", false);
                }
            }
            hadPookieEffect.put(id, pookieCurrentlyActive);
        }
        
        @SubscribeEvent
        public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
            hadPookieEffect.remove(event.player.getUniqueID());
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
}