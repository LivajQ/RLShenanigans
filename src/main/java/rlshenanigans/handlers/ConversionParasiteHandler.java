package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

import java.util.*;


@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ConversionParasiteHandler
{
    static boolean canEvolve = false;
    static boolean tagTamed = false;
    static boolean tagPersistence = false;
    static boolean tagDespawn = true;
    static boolean tagDespawn2 = true;
    static UUID tagOwner = null;
    
    @SubscribeEvent
    public static void onStruckByLightning(EntityStruckByLightningEvent event)
    {
        Entity entity = event.getEntity();
        
        if (entity instanceof EntityParasiteBase)
        {
            boolean named = entity.hasCustomName();
            boolean allowed = entity.getEntityData().getBoolean("AllowConverting");
            
            if (named && !allowed)
            {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public static void onInteractWithParasite(PlayerInteractEvent.EntityInteract event)
    {
        Entity target = event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        
        ItemStack heldItem = player.getHeldItemMainhand();
        Set<ResourceLocation> validWands = new HashSet<>(Arrays.asList(
                new ResourceLocation("srparasites", "itemevolve"),
                new ResourceLocation("srparasites", "itemvariant"),
                new ResourceLocation("srparasites", "itemassimilate"),
                new ResourceLocation("srparasites", "itemdevolve")
        ));
        
        if (target instanceof EntityParasiteBase && validWands.contains(heldItem.getItem().getRegistryName()))
        {
            EntityParasiteBase parasite = (EntityParasiteBase) target;
            NBTTagCompound tag = parasite.getEntityData();
            
            tag.setBoolean("AllowConverting", true);
            tagTamed = tag.getBoolean("Tamed");
            tagPersistence = tag.getBoolean("PersistenceRequired");
            tagDespawn = tag.getBoolean("parasitedespawn");
            tagDespawn2 = tag.getBoolean("ParasiteDespawn");
            tagOwner = tag.getUniqueId("OwnerUUID");
            canEvolve = true;
        }
    }
    
    @SubscribeEvent
    public static void onEvolvedParasiteJoin(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityParasiteBase && canEvolve && entity.hasCustomName())
        {
            EntityParasiteBase evolved = (EntityParasiteBase) entity;
            evolved.getEntityData().setBoolean("Tamed", tagTamed);
            evolved.getEntityData().setBoolean("PersistenceRequired", tagPersistence);
            evolved.enablePersistence();
            evolved.getEntityData().setBoolean("parasitedespawn", tagDespawn);
            evolved.getEntityData().setBoolean("ParasiteDespawn", tagDespawn2);
            evolved.getEntityData().setUniqueId("OwnerUUID", tagOwner);
            
            tagTamed = false;
            tagOwner = null;
            tagPersistence = false;
            tagDespawn = true;
            tagDespawn2 = true;
            canEvolve = false;
        }
        
        if (event.getEntity() instanceof EntityFlam) {
            EntityFlam flam = (EntityFlam) event.getEntity();
            
            List<Entity> nearby = flam.world.getEntitiesWithinAABBExcludingEntity(flam, flam.getEntityBoundingBox().grow(3));
            for (Entity mob : nearby) {
                if (mob instanceof EntityPPreeminent) {
                    NBTTagCompound data = mob.getEntityData();
                    
                    if (data.getBoolean("Tamed")) {
                        flam.getEntityData().setBoolean("Tamed", true);
                        
                        if (data.hasUniqueId("OwnerUUID")) {
                            flam.getEntityData().setUniqueId("OwnerUUID", data.getUniqueId("OwnerUUID"));
                        }
                        break;
                    }
                }
            }
        }
    }
}