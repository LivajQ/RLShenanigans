package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.potion.PotionPookie;
import rlshenanigans.potion.PotionStagger;

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
        boolean pookieCurrentlyActive = player.isPotionActive(PotionPookie.INSTANCE);
        boolean pookiePreviouslyActive = hadPookieEffect.getOrDefault(id, false);
        
        if (pookiePreviouslyActive && !pookieCurrentlyActive) {
            AxisAlignedBB box = player.getEntityBoundingBox().grow(48.0D);
            List<EntityParasiteBase> parasites = player.world.getEntitiesWithinAABB(EntityParasiteBase.class, box);
            
            for (EntityParasiteBase parasite : parasites) {
                NBTTagCompound tag = parasite.getEntityData();
                parasite.tasks.taskEntries.removeIf(entry ->
                        entry.action.getClass().getSimpleName().toLowerCase().contains("follow"));
                tag.setBoolean("PookieAffected", false);
            }
        }
        hadPookieEffect.put(id, pookieCurrentlyActive);
    }
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        
        if (!(entity instanceof EntityPlayer) && entity.isPotionActive(PotionStagger.INSTANCE)) {
            
            entity.motionX = 0;
            entity.motionZ = 0;
            if (entity.motionY > 0) entity.motionY = 0;
            entity.jumpMovementFactor = 0;
            entity.velocityChanged = true;
        }
    }
    
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            if (attacker.isPotionActive(PotionStagger.INSTANCE)) {
                event.setCanceled(true);
            }
        }
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
    
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getEntity().world.isRemote) return;
        if (event.getEntityPlayer().isPotionActive(PotionStagger.INSTANCE)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            
            if (player.isPotionActive(PotionStagger.INSTANCE)) {
                player.motionY = 0;
            }
        }
    }
    
    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            if (player != null) {
                if (player.isPotionActive(PotionStagger.INSTANCE)){
                    event.setDuration(-1);
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onBlock(PlayerInteractEvent.RightClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player != null)
        {
            if (player.isPotionActive(PotionStagger.INSTANCE))
            {
                event.setUseBlock(Event.Result.DENY);
                event.setUseItem(Event.Result.DENY);
            }
        }
    }
}

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
class Client
{
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        if (player.isPotionActive(PotionStagger.INSTANCE))
        {
            player.motionX = 0;
            player.motionZ = 0;
            if (player.motionY > 0) player.motionY = 0;
            player.jumpMovementFactor = 0;
            player.setVelocity(0, player.motionY, 0);
            player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                    .setBaseValue(0.0D);
            player.swingProgress = 0;
            player.swingProgressInt = 0;
            player.isSwingInProgress = false;
        }
    }
}