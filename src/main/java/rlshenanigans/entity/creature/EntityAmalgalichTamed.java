package rlshenanigans.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.*;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityAmalgalichTamed extends RideableCreatureEntity {
    private ForceGoal consumptionAttack;
    private EffectAuraGoal auraGoal;
    private int consumptionDuration = 300;
    private int consumptionWindUp = 60;
    private int consumptionAnimationTime = 0;
    private boolean isConsuming = false;
    private int consumptionCooldown = Integer.MAX_VALUE;
    private int lastConsumption = 0;
    
    public EntityAmalgalichTamed(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.setAttackCooldownMax(30);
        this.hasJumpSound = false;
        this.trueSight = true;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaWidthScale = 4F;
        this.stepHeight = 3.0F;
    }
    
    @Override
    protected void initEntityAI() {
        this.targetTasks.addTask(this.nextFindTargetIndex, new FindNearbyPlayersGoal(this));
        
        this.consumptionDuration = 300;
        this.consumptionWindUp = 60;
        this.consumptionAnimationTime = 0;
        this.isConsuming = false;
        this.consumptionCooldown = Integer.MAX_VALUE;
        
        this.consumptionAttack = (new ForceGoal(this)).setRange(64.0F).setCooldown(consumptionCooldown).setDuration(this.consumptionDuration).setWindUp(this.consumptionWindUp).setForce(-0.75F).setPhase(0).setDismount(true);
        this.tasks.addTask(this.nextIdleGoalIndex, this.consumptionAttack);
        
        this.auraGoal = (new EffectAuraGoal(this)).setRange(1.0F).setCooldown(consumptionCooldown).setDuration(this.consumptionDuration).setTickRate(5).setDamageAmount(1000.0F).setCheckSight(false).setTargetTypes((byte)(TARGET_TYPES.ALLY.id | TARGET_TYPES.ENEMY.id));
        this.tasks.addTask(this.nextIdleGoalIndex, this.auraGoal);
        
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
        this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
        this.tasks.addTask(this.nextIdleGoalIndex, new HealWhenNoPlayersGoal(this));
        
        
        super.initEntityAI();
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        
        //me when I break the animation
        if (!this.getEntityWorld().isRemote)
        {
            if (this.hasAttackTarget() && this.getRNG().nextFloat() < 0.01F && this.ticksExisted - this.lastConsumption > 600)
            {
               this.lastConsumption = this.ticksExisted;
                this.consumptionAttack.cooldownTime = 0;
                this.auraGoal.cooldownTime = 0;
                this.consumptionAnimationTime = 0;
                this.isConsuming = true;
            }
        }
        
        if (this.getEntityWorld().isRemote) {
            if (this.isConsuming) {
                this.consumptionAnimationTime++;
                if (this.consumptionAnimationTime >= this.consumptionDuration)
                    this.isConsuming = false;
            } else {
                this.consumptionAnimationTime = this.consumptionDuration;
            }
        }
    }
    
    @Override
    public <T extends Entity> List<T> getNearbyEntities(Class<? extends T> entityClass, Predicate<Entity> selector, double range) {
        Predicate<Entity> ownerFilter = entity -> {
            if (entity == this.getOwner()) return false;
            return selector == null || selector.test(entity);
        };
        return super.getNearbyEntities(entityClass, ownerFilter, range);
    }
    
    public float getConsumptionAnimation()
    {
        if (this.consumptionAnimationTime >= this.consumptionDuration)
        {
            return 0F;
        }
        int windUpThreshhold = this.consumptionDuration - this.consumptionWindUp;
        if (this.consumptionAnimationTime > windUpThreshhold)
        {
            return 1F - (float) (this.consumptionAnimationTime - windUpThreshhold) / this.consumptionWindUp;
        }
        float finishingTime = (float) this.consumptionWindUp / 2;
        if (this.consumptionAnimationTime < finishingTime)
        {
            return (float) this.consumptionAnimationTime / finishingTime;
        }
        return 1F;
    }
    
    @Override
    public boolean extraAnimation01() {
        return this.isConsuming;
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
                this.applyStaminaCost();
                
                String projectileName = "lobdarklings";
                float velocity = 0.8F;
                float scale = 2.0F;
                float inaccuracy = 0.0F;
                float angle = 360.0F;
                double spawnHeightOffset = 3.0;
                int randomCount = 3;
                
                for (int i = 0; i < randomCount; i++) {
                    ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(projectileName);
                    if (projectileInfo != null) {
                        BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
                        
                        if (projectile != null) {
                            projectile.setProjectileScale(scale);
                            
                            float pitch = this.getRNG().nextFloat() * 10.0F;
                            float yaw = this.getRNG().nextFloat() * angle;
                            
                            projectile.setPosition(this.posX, this.posY + spawnHeightOffset, this.posZ);
                            projectile.shoot(this, pitch, yaw, 0.0F, velocity, inaccuracy);
                            
                            
                            this.getEntityWorld().spawnEntity(projectile);
                        }
                    }
                }
                
            }
        }
    }
    
    @Override
    public float getStaminaCost() {return 10.0F;}
    
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if (this.isBlocking())
            return true;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBurn() {return false;}
    
    @Override
    public boolean isBlocking() {return super.isBlocking();}
    
    @Override
    public boolean canAttackWhileBlocking() {return true;}
    
    @Override
    public boolean isDamageEntityApplicable(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            entity.setDead();
            return false;
        }
        if (entity instanceof EntityIronGolem) {
            entity.setDead();
            return false;
        }
        return super.isDamageEntityApplicable(entity);
    }
    
    @Override
    public boolean canAttackEntity(EntityLivingBase target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer owner = this.getPlayerOwner();
            if (owner != null && target == owner) {
                return false;
            }
        }
        return super.canAttackEntity(target);
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damageAmount) {
        if (this.playerTargets != null && damageSrc.getTrueSource() != null && damageSrc.getTrueSource() instanceof EntityPlayer) {
            if (!this.playerTargets.contains(damageSrc.getTrueSource()))
                this.playerTargets.add((EntityPlayer) damageSrc.getTrueSource());
        }
        return super.attackEntityFrom(damageSrc, damageAmount);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {super.readFromNBT(nbtTagCompound);}
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {super.writeEntityToNBT(nbtTagCompound);}
    
    public float getBrightness() {return 1.0F;}
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {return 15728880;}
}
