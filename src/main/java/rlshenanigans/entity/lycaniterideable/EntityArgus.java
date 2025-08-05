package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.*;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityArgus extends RideableCreatureEntity implements IMob, IFusable
{
    
    private int teleportTime = 60;
    
    public EntityArgus(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();
        
        this.stepHeight = 1.0F;
        
        this.setAttackCooldownMax(40);
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true).setMaxChaseDistanceSq(5.0F));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(14.0F));
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        /*/ Random Target Teleporting:
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
			if(this.teleportTime-- <= 0) {
				this.teleportTime = 20 + this.getRNG().nextInt(20);
				BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().width - 1D, 0);
				if(this.canTeleportTo(teleportPosition)) {
					this.playJumpSound();
					this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
				}
			}
		}*/
        
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
    }
    
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("chaosorb", target, range, 0, new Vec3d(0, 0, 0), 0.6f, 1f, 1F);
        super.attackRanged(target, range);
    }
    
    @Override
    public boolean isFlying() { return true; }
    
    @Override
    public boolean isStrongSwimmer() { return true; }
    
    public boolean petControlsEnabled() { return true; }
    
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    protected IFusable fusionTarget;
    
    @Override
    public IFusable getFusionTarget() {
        return this.fusionTarget;
    }
    
    @Override
    public void setFusionTarget(IFusable fusionTarget) {
        this.fusionTarget = fusionTarget;
    }
    
    @Override
    public Class getFusionClass(IFusable fusable) {
        if(fusable instanceof EntityCinder) {
            return CreatureManager.getInstance().getEntityClass("grue");
        }
        if(fusable instanceof EntityJengu) {
            return CreatureManager.getInstance().getEntityClass("eechetik");
        }
        if(fusable instanceof EntityGeonach) {
            return CreatureManager.getInstance().getEntityClass("tremor");
        }
        if(fusable instanceof EntityDjinn) {
            return CreatureManager.getInstance().getEntityClass("wraith");
        }
        if(fusable instanceof EntityAegis) {
            return CreatureManager.getInstance().getEntityClass("spectre");
        }
        return null;
    }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
    
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
                this.applyStaminaCost();
                
                double distance = 10.0D;
                List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
                    if(!possibleTarget.isEntityAlive()
                            || possibleTarget == EntityArgus.this
                            || EntityArgus.this.isRidingOrBeingRiddenBy(possibleTarget)
                            || EntityArgus.this.isOnSameTeam(possibleTarget)
                            || !EntityArgus.this.canAttackClass(possibleTarget.getClass())
                            || !EntityArgus.this.canAttackEntity(possibleTarget))
                        return false;
                    return true;
                });
                if(!possibleTargets.isEmpty()) {
                    for(EntityLivingBase possibleTarget : possibleTargets) {
                        boolean doDamage = true;
                        if(this.getRider() instanceof EntityPlayer) {
                            if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
                                doDamage = false;
                            }
                        }
                        if(doDamage) {
                            if (ObjectManager.getEffect("instability") != null)
                                possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("instability"), this.getEffectDuration(5), 1));
                            else
                                possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10 * 20, 0));
                        }
                    }
                }
                this.playAttackSound();
            }
        }
    }
    
    @Override
    public float getStaminaCost() {
        return 25.0F;
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

