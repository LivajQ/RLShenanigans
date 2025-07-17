package rlshenanigans.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.ModRegistry;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class ItemAmuletSinLust extends Item implements IBauble
{
    
    public ItemAmuletSinLust() {
        setRegistryName(RLShenanigans.MODID, "amulet_sin_lust");
        setTranslationKey("amulet_sin_lust");
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxStackSize(1);
    }
    
    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
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
        if (player.ticksExisted % 20 != 0) return;
        if (BaublesApi.isBaubleEquipped(player, ModRegistry.sinPendantLust) == -1) return;
        
        AxisAlignedBB box = new AxisAlignedBB(
                player.posX - 10, player.posY - 5, player.posZ - 10,
                player.posX + 10, player.posY + 5, player.posZ + 10
        );
        
        List<EntityLiving> nearbyEntities = player.world.getEntitiesWithinAABB(EntityLiving.class, box);
        
        if (nearbyEntities.size() < 3) return;
        
        float totalHealth = 0;
        for (EntityLiving entity : nearbyEntities) {
            totalHealth += entity.getHealth();
        }
        
        int amplifier = getAmplifier(totalHealth);
        
        player.addPotionEffect(new PotionEffect(
                Potion.getPotionFromResourceLocation("bountifulbaubles:sinful"), 200, amplifier, true, false
        ));
    }
    
    private static int getAmplifier(float totalHealth){
        if(totalHealth > 2000) return 5;
        if(totalHealth > 1500) return 4;
        if(totalHealth > 1000) return 3;
        if(totalHealth > 600) return 3;
        if(totalHealth > 300) return 2;
        if(totalHealth > 100) return 1;
        return 0;
    }
}