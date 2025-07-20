package rlshenanigans.entity.creature;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rlshenanigans.handlers.RLSSoundHandler;

public class EntityDrJr extends EntityMob {
    public EntityDrJr(World world) {
        super(world);
        this.setSize(0.5F, 1.0F);
        this.experienceValue = 5;
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }
    
    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
    }
    
    @Override
    public boolean getCanSpawnHere() {
        BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
        boolean isDark = this.world.getLight(pos) < 8;
        boolean isUnderground = pos.getY() < 40;
        
        return isDark && isUnderground && super.getCanSpawnHere();
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return RLSSoundHandler.DRJR_AMBIENT;
    }
}