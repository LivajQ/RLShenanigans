package rlshenanigans.entity.npc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.ai.*;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.util.NPCPresets;

import javax.vecmath.Color3f;
import java.util.UUID;

public class EntityNPCSummon extends EntityNPCPhantom {
    protected UUID hostPlayerUUID;
    
    public EntityNPCSummon(World world) {
        this(world, null, 100);
    }
    
    public EntityNPCSummon(World world, UUID hostPlayerUUID, int phantomFadeTime) {
        super(world, phantomFadeTime);
        this.hostPlayerUUID = hostPlayerUUID;
    }
    
    @Override
    protected void applyEntityAI() {
        super.applyEntityAI();
        this.targetTasks.addTask(3, new EntityAISelfDefense<>(this, EntityLivingBase.class));
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if (hostPlayerUUID != null) {
            EntityPlayer player = this.world.getPlayerEntityByUUID(hostPlayerUUID);
            if (this.ticksExisted % 20 == 0 && player != null && !player.isDead && !hasCoopTask())
                this.tasks.addTask(3, new EntityAINPCSummonCoop(this, player, 1.0F));
            
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
                String message = "§6§l" + this.name + " has been summoned";
                player.sendStatusMessage(new TextComponentString(message), true);
            }
        }
    }
    
    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.hostPlayerUUID != null) {
            EntityPlayer player = this.world.getPlayerEntityByUUID(this.hostPlayerUUID);
            if (player != null) {
                DamageSource source = DamageSource.causeIndirectDamage(this, player);
                entityIn.attackEntityFrom(source, 0.0F);
            }
        }
        return super.attackEntityAsMob(entityIn);
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.hostPlayerUUID != null) {
            Entity attacker = source.getTrueSource();
            EntityPlayer player = this.world.getPlayerEntityByUUID(this.hostPlayerUUID);
            if (player != null && attacker == player) return false;
            
            if (attacker instanceof EntityLiving && player != null) {
                if (attacker.getEntityData().hasUniqueId("OwnerUUID")
                        && attacker.getEntityData().getUniqueId("OwnerUUID").equals(player.getUniqueID())) return false;
            }
        }
        return super.attackEntityFrom(source, amount);
    }
    
    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (this.hostPlayerUUID != null) {
            EntityPlayer player = this.world.getPlayerEntityByUUID(this.hostPlayerUUID);
            if (player != null) {
                String message = "§6§l" + this.name + " has been defeated";
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
    protected void createCharacter() {
        if (this.world.isRemote) return;
        NPCPresets.generatePreset(this, NPCPresets.Categories.SUMMON);
    }
    
    @Override
    protected double getCharacterStatMultiplier() {
        return ForgeConfigHandler.npc.summonStatisticsMultiplier;
    }
    
    @Override
    protected double statisticRandomFactor() {
        double randomFactor = ForgeConfigHandler.npc.summonStatisticsRandomFactor;
        double min = 1.0 / randomFactor;
        return min + (rand.nextDouble() * (randomFactor - min));
    }
    
    @Override
    protected int getExtraEnchantmentCount() {
        int min = ForgeConfigHandler.npc.summonExtraEnchantmentMin;
        int max = ForgeConfigHandler.npc.summonExtraEnchantmentMax;
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return min + rand.nextInt(max - min + 1);
    }
    
    @Override
    protected double getEnchantabilityMultiplier() {
        return ForgeConfigHandler.npc.summonEnchantabilityMultiplier;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Color3f getPhantomGlowColor() {
        return new Color3f(1.0F, 0.85F, 0.3F);
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (hostPlayerUUID != null) compound.setUniqueId("HostPlayerUUID", hostPlayerUUID);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("HostPlayerUUID")) this.hostPlayerUUID = compound.getUniqueId("HostPlayerUUID");
    }
    
    @Override
    protected void potionTargeter() {
        boolean coinFlip = this.rand.nextBoolean();
        EntityLivingBase target = this.getAttackTarget();
        
        if (target != null) {
            if (coinFlip && this.canEntityBeSeen(target)) throwPotion(target, false);
            
            else if (!coinFlip && this.hostPlayerUUID != null) {
                EntityPlayer host = world.getPlayerEntityByUUID(this.hostPlayerUUID);
                if (host != null) throwPotion(host, true);
            }
        }
    }
    
    private boolean hasCoopTask() {
        for (EntityAITasks.EntityAITaskEntry entry : this.tasks.taskEntries) {
            if (entry.action instanceof EntityAINPCSummonCoop) return true;
        }
        return false;
    }
}