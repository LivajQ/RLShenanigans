package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCrux;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.*;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.packet.RideParasitePacket;
import rlshenanigans.util.ProjectileLauncher;

import java.lang.reflect.Constructor;
import java.util.*;

import static rlshenanigans.util.ParasiteRegistry.RANGED_PARASITES;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RideParasiteHandler {
    private static final Map<UUID, Double> originalSpeeds = new HashMap<>();
    private static final Map<UUID, Long> attackCooldowns = new HashMap<>();
    private static boolean eyeHeightChanged = false;
    
    public static class Handler implements IMessageHandler<RideParasitePacket, IMessage> {
        @Override
        public IMessage onMessage(RideParasitePacket msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            
            player.getServerWorld().addScheduledTask(() -> {
                Entity mount = player.getRidingEntity();
                if (!(mount instanceof EntityParasiteBase)) return;
                
                double speed = msg.sprinting ? 1.4D : 0.25D;
                float yaw = player.rotationYaw;
                float pitch = player.rotationPitch;
                
                double motionX = (-Math.sin(Math.toRadians(yaw)) * msg.forward + Math.cos(Math.toRadians(yaw)) * msg.strafe) * speed;
                double motionZ = (Math.cos(Math.toRadians(yaw)) * msg.forward + Math.sin(Math.toRadians(yaw)) * msg.strafe) * speed;
                
                mount.rotationYaw = yaw;
                mount.prevRotationYaw = yaw;
                mount.rotationPitch = pitch;
                mount.prevRotationPitch = pitch;
                
                if (mount instanceof EntityLiving) {
                    EntityLiving living = (EntityLiving) mount;
                    living.rotationYawHead = yaw;
                    living.prevRotationYawHead = yaw;
                    living.renderYawOffset = yaw;
                    living.prevRenderYawOffset = yaw;
                    living.setRotationYawHead(yaw);
                    living.getNavigator().clearPath();
                }
                
                mount.motionX = motionX;
                mount.motionZ = motionZ;
                
                if (msg.jump && mount.onGround) {
                    mount.motionY = 0.6D;
                    mount.velocityChanged = true;
                }
                
                if (msg.projectile) {
                    if (mount instanceof EntityLivingBase) {
                        ProjectileLauncher launcher = RANGED_PARASITES.get(mount.getClass());
                        fireParasiteProjectile((EntityLivingBase) mount, launcher);
                    }
                }
                
                if (mount instanceof EntityLiving) {
                    boolean canFly = ((EntityLiving) mount).tasks.taskEntries.stream()
                            .map(task -> task.action.getClass().getSimpleName().toLowerCase())
                            .anyMatch(name -> name.contains("flight"));
                    
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
                }
            });
            return null;
        }
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        
        
        if (player != null && !player.getPassengers().isEmpty())
        {
            player.eyeHeight = player.getDefaultEyeHeight() - 0.42F;
            eyeHeightChanged = true;
            return;
        }
        
        if (player != null && player.isRiding() && player.getRidingEntity() instanceof EntityParasiteBase){
            if (player.getRidingEntity() instanceof EntityInfDragonE) player.eyeHeight = 3.5F;
            if (player.getRidingEntity() instanceof EntityCrux) player.eyeHeight = 2.5F;
            if (player.getRidingEntity() instanceof EntityMes) player.eyeHeight = 2.0F;
            if (player.getRidingEntity() instanceof EntityAlafha) player.eyeHeight = 3.0F;
            if (player.getRidingEntity() instanceof EntityVenkrol) player.eyeHeight = 2.0F;
            if (player.getRidingEntity() instanceof EntityVenkrolSII) player.eyeHeight = 2.5F;
            if (player.getRidingEntity() instanceof EntityVenkrolSIII) player.eyeHeight = 2.5F;
            if (player.getRidingEntity() instanceof EntityVenkrolSIV) player.eyeHeight = 3.5F;
            if (player.getRidingEntity() instanceof EntityDodSIV) player.eyeHeight = 5.0F;
            if (player.getRidingEntity() instanceof EntityGanro) player.eyeHeight = 2.5F;
            if (player.getRidingEntity() instanceof EntityEsor) player.eyeHeight = 2.5F;
            if (player.getRidingEntity() instanceof EntityJinjo) player.eyeHeight = 6.5F;
            if (player.getRidingEntity() instanceof EntityPheon) player.eyeHeight = 10.0F;
            if (player.getRidingEntity() instanceof EntityVesta) player.eyeHeight = 4.0F;
            if (player.getRidingEntity() instanceof EntityOronco) player.eyeHeight = 5.5F;
            if (player.getRidingEntity() instanceof EntityTerla) player.eyeHeight = 10.0F;
            eyeHeightChanged = true;
            return;
        }
        if (player != null && eyeHeightChanged) {
            player.eyeHeight = player.getDefaultEyeHeight();
            eyeHeightChanged = false;
        }
    }
    
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        for (Entity entity : event.world.loadedEntityList) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            
            EntityLiving parasite = (EntityLiving) entity;
            UUID id = parasite.getUniqueID();
            
            if (parasite.isBeingRidden()) {
                if (!originalSpeeds.containsKey(id)) {
                    double original = parasite.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
                    originalSpeeds.put(id, original);
                }
                parasite.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
                
                if (parasite.getNavigator().getPath() != null) {
                    parasite.getNavigator().clearPath();
                }
                
                Entity controller = parasite;
                while (!controller.getPassengers().isEmpty()) {
                    controller = controller.getPassengers().get(0);
                }
                
                if (controller instanceof EntityPlayer) {
                    float yaw = controller.rotationYaw;
                    
                    parasite.rotationYaw = yaw;
                    parasite.prevRotationYaw = yaw;
                    parasite.rotationYawHead = yaw;
                    parasite.prevRotationYawHead = yaw;
                    parasite.renderYawOffset = yaw;
                    parasite.prevRenderYawOffset = yaw;
                    parasite.setRotationYawHead(yaw);
                }
            }
            else {
                if (originalSpeeds.containsKey(id)) {
                    parasite.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                            .setBaseValue(originalSpeeds.remove(id));
                }
            }
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
    
    public static void fireParasiteProjectile(EntityLivingBase shooter, ProjectileLauncher launcher) {
        if (shooter.world.isRemote) return;
        
        UUID id = shooter.getUniqueID();
        long now = System.currentTimeMillis();
        
        if (shooter instanceof EntityJinjo) {
            long last = attackCooldowns.getOrDefault(id, 0L);
            long jinjoCooldown = 1500L;
            if (now - last < jinjoCooldown) return;
            attackCooldowns.put(id, now);
            
            EntityJinjo jinjo = (EntityJinjo) shooter;
            EntityBomb bomb = new EntityBomb(jinjo.world, jinjo, SRPConfigMobs.jinjoGriefing);
            
            bomb.copyLocationAndAnglesFrom(jinjo);
            bomb.setFuse(80);
            bomb.setStren(4.0F);
            bomb.setSkin(2);
            bomb.setDamage(
                    (float) jinjo.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()
                            * SRPConfigMobs.jinjoExplotionMult, 7);
            bomb.updateSTR();
            
            jinjo.world.spawnEntity(bomb);
            jinjo.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
            return;
        }
        
        if (shooter instanceof EntityOmboo) {
            long last = attackCooldowns.getOrDefault(id, 0L);
            long ombooCooldown = 1500L;
            if (now - last < ombooCooldown) return;
            attackCooldowns.put(id, now);
            
            EntityOmboo omboo = (EntityOmboo) shooter;
            EntityBomb bomb = new EntityBomb(omboo.world, omboo, SRPConfigMobs.ombooGriefing);
            
            bomb.copyLocationAndAnglesFrom(omboo);
            bomb.setFuse(80);
            bomb.setStren(1.0F);
            bomb.setSkin(1);
            bomb.setDamage((float) SRPAttributes.OMBOO_BOMBDAMAGE, 4);
            bomb.updateSTR();
            
            omboo.world.spawnEntity(bomb);
            omboo.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
            return;
        }
        
        long last = attackCooldowns.getOrDefault(id, 0L);
        if (now - last < launcher.cooldown) return;
        attackCooldowns.put(id, now);
        
        Vec3d look = shooter.getLook(1.0F);
        double forwardOffset = 2.0;
        double x = shooter.posX + look.x * forwardOffset;
        double y = shooter.posY + shooter.getEyeHeight() - 0.2 + look.y * forwardOffset;
        double z = shooter.posZ + look.z * forwardOffset;
        
        try {
            Constructor<? extends Entity> constructor = launcher.projectileClass.getConstructor(
                    World.class, EntityLivingBase.class, double.class, double.class, double.class);
            
            Entity projectile = constructor.newInstance(shooter.world, shooter, look.x, look.y, look.z);
            projectile.setPosition(x, y, z);
            
            projectile.motionX = look.x;
            projectile.motionY = look.y;
            projectile.motionZ = look.z;
            
            shooter.world.spawnEntity(projectile);
            shooter.playSound(launcher.sound, launcher.volume, launcher.pitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}