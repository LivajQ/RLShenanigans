package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.github.alexthe666.iceandfire.core.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
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
        
        if(!ForgeConfigHandler.server.customMobsEnabled) return;
        
        Entity entity = event.getEntity();
        
        if (entity instanceof EntityZombie) {
            if(rollChance(10)) {
                EntityZombie strengthMain = (EntityZombie) event.getEntity();
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModRegistry.weaponZweihander));
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ModRegistry.weaponZweihander));
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ModItems.deathworm_white_helmet));
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ModItems.deathworm_white_chestplate));
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ModItems.deathworm_white_leggings));
                strengthMain.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ModItems.deathworm_white_boots));
                strengthMain.addPotionEffect(new PotionEffect(MobEffects.SPEED, Integer.MAX_VALUE, 2));
                strengthMain.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
                strengthMain.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0D);
                strengthMain.setHealth(strengthMain.getMaxHealth());
                strengthMain.setCustomNameTag("§c§lSTRENGTH MAIN");
                strengthMain.enablePersistence();
                return;
            }
        }
        
        if (entity instanceof EntitySkeleton) {
            if(rollChance(10)) {
                EntityNogla reeker = new EntityNogla(entity.world);
                reeker.setPosition(entity.posX, entity.posY, entity.posZ);
                entity.world.spawnEntity(reeker);
                reeker.setCustomNameTag("§dI lost my hive :<");
                reeker.enablePersistence();
                reeker.getEntityData().setBoolean("ParasiteDespawn", false);
                
                entity.setDead();
                return;
            }
        }
        
        if (entity instanceof EntitySpider) {
            if (rollChance(10)) {
                EntityLivingBase base = (EntityLivingBase) entity;
                
                for (int i = 0; i < 5; i++) {
                    EntityMudo rupter = new EntityMudo(entity.world);
                    rupter.setPosition(entity.posX, entity.posY, entity.posZ);
                    entity.world.spawnEntity(rupter);
                    rupter.setCustomNameTag("§d§lCOLUMN");
                    rupter.enablePersistence();
                    rupter.getEntityData().setBoolean("ParasiteDespawn", false);
                    
                    rupter.startRiding(base, true);
                    base = rupter;
                }

                return;
            }
        }
    }
    
    private static boolean rollChance(int weight) {
        return RANDOM.nextInt(1000) < weight;
    }
}
