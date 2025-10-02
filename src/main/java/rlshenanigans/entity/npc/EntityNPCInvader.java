package rlshenanigans.entity.npc;

import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import rlshenanigans.entity.ai.EntityAIMineToTarget;
import rlshenanigans.entity.ai.EntityAINPCInvaderHunt;
import rlshenanigans.entity.ai.EntityAIThrowPearl;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.util.NPCPresets;

import java.util.UUID;

public class EntityNPCInvader extends EntityNPCPhantom {
    protected UUID invadedPlayerUUID;
    
    public EntityNPCInvader(World world) {
        this(world, null, 100);
    }
    
    public EntityNPCInvader(World world, UUID invadedPlayerUUID, int phantomFadeTime) {
        super(world, phantomFadeTime);
        this.invadedPlayerUUID = invadedPlayerUUID;
    }
    
    @Override
    protected void applyEntityAI() {
        super.applyEntityAI();
        this.tasks.addTask(1, new EntityAIThrowPearl(this));
        this.tasks.addTask(2, new EntityAIMineToTarget(this, 2137, 3, 40));
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if (invadedPlayerUUID != null) {
            EntityPlayer player = this.world.getPlayerEntityByUUID(invadedPlayerUUID);
            if (this.ticksExisted % 20 == 0 && player != null && !player.isDead && !hasHuntTask())
                this.tasks.addTask(0, new EntityAINPCInvaderHunt(this, player));
            
            if (player != null && player.isDead && this.phantomFadeTime <= 0) {
                this.phantomFadeTime = phantomFadeTimeMax;
                this.isDespawning = true;
            }
            
            if (player == null) this.setDead();
            else if (this.ticksExisted % 20 == 0) {
                double distanceSq = this.getDistanceSq(player);
                if (distanceSq > 128 * 128) this.setDead();
            }
            
            if (phantomFadeTime == 1 && player != null && !isDespawning && firstSpawn) {
                String message = "§c§l" + this.name + " is invading you";
                player.sendStatusMessage(new TextComponentString(message), true);
            }
        }
    }
    
    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        this.setDead();
    }
    
    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        super.dropEquipment(wasRecentlyHit, lootingModifier);
        ItemStack mainHand = getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        ItemStack offHand = getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        if (!mainHand.isEmpty()) this.entityDropItem(mainHand.copy(), 0.5F);
        if (!offHand.isEmpty()) this.entityDropItem(offHand.copy(), 0.5F);
    }
    
    @Override
    protected void createCharacter() {
        if (this.world.isRemote) return;
        NPCPresets.generatePreset(this, NPCPresets.Categories.INVADER);
    }
    
    @Override
    protected double getCharacterStatMultiplier() {
        return ForgeConfigHandler.npc.invaderStatisticsMultiplier;
    }
    
    @Override
    protected double statisticRandomFactor() {
        double randomFactor = ForgeConfigHandler.npc.invaderStatisticsRandomFactor;
        double min = 1.0 / randomFactor;
        return min + (rand.nextDouble() * (randomFactor - min));
    }
    
    @Override
    protected int getExtraEnchantmentCount() {
        int min = ForgeConfigHandler.npc.invaderExtraEnchantmentMin;
        int max = ForgeConfigHandler.npc.invaderExtraEnchantmentMax;
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return min + rand.nextInt(max - min + 1);
    }
    
    @Override
    protected double getEnchantabilityMultiplier() {
        return ForgeConfigHandler.npc.invaderEnchantabilityMultiplier;
    }
    
    @Override
    public Vec3d getPhantomGlowColor() {
        return new Vec3d(1.0, 0.1, 0.1);
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (invadedPlayerUUID != null) compound.setUniqueId("InvadedPlayerUUID", invadedPlayerUUID);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("InvadedPlayerUUID")) this.invadedPlayerUUID = compound.getUniqueId("InvadedPlayerUUID");
    }
    
    private boolean hasHuntTask() {
        for (EntityAITasks.EntityAITaskEntry entry : this.tasks.taskEntries) {
            if (entry.action instanceof EntityAINPCInvaderHunt) return true;
        }
        return false;
    }
    
    @Override
    protected int potionCooldown() {
        return 160;
    }
    
    @Override
    protected float potionThrowerChance() {
        return Math.min(1.0F, 0.2F + this.characterStrength * 0.0035F);
    }
    
    public UUID getInvadedPlayerUUID() {
        return invadedPlayerUUID;
    }
}