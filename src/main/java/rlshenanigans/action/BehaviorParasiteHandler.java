package rlshenanigans.action;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import rlshenanigans.entity.ai.ParasiteEntityAIFollowOwner;
import rlshenanigans.util.ParasiteDropList;

public class BehaviorParasiteHandler {
    
    public static void execute(EntityParasiteBase parasite, ParasiteCommand command, EntityPlayer player) {
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
            
            case RIDE:
                player.startRiding(parasite, true);
                break;
                
            case ASKFORDROP:
                NBTTagCompound data = parasite.getEntityData();
                long currentTime = parasite.ticksExisted;
                long lastDrop = data.getLong("LastDropTime");
                
                if (currentTime - lastDrop < 12000) {
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