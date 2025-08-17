package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.packet.RideMobPacket;
import rlshenanigans.proxy.ClientProxy;
import rlshenanigans.util.RibeMobUtils;

@Mod.EventBusSubscriber
public class RideMobHandler extends RibeMobUtils {
    
    private static boolean eyeHeightChanged = false;
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onInput(InputUpdateEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || !player.isRiding()) return;
        if (!(player.getRidingEntity() instanceof EntityLiving)) return;
        
        float forward = event.getMovementInput().moveForward;
        float strafe = event.getMovementInput().moveStrafe;
        boolean jump = event.getMovementInput().jump;
        boolean sprinting = player.isSprinting();
        boolean ascend = ClientProxy.keyAscend.isKeyDown();
        boolean descend = ClientProxy.keyDescend.isKeyDown();
        boolean projectile = ClientProxy.keyProjectile.isKeyDown();
        
        RLSPacketHandler.INSTANCE.sendToServer(new RideMobPacket(forward, strafe, jump, sprinting, ascend, descend, projectile));
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        
        if (player != null && !player.getPassengers().isEmpty())
        {
            player.eyeHeight = player.getDefaultEyeHeight() - 0.42F;
            eyeHeightChanged = true;
            return;
        }
        
        if (player != null && player.isRiding() && player.getRidingEntity() instanceof EntityParasiteBase){
            parasiteCorrectEyeHeight(player);
            eyeHeightChanged = true;
            return;
        }
        if (player != null && eyeHeightChanged) {
            player.eyeHeight = player.getDefaultEyeHeight();
            eyeHeightChanged = false;
        }
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side != Side.SERVER) return;
        
        EntityPlayer player = event.player;
        
        if (player.isSneaking() && !player.getPassengers().isEmpty()) {
            for (Entity passenger : player.getPassengers()) {
                passenger.dismountRidingEntity();
            }
            
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetPassengers(player));
            }
        }
    }
    
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;
        
        for (Entity entity : event.world.loadedEntityList)
        {
            if (!(entity instanceof EntityLiving)) continue;
            EntityLiving mob = (EntityLiving) entity;
            if (!isValidMob(mob)) continue;
            
            if (mob.getPassengers().isEmpty()) continue;
            
            if (!(mob.getPassengers().get(0) instanceof EntityPlayer)) continue;
            
            EntityPlayer player = (EntityPlayer) mob.getPassengers().get(0);
            
            float yaw = player.rotationYaw;
            float pitch = player.rotationPitch;
            
            mob.rotationYaw = yaw;
            mob.prevRotationYaw = yaw;
            mob.rotationYawHead = yaw;
            mob.prevRotationYawHead = yaw;
            mob.renderYawOffset = yaw;
            mob.prevRenderYawOffset = yaw;
            mob.setRotationYawHead(yaw);
            mob.rotationPitch = pitch;
            mob.prevRotationPitch = pitch;
        }
    }
    
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLiving)) return;
        if (!isValidMob((EntityLiving) event.getEntityLiving())) return;
        EntityLiving mob = (EntityLiving) event.getEntityLiving();
        
        purgeAttackCooldown(mob);
    }
    
    @SubscribeEvent
    public static void onSetAttackTarget(LivingSetAttackTargetEvent event) {
        if (!(event.getEntityLiving() instanceof EntityLiving)) return;
        if (!isValidMob((EntityLiving) event.getEntityLiving())) return;
        EntityLiving mob = (EntityLiving) event.getEntityLiving();
        if(mob.getAttackTarget() == null) return;
        if (mob.getPassengers().isEmpty()) return;
        if (!(mob.getPassengers().get(0) instanceof EntityPlayer)) return;
        
        mob.setAttackTarget(null);
    }
    
    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (!(event.getEntityBeingMounted() instanceof EntityLiving)) return;
        if (!isValidMob((EntityLiving) event.getEntityBeingMounted())) return;
        
        EntityLiving mob = (EntityLiving) event.getEntityBeingMounted();
        
        if (event.isMounting()) {
            double original = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
            mob.getEntityData().setDouble("OriginalSpeed", original);
            mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
            if (mob.getAttackTarget() != null) mob.setAttackTarget(null);
        } else {
            purgeAttackCooldown(mob);
            
            double origSpeed = mob.getEntityData().getDouble("OriginalSpeed");
            if (origSpeed > 0) mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(origSpeed);
            else mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4F);
        }
    }
    
    public static class Handler implements IMessageHandler<RideMobPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RideMobPacket msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            player.getServerWorld().addScheduledTask(() ->
            {
                Entity mob = player.getRidingEntity();
                if (!(mob instanceof EntityLiving)) return;
                
                EntityLiving mount = (EntityLiving) mob;
                if(!isValidMob(mount)) return;
                
                double speed = msg.sprinting ? 1.4D : 0.4D;
                float yaw = player.rotationYaw;
                
                double motionX = (-Math.sin(Math.toRadians(yaw)) * msg.forward + Math.cos(Math.toRadians(yaw)) * msg.strafe) * speed;
                double motionZ = (Math.cos(Math.toRadians(yaw)) * msg.forward + Math.sin(Math.toRadians(yaw)) * msg.strafe) * speed;
                
                mount.motionX = motionX;
                mount.motionZ = motionZ;
                
                if (msg.jump && mount.onGround) {
                    mount.motionY = 0.6D;
                    mount.velocityChanged = true;
                }
                
                if (msg.projectile) {
                    if(!(mount instanceof EntityParasiteBase)) return;
                    fireParasiteProjectile(mount);
                }
                
                boolean canFly = mount.tasks.taskEntries.stream()
                        .map(task -> task.action.getClass().getSimpleName().toLowerCase())
                        .anyMatch(name -> name.contains("fly") || name.contains("flight"))
                        || mount.getNavigator() instanceof PathNavigateFlying;
                
                if (canFly) {
                    if (msg.ascend) {
                        mount.motionY = 0.5D;
                        mount.velocityChanged = true;
                    }
                    if (msg.descend && !mount.onGround) {
                        mount.motionY = -0.4D;
                        mount.velocityChanged = true;
                    }
                }
            
            });
            return null;
        }
    }
}