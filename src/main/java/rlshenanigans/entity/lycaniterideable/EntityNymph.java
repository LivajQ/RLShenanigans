package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityNymph extends RideableCreatureEntity
{
    
    public int healingRate = 20;
    
    public EntityNymph(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();
        
        this.stepHeight = 1.0F;
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(14.0F));
    }
    
    @Override
    public void loadCreatureFlags() {
        this.healingRate = this.creatureInfo.getFlag("healingRate", this.healingRate);
    }

    private int farmingTick = 0;
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote) {
            if(this.healingRate > 0 && !this.isPetType("familiar")) {
                if (this.updateTick % this.healingRate == 0) {
                    List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
                    for (Object entityObj : aoeTargets) {
                        EntityLivingBase target = (EntityLivingBase) entityObj;
                        if (target != this && !(target instanceof com.lycanitesmobs.core.entity.creature.EntityNymph) && target != this.getAttackTarget() && target != this.getAvoidTarget()) {
                            target.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3 * 20, 0));
                        }
                    }
                }
            }
            
            if(this.hasAvoidTarget()) {
                if(this.updateTick % this.getRangedCooldown() == 0) {
                    this.attackRanged(this.getAvoidTarget(), this.getDistance(this.getAvoidTarget()));
                }
            }
            else if(this.hasAttackTarget()) {
                if(this.updateTick % this.getRangedCooldown() == 0) {
                    this.attackRanged(this.getAttackTarget(), this.getDistance(this.getAttackTarget()));
                }
            }
        }
        
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 1; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        this.posY + this.rand.nextDouble() * (double) this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        0.0D, 0.0D, 0.0D,
                        Blocks.RED_FLOWER.getStateId(Blocks.RED_FLOWER.getStateFromMeta(2)));
				this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						this.posY + this.rand.nextDouble() * (double) this.height,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						0.0D, 0.0D, 0.0D,
						Blocks.RED_FLOWER.getStateId(Blocks.RED_FLOWER.getStateFromMeta(8)));
            }
    }
    
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("faebolt", target, range, 0, new Vec3d(0, 0, 0), 0.75f, 1f, 1F);
        super.attackRanged(target, range);
    }
    
    @Override
    public boolean isFlying() { return true; }
    
    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 105, 2));
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
