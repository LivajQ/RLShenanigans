package rlshenanigans.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

import java.util.Random;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class CustomMobSpawnHandler {
    
    private static final Random RANDOM = new Random();
    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getWorld().isRemote) return;
       
        if (event.getSpawner() != null) return;
        
        if ((event.getEntity() instanceof EntityZombie)) {
            if(rollChance(10))
            {
                EntityZombie strengthMain = (EntityZombie) event.getEntity();
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModRegistry.weaponZweihander));
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ModRegistry.weaponZweihander));
                strengthMain.addPotionEffect(new PotionEffect(MobEffects.SPEED, Integer.MAX_VALUE, 2));
                strengthMain.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
                strengthMain.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0D);
                strengthMain.setHealth(strengthMain.getMaxHealth());
                strengthMain.setCustomNameTag("§c§lSTRENGTH MAIN");
                strengthMain.enablePersistence();
                return;
            }
        }
    }
    
    private static boolean rollChance(int weight) {
        return RANDOM.nextInt(1000) < weight;
    }
}
