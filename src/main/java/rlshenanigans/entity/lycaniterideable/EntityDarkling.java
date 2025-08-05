package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntityDarkling extends RideableCreatureEntity implements IMob
{
    protected static final DataParameter<Integer> LATCH_TARGET = EntityDataManager.createKey(com.lycanitesmobs.core.entity.creature.EntityDarkling.class, DataSerializers.VARINT);
    protected static final DataParameter<Float> LATCH_HEIGHT = EntityDataManager.createKey(com.lycanitesmobs.core.entity.creature.EntityDarkling.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> LATCH_ANGLE = EntityDataManager.createKey(com.lycanitesmobs.core.entity.creature.EntityDarkling.class, DataSerializers.FLOAT);

    EntityLivingBase latchEntity = null;
    int latchEntityID = 0;
    double latchHeight = 0.5D;
    double latchAngle = 90D;
    
    public EntityDarkling(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.setupMob();
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(LATCH_TARGET, 0);
        this.dataManager.register(LATCH_HEIGHT, (float)this.latchHeight);
        this.dataManager.register(LATCH_ANGLE, (float)this.latchAngle);
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && !this.hasLatchTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0)
            this.leap(6.0F, 0.6D, this.getAttackTarget());
        
        if(this.hasLatchTarget()) {
            this.noClip = true;
            
            Vec3d latchPos = this.getFacingPositionDouble(this.getLatchTarget().posX, this.getLatchTarget().posY + (this.getLatchTarget().height * this.latchHeight), this.getLatchTarget().posZ, this.getLatchTarget().width * 0.5D, this.latchAngle);
            this.setPosition(latchPos.x, latchPos.y, latchPos.z);
            double distanceX = this.getLatchTarget().posX - this.posX;
            double distanceZ = this.getLatchTarget().posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float) MathHelper.atan2(distanceX, distanceZ)) * (180F / (float)Math.PI);
            
            if(!this.getEntityWorld().isRemote) {
                if(this.getLatchTarget().isEntityAlive() && !this.isInWater()) {
                    this.setAttackTarget(this.getLatchTarget());
                    if (this.updateTick % 40 == 0) {
                        float damage = this.getAttackDamage(1);
                        if (this.attackMelee(this.getLatchTarget(), 1.0))
                            this.heal(damage * 2);
                    }
                }
                else {
                    this.setPosition(this.getLatchTarget().posX, this.getLatchTarget().posY, this.getLatchTarget().posZ);
                    this.setLatchTarget(null);
                    this.noClip = false;
                }
            }

            else {
                for(int i = 0; i < 2; ++i) {
                    this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
            this.noClip = false;
    }
    
    public EntityLivingBase getLatchTarget() {
        try {
            if (this.getEntityWorld().isRemote) {
                this.latchHeight = this.dataManager.get(LATCH_HEIGHT);
                this.latchAngle = this.dataManager.get(LATCH_ANGLE);
                int latchEntityID = this.getDataManager().get(LATCH_TARGET);
                if (latchEntityID != this.latchEntityID) {
                    this.latchEntity = null;
                    this.latchEntityID = latchEntityID;
                    if (latchEntityID != 0) {
                        Entity possilbeLatchEntity = this.getEntityWorld().getEntityByID(latchEntityID);
                        if (possilbeLatchEntity != null && possilbeLatchEntity instanceof EntityLivingBase)
                            this.latchEntity = (EntityLivingBase) possilbeLatchEntity;
                    }
                }
            }
        }
        catch (Exception e) {}
        return this.latchEntity;
    }
    
    public void setLatchTarget(EntityLivingBase entity) {
        this.latchEntity = entity;
        if(this.getEntityWorld().isRemote)
            return;
        if(entity == null) {
            this.getDataManager().set(LATCH_TARGET, 0);
            return;
        }
        if(ObjectManager.getEffect("repulsion") != null) {
            if ((entity).isPotionActive(ObjectManager.getEffect("repulsion"))) {
                this.latchEntity = null;
                this.getDataManager().set(LATCH_TARGET, 0);
                return;
            }
        }
        this.getDataManager().set(LATCH_TARGET, entity.getEntityId());
        this.latchHeight = 0.25D + (0.75D * this.getRNG().nextDouble());
        this.latchAngle = 360 * this.getRNG().nextDouble();
        this.dataManager.set(LATCH_HEIGHT, (float) this.latchHeight);
        this.dataManager.set(LATCH_ANGLE, (float) this.latchAngle);
    }
    
    public boolean hasLatchTarget() {
        return this.getLatchTarget() != null;
    }
    
    @Override
    public boolean canAttackClass(Class targetType) {
        if(this.hasLatchTarget())
            return false;
        return super.canAttackClass(targetType);
    }
    
    @Override
    public boolean attackMelee(Entity target, double damageScale) {

        double targetKnockbackResistance = 0;
        if(target instanceof EntityLivingBase) {
            targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
            ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        }
        
        if(!super.attackMelee(target, damageScale))
            return false;
        
        if(target instanceof EntityLivingBase)
            ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
        
        if(!this.hasLatchTarget() && target instanceof EntityLivingBase && !this.isInWater()) {
            this.setLatchTarget((EntityLivingBase) target);
        }
        
        return true;
    }
    
    @Override
    public boolean canEntityBeSeen(Entity target) {
        if (target == this.getLatchTarget()) {
            return true;
        }
        return super.canEntityBeSeen(target);
    }
    
    @Override
    public boolean canClimb() { return true; }
    
    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    @Override
    public boolean canStealth() {
        if(this.getEntityWorld().isRemote) return false;
        if(this.isMoving()) return false;
        return this.testLightLevel() <= 0;
    }
    
    @Override
    public void startStealth() {
        if(this.getEntityWorld().isRemote) {
            EnumParticleTypes particle = EnumParticleTypes.SPELL_WITCH;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
                this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
        super.startStealth();
    }
    
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("inWall"))
            return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public float getFallResistance() {
        return 10;
    }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(ObjectManager.getEffect("leech"), 105, 4));
    }
    
    @Override
    public float getStaminaCost() {
        return 10.0F;
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
