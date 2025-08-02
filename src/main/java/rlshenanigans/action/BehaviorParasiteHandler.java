package rlshenanigans.action;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import rlshenanigans.entity.ai.ParasiteEntityAIFollowOwner;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParticlePulsePacket;
import rlshenanigans.util.ParasiteDropList;
import rlshenanigans.util.SizeMultiplierHelper;
import rlshenanigans.util.TamedParasiteRegistry;

public class BehaviorParasiteHandler {
    
    public static void execute(EntityParasiteBase parasite, ParasiteCommand command, EntityPlayer player, float resizeValue) {
        MinecraftServer server;
        
        switch (command) {
            case FOLLOW:
                parasite.tasks.addTask(6, new ParasiteEntityAIFollowOwner(parasite, 2.0D, 10.0F, 2.0F));
                parasite.getEntityData().setBoolean("Waiting", false);
                player.sendStatusMessage(new TextComponentString("Your §dPookie §fis following."), true);
                break;
                
            case ROAM:
                parasite.getNavigator().clearPath();
                parasite.tasks.taskEntries.removeIf(entry ->
                        entry.action instanceof ParasiteEntityAIFollowOwner);
                parasite.getEntityData().setBoolean("Waiting", true);
                player.sendStatusMessage(new TextComponentString("Your §dPookie §fis roaming."), true);
                break;
            
            case GIVEITEM:
                player.sendStatusMessage(new TextComponentString("Maybe if I figure it out lol"), true);
                break;
                
            case RIDE:
                player.startRiding(parasite, true);
                break;
            
            case RIDEREVERSE:
                if (player.getPassengers().size() >= 3) {
                    player.sendStatusMessage(new TextComponentString("You can't carry any more §dPookies"), true);
                    break;
                }
                if (!player.world.isRemote) {
                    parasite.startRiding(player, true);
                    if (player instanceof EntityPlayerMP) {
                        EntityPlayerMP playerMP = (EntityPlayerMP) player;
                        playerMP.connection.sendPacket(new SPacketSetPassengers(player));
                    }
                }
                break;
                
            case RESIZE:
                if (player.world.isRemote) return;
                float baseWidth = parasite.getEntityData().getFloat("BaseWidth");
                float baseHeight = parasite.getEntityData().getFloat("BaseHeight");
                float sizeMultiplier = resizeValue;
                if(sizeMultiplier < 0.25F || sizeMultiplier > 8.0F) sizeMultiplier = 1.0F;
                
                if (player instanceof EntityPlayerMP) {
                    SizeMultiplierHelper.resizeEntity(parasite.getEntityWorld(), parasite.getEntityId(), (EntityPlayerMP) player,
                            sizeMultiplier,baseWidth, baseHeight, true);
                }
                
                server = parasite.getServer();
                if(server == null) return;
                server.addScheduledTask(() -> {
                    TamedParasiteRegistry.untrack(parasite.getUniqueID());
                    TamedParasiteRegistry.track(parasite, player);
                });
                
                break;
            
            case SMOOCH:
                RLSPacketHandler.INSTANCE.sendToAll(
                        new ParticlePulsePacket(parasite, EnumParticleTypes.HEART, 100, 30)
                );
                
                parasite.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, 2));
                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, 2));
                break;
                
            case ASKFORDROP:
                if(parasite.world.isRemote) return;
                NBTTagCompound data = parasite.getEntityData();
                long currentTime = parasite.world.getTotalWorldTime();
                long lastDrop = data.getLong("LastDropTime");
                
                if (currentTime - lastDrop < 6000) {
                    player.sendStatusMessage(new TextComponentString("You must wait before asking your §dPookie §ffor a drop"), true);
                    break;
                }
                
                ItemStack drop = ParasiteDropList.getDrops(parasite);
                if (drop != null && !drop.isEmpty()) {
                    parasite.entityDropItem(drop, 0.5F);
                    player.sendStatusMessage(new TextComponentString("Your §dPookie §fdropped: §a" + drop.getDisplayName()), true);
                } else {
                    player.sendStatusMessage(new TextComponentString("Your §dPookie §fhas nothing to give this time"), true);
                }
                data.setLong("LastDropTime", currentTime);
                
                server = parasite.getServer();
                if(server == null) return;
                server.addScheduledTask(() -> {
                    TamedParasiteRegistry.untrack(parasite.getUniqueID());
                    TamedParasiteRegistry.track(parasite, player);
                });
                
                break;
            
            case SELF_DESTRUCT:
                World world = parasite.world;
                double x = parasite.posX;
                double y = parasite.posY;
                double z = parasite.posZ;
                
                world.createExplosion(parasite, x, y, z, 200.0F, true);
                break;
        }
    }
}