package rlshenanigans.entity.creature;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal;
import com.lycanitesmobs.core.entity.projectile.EntityDevilGatling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAsmodeusTamed extends RideableCreatureEntity {
    
    public AttackRangedGoal aiRangedAttack;
    
    public EntityAsmodeusTamed(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setAttackCooldownMax(30);
        this.hasJumpSound = true;
        this.trueSight = true;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaWidthScale = 4F;
        this.stepHeight = 3.0F;
    }
    
    @Override
    protected void initEntityAI() {
        this.targetTasks.addTask(this.nextFindTargetIndex, new FindNearbyPlayersGoal(this));
        
        this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
        this.tasks.addTask(this.nextIdleGoalIndex, new HealWhenNoPlayersGoal(this));
        
        this.aiRangedAttack = new AttackRangedGoal(this).setSpeed(1.0D).setStaminaTime(200).setStaminaDrainRate(3).setRange(90.0F).setChaseTime(0).setCheckSight(false);
        this.tasks.addTask(this.nextCombatGoalIndex++, this.aiRangedAttack);
        
        super.initEntityAI();
    }
    
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
    }
    
    @Override
    public void attackRanged(Entity target, float range) {
        for (int i = 0; i < 5; i++) {
            this.fireProjectile(EntityDevilGatling.class, target, range, 0, new Vec3d(0, -2.4D, 0), 4f, 2f, 8F);
        }
        this.attackHitscan(target, target instanceof EntityPlayer ? 1 : 10);
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
    public void startJumping() {
        if(this.onGround) this.addVelocity(0, 2.0F, 0);
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            EntityPlayer player = (EntityPlayer) rider;
            
            double maxDistance = 50.0D;
            
            RayTraceResult hit = player.rayTrace(maxDistance, 1.0F);
            
            double targetX;
            double targetY;
            double targetZ;
            
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = hit.getBlockPos();
                targetX = pos.getX() + 0.5;
                targetY = pos.getY() + 1;
                targetZ = pos.getZ() + 0.5;
            } else {
                Vec3d lookVec = player.getLookVec();
                targetX = player.posX + lookVec.x * maxDistance;
                targetY = player.posY + lookVec.y * maxDistance;
                targetZ = player.posZ + lookVec.z * maxDistance;
            }
            
            this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            this.getEntityWorld().playEvent(2003, this.getPosition(), 0);
            
            this.setPositionAndUpdate(targetX, targetY, targetZ);
            this.motionY = 0.0;
            
            this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            this.getEntityWorld().playEvent(2003, this.getPosition(), 0);
            
            this.applyStaminaCost();
        }
    }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        if(!rider.onGround) rider.fallDistance = 0;
    }
    
    @Override
    public float getStaminaCost() {return 5.0F;}
    
    @Override
    public float getFallResistance() {
        return 100.0F;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {super.readFromNBT(nbtTagCompound);}

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {super.writeEntityToNBT(nbtTagCompound);}
    
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}