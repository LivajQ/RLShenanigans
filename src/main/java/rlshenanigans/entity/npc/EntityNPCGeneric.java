package rlshenanigans.entity.npc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import rlshenanigans.entity.ai.EntityAIHuntUntamed;
import rlshenanigans.entity.ai.EntityAIMineToTarget;
import rlshenanigans.entity.ai.EntityAISelfDefense;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.util.NPCPresets;

import java.util.Arrays;
import java.util.List;

public class EntityNPCGeneric extends EntityNPCBase {
    public boolean inPlayerParty = false;
    
    public EntityNPCGeneric(World world) {
        super(world);
    }
    
    @Override
    protected void applyEntityAI() {
        super.applyEntityAI();
        this.tasks.addTask(1, new EntityAIMineToTarget(this, 5, 3, 100));
        
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityNPCGeneric.class));
        this.targetTasks.addTask(2, new EntityAISelfDefense<>(this, EntityLivingBase.class));
        this.targetTasks.addTask(3, new EntityAIHuntUntamed<>(this, EntityLivingBase.class, 10, true, false, true, target ->
                target instanceof IMob
        ));
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!world.isRemote) {
            if (rand.nextDouble() * 100 < ForgeConfigHandler.npc.johnMinecraftChance) {
                EntityNPCJohnMinecraft johnMinecraft = new EntityNPCJohnMinecraft(world);
                johnMinecraft.setPosition(this.posX, this.posY, this.posZ);
                world.spawnEntity(johnMinecraft);
                johnMinecraft.onInitialSpawn(difficulty, null);
                this.setDead();
            }
        }
        return livingdata;
    }
    
    @Override
    protected void createCharacter() {
        if (this.world.isRemote) return;
        if (rand.nextFloat() < 0.2F) NPCPresets.generatePreset(this, NPCPresets.Categories.GENERIC);
        else NPCPresets.generatePreset(this, NPCPresets.Categories.RANDOM);
    }
    
    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        super.dropEquipment(wasRecentlyHit, lootingModifier);
        ItemStack mainHand = getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        ItemStack offHand = getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        
        if (!mainHand.isEmpty() && this.rand.nextFloat() < 0.1F) this.entityDropItem(mainHand.copy(), 0.5F);
        if (!offHand.isEmpty() && this.rand.nextFloat() < 0.1F) this.entityDropItem(offHand.copy(), 0.5F);
    }
    
    @Override
    protected double getCharacterStatMultiplier() {
        return ForgeConfigHandler.npc.genericStatisticsMultiplier;
    }
    
    @Override
    protected double statisticRandomFactor() {
        double randomFactor = ForgeConfigHandler.npc.genericStatisticsRandomFactor;
        double min = 1.0 / randomFactor;
        return min + (rand.nextDouble() * (randomFactor - min));
    }
    
    @Override
    protected int getExtraEnchantmentCount() {
        int min = ForgeConfigHandler.npc.genericExtraEnchantmentMin;
        int max = ForgeConfigHandler.npc.genericExtraEnchantmentMax;
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return min + rand.nextInt(max - min + 1);
    }
    
    @Override
    protected double getEnchantabilityMultiplier() {
        return ForgeConfigHandler.npc.genericEnchantabilityMultiplier;
    }
    
    @Override
    protected boolean isWillingToTalk() {
        return true;
    }
    
    @Override
    protected void potionTargeter() {
        List<EntityNPCGeneric> nearbyAllies = this.world.getEntitiesWithinAABB(
                EntityNPCGeneric.class,
                this.getEntityBoundingBox().grow(12.0D, 6.0D, 12.0D),
                ally -> ally != this && ally.isEntityAlive() && !ally.inPlayerParty && this.canEntityBeSeen(ally)
        );
        
        boolean coinFlip = this.rand.nextBoolean();
        EntityLivingBase target = this.getAttackTarget();
        
        if (target != null) {
            if (coinFlip && this.canEntityBeSeen(target)) throwPotion(target, false);
            
            else if (!coinFlip && !nearbyAllies.isEmpty()) {
                EntityNPCGeneric ally = nearbyAllies.get(this.rand.nextInt(nearbyAllies.size()));
                throwPotion(ally, true);
            }
        }
        
        else {
            if (this.getHealth() / this.getMaxHealth() < 0.75F) throwPotion(this, true, PotionTypes.REGENERATION);
            
            else {
                for (EntityNPCGeneric ally : nearbyAllies) {
                    if (ally.getHealth() / ally.getMaxHealth() < 0.75F) {
                        throwPotion(ally, true, PotionTypes.REGENERATION);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("InPlayerParty", this.inPlayerParty);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.inPlayerParty = compound.getBoolean("InPlayerParty");
    }
}