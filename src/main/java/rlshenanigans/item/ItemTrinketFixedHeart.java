package rlshenanigans.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import cursedflames.bountifulbaubles.item.ItemTrinketBrokenHeart;
import cursedflames.bountifulbaubles.item.ModItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.RLSItemHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ItemTrinketFixedHeart extends Item implements IBauble
{
    
    public ItemTrinketFixedHeart() {
        setRegistryName(RLShenanigans.MODID, "trinket_fixed_heart");
        setTranslationKey("trinket_fixed_heart");
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxStackSize(1);
    }
    
    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.TRINKET;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for (int i = 0; i < 10; i++) {
            String key = getTranslationKey() + ".tooltip." + i;
            String line = I18n.format(key);
            if (!line.equals(key)) {
                tooltip.add(line);
            } else {
                break;
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player.world.isRemote) return;
        if (player.ticksExisted % 60 != 0) return;
        if (BaublesApi.isBaubleEquipped(player, RLSItemHandler.trinketFixedHeart) == -1) return;
        
        IAttributeInstance maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        AttributeModifier modifier = maxHealth.getModifier(ItemTrinketBrokenHeart.MODIFIER_UUID);
        
        if (modifier != null && modifier.getAmount() < 0.0) {
            double restoreAmount = 1.0;
            double newAmount = modifier.getAmount() + restoreAmount;
            
            maxHealth.removeModifier(modifier);
            
            if (newAmount < 0.0) {
                maxHealth.applyModifier(new AttributeModifier(
                        ItemTrinketBrokenHeart.MODIFIER_UUID, "Broken Heart Regen", newAmount, 0));
            }
        }
    }
    
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ModItems.spectralSilt || super.getIsRepairable(toRepair, repair);
    }
}