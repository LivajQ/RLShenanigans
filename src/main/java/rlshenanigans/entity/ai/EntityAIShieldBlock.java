package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import java.util.Random;
import java.util.UUID;

public class EntityAIShieldBlock extends EntityAIBase {
    private static final UUID BLOCKING_SPEED_MODIFIER_UUID = UUID.fromString("e3a1f3e2-9c4b-4d3e-8f3a-1d3e9c4b4d3e");
    private static final AttributeModifier BLOCKING_SPEED_MODIFIER = new AttributeModifier(BLOCKING_SPEED_MODIFIER_UUID, "Blocking slowdown", -0.5D, 2);
    
    private final EntityLiving entity;
    private final boolean slowdown;
    private int blockTicks = 0;
    private int cooldownTicks = 0;
    private final Random rand;
    
    public EntityAIShieldBlock(EntityLiving entity) {
        this(entity, true);
    }
    
    public EntityAIShieldBlock(EntityLiving entity, boolean slowdown) {
        this.entity = entity;
        this.slowdown = slowdown;
        this.rand = entity.getRNG();
    }
    
    @Override
    public boolean shouldExecute() {
        ItemStack offhand = entity.getHeldItemOffhand();
        EntityLivingBase target = entity.getAttackTarget();
        
        if (!(offhand.getItem() instanceof ItemShield) || target == null || !target.isEntityAlive() || !entity.canEntityBeSeen(target)) {
            return false;
        }
        
        double distSq = entity.getDistanceSq(target);
        
        if (distSq > 25.0D) {
            return true;
        }
        
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }
        
        return rand.nextFloat() < 0.1F;
    }
    
    @Override
    public void startExecuting() {
        entity.setActiveHand(EnumHand.OFF_HAND);
        blockTicks = 40 + rand.nextInt(20);
        if (!slowdown) return;
        IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (!movement.hasModifier(BLOCKING_SPEED_MODIFIER)) {
            movement.applyModifier(BLOCKING_SPEED_MODIFIER);
        }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = entity.getAttackTarget();
        if (target == null || !target.isEntityAlive() || !entity.canEntityBeSeen(target)) {
            return false;
        }
        
        double distSq = entity.getDistanceSq(target);
        
        if (distSq > 25.0D) {
            return true;
        }
        
        return blockTicks > 0;
    }
    
    @Override
    public void updateTask() {
        if (blockTicks > 0) {
            blockTicks--;
        }
    }
    
    @Override
    public void resetTask() {
        entity.resetActiveHand();
        IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (movement.hasModifier(BLOCKING_SPEED_MODIFIER)) {
            movement.removeModifier(BLOCKING_SPEED_MODIFIER);
        }
        
        if (blockTicks <= 0) {
            cooldownTicks = 60 + rand.nextInt(40);
        }
        
        blockTicks = 0;
    }
}