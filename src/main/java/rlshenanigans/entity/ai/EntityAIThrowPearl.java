package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityEnderPearl;

import java.util.Random;

public class EntityAIThrowPearl extends EntityAIBase
{
    private final EntityLiving entity;
    private final float offsetY;
    private final Random rand = new Random();
    private int cooldownTicks = 0;
    
    public EntityAIThrowPearl(EntityLiving entity) {
        this(entity, -0.2F);
    }
    
    public EntityAIThrowPearl(EntityLiving entity, float offsetY) {
        this.entity = entity;
        this.offsetY = offsetY;
    }
    
    @Override
    public boolean shouldExecute() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }
        
        EntityLivingBase target = entity.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;
        if (!entity.canEntityBeSeen(target)) return false;
        
        double dx = target.posX - entity.posX;
        double dy = target.posY - entity.posY;
        double dz = target.posZ - entity.posZ;
        double horizontalDistanceSq = dx * dx + dz * dz;
        
        if (dy >= 3.0 || horizontalDistanceSq >= 25.0) {
            return rand.nextInt(100) < 5;
        }
        
        return false;
    }
    
    @Override
    public void startExecuting() {
        EntityLivingBase target = entity.getAttackTarget();
        if (target == null) return;
        
        double dx = target.posX - entity.posX;
        double dy = target.posY + target.getEyeHeight() - entity.posY - offsetY;
        double dz = target.posZ - entity.posZ;
        
        EntityEnderPearl pearl = new EntityEnderPearl(entity.world, entity);
        pearl.setPosition(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        pearl.shoot(dx, dy, dz, 1.5F, 0.0F);
        entity.world.spawnEntity(pearl);
        
        cooldownTicks = 100;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }
}