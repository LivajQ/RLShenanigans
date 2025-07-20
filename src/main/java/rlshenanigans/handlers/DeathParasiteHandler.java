package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.util.ParasiteDeathMessages;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class DeathParasiteHandler
{
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event)
    {
        EntityLivingBase victim = event.getEntityLiving();
        Entity source = event.getSource().getTrueSource();
        
        if (source instanceof EntityParasiteBase)
        {
            String deathMsg = ParasiteDeathMessages.getParasiteDeathMessage(victim, (EntityParasiteBase) source);
            victim.sendMessage(new TextComponentString(deathMsg));
        }
    }
}