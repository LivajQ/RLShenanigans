package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityMakaAlpha;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityMaka extends RideableCreatureEntity
{

    public EntityMaka(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.babySpawnChance = 0.1D;
        this.attackCooldownMax = 10;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();
        this.stepHeight = 2.0F;
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
        
        this.targetTasks.addTask(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(EntityMakaAlpha.class).setSightCheck(false));
    }
    
    @Override
    public void onFirstSpawn() {
        CreatureInfo alphaInfo = CreatureManager.getInstance().getCreature("makaalpha");
        if(alphaInfo != null) {
            float alphaChance = (float)alphaInfo.creatureSpawn.spawnWeight / Math.max(this.creatureInfo.creatureSpawn.spawnWeight, 1);
            if (this.getRNG().nextFloat() <= alphaChance) {
                EntityMakaAlpha alpha = (EntityMakaAlpha)CreatureManager.getInstance().getCreature("makaalpha").createEntity(this.getEntityWorld());
                alpha.copyLocationAndAnglesFrom(this);
                this.getEntityWorld().spawnEntity(alpha);
                this.setDead();
            }
        }
        super.onFirstSpawn();
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GRASS)
                return 10F;
            if(blockState.getMaterial() == Material.GROUND)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }
    
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return true;
    }
    
    @Override
    public boolean canAttackEntity(EntityLivingBase target) {
        if(target instanceof com.lycanitesmobs.core.entity.creature.EntityMaka || target instanceof EntityMakaAlpha)
            return false;
        return super.canAttackEntity(target);
    }
    
    @Override
    public void setGrowingAge(int age) {
        if(age == 0 && this.getAge() < 0) {
            if (this.getRNG().nextFloat() >= 0.9F) {
                EntityMakaAlpha alpha = (EntityMakaAlpha)CreatureManager.getInstance().getCreature("makaalpha").createEntity(this.getEntityWorld());
                alpha.copyLocationAndAnglesFrom(this);
                this.getEntityWorld().spawnEntity(alpha);
                this.setDead();
            }
        }
        super.setGrowingAge(age);
    }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 105, 2));
        rider.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 105, 4));
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
                this.applyStaminaCost();
                
                double range = 10.0D;
                List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(range, range, range), possibleTarget -> {
                    if(!possibleTarget.isEntityAlive()
                            || possibleTarget == EntityMaka.this
                            || EntityMaka.this.isRidingOrBeingRiddenBy(possibleTarget)
                            || EntityMaka.this.isOnSameTeam(possibleTarget)
                            || !EntityMaka.this.canAttackClass(possibleTarget.getClass())
                            || !EntityMaka.this.canAttackEntity(possibleTarget))
                        return false;
                    return true;
                });
                if(!possibleTargets.isEmpty()) {
                    for(EntityLivingBase possibleTarget : possibleTargets) {
                        double dx = possibleTarget.posX - this.posX;
                        double dz = possibleTarget.posZ - this.posZ;
                        double distance = Math.sqrt(dx * dx + dz * dz);
                        
                        if (distance > 0.01F) {
                            double strength = 5.0D;
                            double knockbackX = (dx / distance) * strength;
                            double knockbackZ = (dz / distance) * strength;
                            
                            possibleTarget.addVelocity(knockbackX, strength / 2, knockbackZ);
                            possibleTarget.velocityChanged = true;
                        }
                    }
                }
                this.playAttackSound();
            }
        }
    }
    
    @Override
    public float getStaminaCost() {
        return 20.0F;
    }
    
    @Override
    public ResourceLocation getEquipmentTexture(String equipmentPart) {
        Set<String> unsupportedParts = new HashSet<>(Arrays.asList(
                "saddle", "chest", "chestIron", "chestGold", "chestDiamond"));
        
        if (unsupportedParts.contains(equipmentPart)) {
            return new ResourceLocation("rlshenanigans", "textures/entity/transparent.png");
        }
        
        return super.getEquipmentTexture(equipmentPart);
    }
}
