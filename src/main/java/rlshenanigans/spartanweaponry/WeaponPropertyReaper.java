package rlshenanigans.spartanweaponry;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyWithCallback;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.ai.*;

import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class WeaponPropertyReaper extends WeaponPropertyWithCallback {
    
    public WeaponPropertyReaper(String propType, String propModId) {
        super(propType, propModId);
    }
    
    public WeaponProperty.PropertyQuality getQuality() {
        return PropertyQuality.POSITIVE;
    }
    
    @Override
    protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
        tooltip.add(TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, new Object[]{this.magnitude * 100.0F}));
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) return;
        EntityLivingBase killer = (EntityLivingBase) event.getSource().getTrueSource();
        EntityLivingBase victim = event.getEntityLiving();
        World world = victim.getEntityWorld();
        
        Item mainHand = killer.getHeldItemMainhand().getItem();
        Item offHand = killer.getHeldItemOffhand().getItem();
        
        if (!isReaperProperty(mainHand) && !isReaperProperty(offHand)) return;
        
        EntitySkeleton summon = new EntitySkeleton(world);
        summon.setPosition(victim.posX, victim.posY, victim.posZ);
        
        DifficultyInstance difficulty = world.getDifficultyForLocation(new BlockPos(victim.posX, victim.posY, victim.posZ));
        summon.onInitialSpawn(difficulty, null);
        
        summon.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(victim.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() * 0.3F);
        summon.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(victim.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue() * 0.3F);
        summon.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(victim.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 0.3F);
        summon.setHealth(summon.getMaxHealth());
        summon.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        summon.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
        
        summon.getEntityData().setUniqueId("OwnerUUID", killer.getUniqueID());
        summon.getEntityData().setBoolean("MiscTamed", true);
        summon.getEntityData().setInteger("ReaperSummonLifetime", 600);
        summon.setCustomNameTag(killer.getName() + "'s Skeleton");
        summon.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        
        summon.targetTasks.taskEntries.removeIf(entry -> entry.action instanceof EntityAINearestAttackableTarget || entry.action instanceof EntityAIFleeSun);
        summon.tasks.addTask(6, new MiscEntityAIFollowOwner(summon, 1.2F, 10.0F, 2.0F));
        summon.targetTasks.addTask(1, new MiscEntityAIOwnerHurtByTarget(summon));
        summon.targetTasks.addTask(2, new MiscEntityAIOwnerHurtTarget(summon));
        
        world.spawnEntity(summon);
    }
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntitySkeleton)) return;
        EntitySkeleton skeleton = (EntitySkeleton) event.getEntity();
        if (skeleton.ticksExisted % 20 != 0) return;
        
        int lifetime = skeleton.getEntityData().getInteger("ReaperSummonLifetime");
        if (lifetime <= 0) return;
        
        lifetime -= 20;
        if (lifetime <= 0) skeleton.setDead();
        else skeleton.getEntityData().setInteger("ReaperSummonLifetime", lifetime);
    }
    
    private static boolean isReaperProperty(Item item) {
        if (!(item instanceof ItemSwordBase)) return false;
        ItemSwordBase spartanWeapon = (ItemSwordBase)  item;
        return spartanWeapon.hasWeaponProperty(RLSWeaponProperties.REAPER);
    }
}