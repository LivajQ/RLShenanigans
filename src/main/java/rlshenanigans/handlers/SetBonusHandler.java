package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.potion.PotionDragonBad;
import rlshenanigans.potion.PotionGolemBad;
import rlshenanigans.potion.PotionPookie;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class SetBonusHandler
{
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;
        
        boolean goodBonus = ForgeConfigHandler.server.setBonusEnabled;
        boolean badBonus = ForgeConfigHandler.server.setBonusEnabled;
        
        EntityPlayer player = event.player;
        
        if (player.ticksExisted % 20 != 0) return;
        
        
        //########## GOOD BONUSES ##########
        
        if(ForgeConfigHandler.server.setBonusEnabled)
        {
            ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            
            boolean wearingSentient = (head.getItem() == SRPItems.armor_helmetSentient || head.getItem() == SRPItems.armor_helmet) &&
                    (chest.getItem() == SRPItems.armor_chestSentient || chest.getItem() == SRPItems.armor_chest) &&
                    (legs.getItem() == SRPItems.armor_pantsSentient || legs.getItem() == SRPItems.armor_pants) &&
                    (boots.getItem() == SRPItems.armor_bootsSentient || boots.getItem() == SRPItems.armor_boots);
            
            if (wearingSentient)
            {
                player.addPotionEffect(new PotionEffect(PotionPookie.INSTANCE, 40, 0, true, true));
            }
        }
        
        
        //########## BAD BONUSES ##########
        
        if(ForgeConfigHandler.server.badArmorDebuffsEnabled)
        {
            boolean wearingDragon = dragonDetector(player);
            if (wearingDragon)
            {
                player.addPotionEffect(new PotionEffect(PotionDragonBad.INSTANCE, 40, 0, true, true));
            }
            
            boolean wearingGolem = golemDetector(player);
            if (wearingGolem)
            {
                player.addPotionEffect(new PotionEffect(PotionGolemBad.INSTANCE, 40, 0, true, true));
            }
        }
    }
    
    public static boolean dragonDetector(EntityPlayer player) {
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET})
        {
            ItemStack armorPiece = player.getItemStackFromSlot(slot);
            if (!armorPiece.isEmpty()) {
                ResourceLocation rl = armorPiece.getItem().getRegistryName();
                if (rl != null && "iceandfire".equals(rl.getNamespace()) && rl.getPath().startsWith("armor_") && !rl.getPath().contains("metal")) {
                    
                    String suffix = "";
                    switch (slot) {
                        case HEAD:  suffix = "_helmet"; break;
                        case CHEST: suffix = "_chestplate"; break;
                        case LEGS:  suffix = "_leggings"; break;
                        case FEET:  suffix = "_boots"; break;
                    }
                    
                    if (rl.getPath().endsWith(suffix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean golemDetector(EntityPlayer player) {
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET})
        {
            ItemStack armorPiece = player.getItemStackFromSlot(slot);
            if (!armorPiece.isEmpty()) {
                ResourceLocation rl = armorPiece.getItem().getRegistryName();
                if (rl != null && "forgottenitems".equals(rl.getNamespace())) {
                    return true;
                }
            }
        }
        return false;
    }
}