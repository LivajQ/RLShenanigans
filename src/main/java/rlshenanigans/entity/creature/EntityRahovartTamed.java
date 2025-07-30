package rlshenanigans.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.*;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireWave;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

public class EntityRahovartTamed extends RideableCreatureEntity {
    public int hellfireEnergy = 0;
    protected static final DataParameter<Integer> HELLFIRE_ENERGY = EntityDataManager.createKey(EntityRahovart.class, DataSerializers.VARINT);
    public int hellfireWallTime = 0;
    public int hellfireWallTimeMax = 200;
    public int hellfireWallCooldown = 600;
    public boolean hellfireWallClockwise = false;
    public EntityHellfireBarrier hellfireWallLeft;
    public EntityHellfireBarrier hellfireWallRight;
    
    public EntityRahovartTamed(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.setAttackCooldownMax(40);
        this.trueSight = true;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaWidthScale = 2F;
        this.stepHeight = 3.0F;
    }

    @Override
    protected void initEntityAI() {
        this.targetTasks.addTask(this.nextFindTargetIndex, new FindNearbyPlayersGoal(this));
        
        this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
        this.tasks.addTask(this.nextIdleGoalIndex, new HealWhenNoPlayersGoal(this));
        this.tasks.addTask(this.nextIdleGoalIndex, new FireProjectilesGoal(this).setProjectile("hellfireball").setFireRate(10).setVelocity(3.0F).setScale(2F));
        
        super.initEntityAI();
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(EntityRahovartTamed.HELLFIRE_ENERGY, this.hellfireEnergy);
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if (!this.getEntityWorld().isRemote) {
            
            if (this.hellfireWallTime > 0) {
                this.hellfireWallUpdate();
                --this.hellfireWallTime;
                
                if (this.hellfireWallTime <= 0) {
                    this.hellfireWallCooldown = 600;
                }
            }
            
            else if (this.hellfireWallCooldown <= 0 && this.hasAttackTarget()) {
                if (this.rand.nextFloat() < 0.001F) {
                    this.hellfireWallAttack(this.rotationYaw);
                    this.hellfireWallTime = this.hellfireWallTimeMax;
                }
            }
            
            else if (this.hellfireWallCooldown > 0) {
                --this.hellfireWallCooldown;
            }
        }
    }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("hellfireball", target, range, 0, new Vec3d(0, 0, 0), 1.2f, 8f, 1F);
        super.attackRanged(target, range);
    }
    
    public void hellfireWallAttack(double angle) {
        
        this.playAttackSound();
        this.triggerAttackCooldown();
        
        this.hellfireWallTime = hellfireWallTimeMax;
        this.hellfireWallClockwise = this.getRNG().nextBoolean();
    }
    
    public void hellfireWallUpdate() {
        this.triggerAttackCooldown();
        
        double hellfireWallNormal = (double)this.hellfireWallTime / hellfireWallTimeMax;
        double hellfireWallAngle = 360;
        if(this.hellfireWallClockwise)
            hellfireWallAngle = -360;
        
        // Left (Positive) Wall:
        if(this.hellfireWallLeft == null) {
            this.hellfireWallLeft = new EntityHellfireBarrier(this.getEntityWorld(), this);
            this.hellfireWallLeft.wall = true;
            this.getEntityWorld().spawnEntity(this.hellfireWallLeft);
        }
        this.hellfireWallLeft.time = 0;
        this.hellfireWallLeft.posX = this.posX;
        this.hellfireWallLeft.posY = this.posY;
        this.hellfireWallLeft.posZ = this.posZ;
        this.hellfireWallLeft.rotation = hellfireWallNormal * hellfireWallAngle;
        
        // Right (Negative) Wall:
        if(this.hellfireWallRight == null) {
            this.hellfireWallRight = new EntityHellfireBarrier(this.getEntityWorld(), this);
            this.hellfireWallRight.wall = true;
            this.getEntityWorld().spawnEntity(this.hellfireWallRight);
        }
        this.hellfireWallRight.time = 0;
        this.hellfireWallRight.posX = this.posX;
        this.hellfireWallRight.posY = this.posY;
        this.hellfireWallRight.posZ = this.posZ;
        this.hellfireWallRight.rotation = 180 + (hellfireWallNormal * hellfireWallAngle);
        
        try {
            Field sizeField = EntityHellfireBarrier.class.getDeclaredField("hellfireSize");
            sizeField.setAccessible(true);
            sizeField.setInt(this.hellfireWallLeft, 2);
            sizeField.setInt(this.hellfireWallRight, 2);
            
            /*
            Field widthField = EntityHellfireBarrier.class.getDeclaredField("hellfireWidth");
            widthField.setAccessible(true);
            widthField.setInt(this.hellfireWallLeft, 1);
            widthField.setInt(this.hellfireWallRight, 1);
            
            Field heightField = EntityHellfireBarrier.class.getDeclaredField("hellfireHeight");
            heightField.setAccessible(true);
            heightField.setInt(this.hellfireWallLeft, 1);
            heightField.setInt(this.hellfireWallRight, 1);
             */
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void hellfireWallCleanup() {
        if(this.hellfireWallLeft != null) {
            this.hellfireWallLeft.setDead();
            this.hellfireWallLeft = null;
        }
        if(this.hellfireWallRight != null) {
            this.hellfireWallRight.setDead();
            this.hellfireWallRight = null;
        }
    }
    
    public void hellfireWaveAttack(double angle) {
        this.triggerAttackCooldown();
        this.playAttackSound();
        EntityHellfireWave hellfireWave = new EntityHellfireWave(this.getEntityWorld(), this);
        hellfireWave.posY = this.posY;
        hellfireWave.rotation = angle;
        this.getEntityWorld().spawnEntity(hellfireWave);
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WITHER)
            return false;
        if(ObjectManager.getEffect("decay") != null)
            if(potionEffect.getPotion() == ObjectManager.getEffect("decay")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean isDamageEntityApplicable(Entity entity) {
        if(entity instanceof EntityPigZombie) {
            entity.setDead();
            return false;
        }
        if(entity instanceof EntityIronGolem) {
            entity.setDead();
            return false;
        }
        return super.isDamageEntityApplicable(entity);
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damageAmount) {
        if(this.playerTargets != null && damageSrc.getTrueSource() != null && damageSrc.getTrueSource() instanceof EntityPlayer) {
            if (!this.playerTargets.contains(damageSrc.getTrueSource()))
                this.playerTargets.add((EntityPlayer)damageSrc.getTrueSource());
        }
        return super.attackEntityFrom(damageSrc, damageAmount);
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (!(this.getStamina() < this.getStaminaCost())) {
                this.playAttackSound();
                double angle = this.rotationYaw + 90;
                if (rider instanceof EntityLivingBase) angle = rider.rotationYaw + 90;
                this.hellfireWaveAttack(angle);
                this.applyStaminaCost();
            }
        }
    }
    
    @Override
    public float getStaminaCost() {return 50.0F;}
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        if(nbtTagCompound.hasKey("HellfireWallTime")) {
            this.hellfireWallTime = nbtTagCompound.getInteger("HellfireWallTime");
        }
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("HellfireWallTime", this.hellfireWallTime);
    }
    
    @Override
    protected void playStepSound(BlockPos pos, Block block) {super.playStepSound(pos, block);}
    
    @Override
    public float getBrightness() {return 1.0F;}
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {return 15728880;}
}
