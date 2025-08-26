package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.github.alexthe666.iceandfire.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.ai.RLSEntityAIFollow;
import rlshenanigans.packet.ParticlePulsePacket;

import static rlshenanigans.handlers.ForgeConfigHandler.customMobSpawn;

import java.util.Random;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class CustomMobHandler {
    private static final ResourceLocation LIBRARIAN = new ResourceLocation("minecraft:librarian");
    private static final ResourceLocation CARTOGRAPHER = new ResourceLocation("minecraft:cartographer");
    private static final Random RANDOM = new Random();
    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getWorld().isRemote) return;
       
        if (event.getSpawner() != null) return;
        
        Entity entity = event.getEntity();
        
        if (entity.getEntityData().getBoolean("RLSCustomChecked")) return;
        
        if (entity instanceof EntityZombie) {
            entity.getEntityData().setBoolean("RLSCustomChecked", true);
            
            if(rollChance(customMobSpawn.strengthMainSpawnChance)) {
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
        
        if (entity instanceof EntityPigZombie) {
            entity.getEntityData().setBoolean("RLSCustomChecked", true);
            
            if(rollChance(customMobSpawn.lostMyHiveChance)) {
                EntityNogla reeker = new EntityNogla(entity.world);
                reeker.setPosition(entity.posX, entity.posY, entity.posZ);
                entity.world.spawnEntity(reeker);
                reeker.setCustomNameTag("§dI lost my hive :<");
                reeker.enablePersistence();
                reeker.getEntityData().setBoolean("parasitedespawn", false);
                
                entity.setDead();
                return;
            }
        }
        
        if (entity instanceof EntityEnderman && entity.world.provider.getDimension() != 0) {
            entity.getEntityData().setBoolean("RLSCustomChecked", true);
            
            if (rollChance(customMobSpawn.columnChance)) {
                EntityLivingBase base = (EntityLivingBase) entity;
                
                for (int i = 0; i < 5; i++) {
                    EntityMudo rupter = new EntityMudo(entity.world);
                    rupter.setPosition(entity.posX, entity.posY, entity.posZ);
                    entity.world.spawnEntity(rupter);
                    rupter.setCustomNameTag("§d§lCOLUMN");
                    rupter.enablePersistence();
                    rupter.getEntityData().setBoolean("parasitedespawn", false);
                    
                    rupter.startRiding(base, true);
                    base = rupter;
                }

                return;
            }
        }
    }
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (event.getEntityLiving().ticksExisted != 2) return;
        if (event.getEntityLiving().getEntityData().getBoolean("RLSCustomChecked")) return;
        
        if (event.getEntityLiving() instanceof EntityVillager) {
            event.getEntityLiving().getEntityData().setBoolean("RLSCustomChecked", true);
            
            EntityVillager villager = (EntityVillager) event.getEntityLiving();
            VillagerRegistry.VillagerProfession profession = villager.getProfessionForge();
            if (!LIBRARIAN.equals(profession.getRegistryName()) && !CARTOGRAPHER.equals(profession.getRegistryName())) return;
            
            if (rollChance(customMobSpawn.freakyberianChance)) {
                villager.setCustomNameTag("Freakyberian");
                villager.getEntityData().setBoolean("IsFreakyberian", true);
            }
        }
    }
    
    @SubscribeEvent
    public static void onFreakyberianInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof EntityVillager)) return;
        
        EntityVillager freakyberian = (EntityVillager) event.getTarget();
        if (!freakyberian.getEntityData().getBoolean("IsFreakyberian")) return;
        event.setCanceled(true);
        if (freakyberian.getEntityData().getBoolean("FreakyberianTriggered")) return;
        freakyberian.getEntityData().setBoolean("FreakyberianTriggered", true);
        if (freakyberian.getGrowingAge() < 0) freakyberian.setGrowingAge(0);
        
        EntityPlayer player = event.getEntityPlayer();
        BlockPos center = freakyberian.getPosition();
        int radius = 5;
        Block block = Blocks.PINK_GLAZED_TERRACOTTA;
        
        World world = event.getWorld();
        
        //turn blocks in the radius to air
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.add(x, y, z);
                    Block current = world.getBlockState(pos).getBlock();
                    
                    if (current != Blocks.AIR && current != Blocks.OBSIDIAN && current != Blocks.BEDROCK) {
                        world.setBlockToAir(pos);
                    }
                }
            }
        }
        
        //build box
        for (int x = -radius; x <= radius; x++)
        {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(y) == radius || Math.abs(z) == radius) {
                        BlockPos pos = center.add(x, y, z);
                        Block current = world.getBlockState(pos).getBlock();
                        
                        if (current == Blocks.AIR) {
                            world.setBlockState(pos, block.getDefaultState());
                        }
                    }
                }
            }
            world.setBlockState(center.up(), Blocks.GLOWSTONE.getDefaultState());
        }
        
        Potion lovePotion = ForgeRegistries.POTIONS.getValue(new ResourceLocation("switchbow", "love"));
        
        RLSPacketHandler.INSTANCE.sendToAll(new ParticlePulsePacket(freakyberian, EnumParticleTypes.HEART, 1200, 15));
        
        if (lovePotion != null) player.addPotionEffect(new PotionEffect(lovePotion, 1200, 0));
        else RLSPacketHandler.INSTANCE.sendToAll(new ParticlePulsePacket(player, EnumParticleTypes.HEART, 1200, 15));
        
        freakyberian.tasks.addTask(1, new RLSEntityAIFollow(freakyberian, player, 1.0D, 2.0F, 12.0F));
    }
    
    private static boolean rollChance(double chancePercent) {
        return RANDOM.nextDouble() * 100 < chancePercent;
    }
}
