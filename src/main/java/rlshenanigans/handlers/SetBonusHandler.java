package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.potion.PotionPookie;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class SetBonusHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;
        
        EntityPlayer player = event.player;
        
        if (player.ticksExisted % 20 != 0) return;
        
        ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        
        boolean wearingSet = (head.getItem() == SRPItems.armor_helmetSentient || head.getItem() == SRPItems.armor_helmet) &&
                (chest.getItem() == SRPItems.armor_chestSentient || chest.getItem() == SRPItems.armor_chest) &&
                (legs.getItem() == SRPItems.armor_pantsSentient || legs.getItem() == SRPItems.armor_pants) &&
                (boots.getItem() == SRPItems.armor_bootsSentient || boots.getItem() == SRPItems.armor_boots);
        
        if (wearingSet) {
            player.addPotionEffect(new PotionEffect(PotionPookie.INSTANCE, 40, 0, true, true));
        }
    }
}