package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.util.ParasiteEvolutionRegistry;
import rlshenanigans.util.TamedParasiteRegistry;

import java.util.*;


@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ConversionParasiteHandler
{
    @SubscribeEvent
    public static void onStruckByLightning(EntityStruckByLightningEvent event)
    {
        Entity entity = event.getEntity();
        
        if (entity instanceof EntityParasiteBase)
        {
            boolean tamed = entity.getEntityData().getBoolean("Tamed");
            
            if (tamed)
            {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteractWithParasite(PlayerInteractEvent.EntityInteract event) {
        if(event.getWorld().isRemote) return;
       
        Entity target = event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        
        ItemStack heldItem = player.getHeldItemMainhand();
        
        if (heldItem.isEmpty() || heldItem.getItem().getRegistryName() == null) return;
        
        Set<ResourceLocation> validWands = new HashSet<>(Arrays.asList(
                new ResourceLocation("srparasites", "itemevolve"),
                new ResourceLocation("srparasites", "itemvariant"),
                new ResourceLocation("srparasites", "itemassimilate"),
                new ResourceLocation("srparasites", "itemdevolve")
        ));
        
        if (target instanceof EntityParasiteBase && validWands.contains(heldItem.getItem().getRegistryName())) {
            if (event.getHand() != EnumHand.MAIN_HAND) {
                event.setCanceled(true);
                return;
            }
            EntityParasiteBase parasite = (EntityParasiteBase) target;
            ResourceLocation itemId = heldItem.getItem().getRegistryName();
            
            if (itemId.equals(new ResourceLocation("srparasites", "itemdevolve"))) {
                if (!parasite.getEntityData().getBoolean("Tamed") && !player.capabilities.isCreativeMode) {
                    event.setCanceled(true);
                    return;
                }
                devolve(parasite, player, player.getEntityWorld());
                event.setCanceled(true);
            }
            
            if (itemId.equals(new ResourceLocation("srparasites", "itemevolve"))) {
                evolve(parasite, player, player.getEntityWorld());
                event.setCanceled(true);
            }
            
            if (itemId.equals(new ResourceLocation("srparasites", "itemassimilate"))
                    && parasite.getEntityData().getBoolean("Tamed"))
                if(parasite instanceof EntityPInfected && !(parasite instanceof EntityInfDragonE))
                    TamedParasiteRegistry.untrack(parasite.getUniqueID());
        }
    }
    
    @SubscribeEvent
    public static void onEvolvedParasiteJoin(EntityJoinWorldEvent event)
    {
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
    
    private static void evolve(EntityParasiteBase oldParasite, EntityPlayer player, World world) {
        for (ParasiteEvolutionRegistry entry : ParasiteEvolutionRegistry.EVOLUTIONS) {
            if (entry.inferiorClassName.isInstance(oldParasite)) {
                try {
                    EntityParasiteBase newParasite = (EntityParasiteBase) entry.superiorClassName
                            .getConstructor(World.class)
                            .newInstance(oldParasite.world);
                    
                    transformParasite(oldParasite, newParasite, player, world);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        String name = oldParasite.getDisplayName().getFormattedText();
        player.sendStatusMessage(new TextComponentString(name + " cannot evolve any further"), true);
    }
    
    private static void devolve(EntityParasiteBase oldParasite, EntityPlayer player, World world) {
        for (ParasiteEvolutionRegistry entry : ParasiteEvolutionRegistry.EVOLUTIONS) {
            if (entry.superiorClassName.isInstance(oldParasite)) {
                try {
                    EntityParasiteBase newParasite = (EntityParasiteBase) entry.inferiorClassName
                            .getConstructor(World.class)
                            .newInstance(oldParasite.world);
                    
                    transformParasite(oldParasite, newParasite, player, world);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        
        try {
            if (oldParasite instanceof EntityLesh) {
                oldParasite.setDead();
                TamedParasiteRegistry.untrack(oldParasite.getUniqueID());
                return;
            }
            
            EntityParasiteBase fallback = new EntityLesh(world);
            transformParasite(oldParasite, fallback, player, world);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void transformParasite(EntityParasiteBase oldParasite, EntityParasiteBase newParasite, EntityPlayer player, World world) {
        newParasite.setLocationAndAngles(oldParasite.posX, oldParasite.posY, oldParasite.posZ,
                oldParasite.rotationYaw, oldParasite.rotationPitch);
        
        EntityLightningBolt lightning = new EntityLightningBolt(world, oldParasite.posX, oldParasite.posY, oldParasite.posZ, true);
        world.addWeatherEffect(lightning);
        
        if(!oldParasite.getEntityData().getBoolean("Tamed")) {
            world.spawnEntity(newParasite);
            oldParasite.setDead();
            return;
        }
        
        TameParasiteHandler.setTags(newParasite, player, world);
        
        if (oldParasite.hasCustomName()) {
            newParasite.setCustomNameTag(oldParasite.getCustomNameTag());
            newParasite.setAlwaysRenderNameTag(oldParasite.getAlwaysRenderNameTag());
        }
        
        world.spawnEntity(newParasite);
        oldParasite.setDead();
        
        TamedParasiteRegistry.track(newParasite, player);
        TamedParasiteRegistry.untrack(oldParasite.getUniqueID());
    }
}