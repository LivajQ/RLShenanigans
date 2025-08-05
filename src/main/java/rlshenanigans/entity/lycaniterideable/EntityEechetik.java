package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.PotionBase;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityEechetik extends RideableCreatureEntity implements IMob
{
    
    public int myceliumRadius = 2;

    public EntityEechetik(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        
        this.setupMob();
        
        this.stepHeight = 1.0F;
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }
    
    @Override
    public void loadCreatureFlags() {
        this.myceliumRadius = this.creatureInfo.getFlag("myceliumRadius", this.myceliumRadius);
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.hasAttackTarget()) {
            PotionBase plague = ObjectManager.getEffect("plague");
            if(plague != null) {
                PotionEffect potionEffect = new PotionEffect(plague, this.getEffectDuration(2), 1);
                List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 2);
                for(Object entityObj : aoeTargets) {
                    EntityLivingBase target = (EntityLivingBase) entityObj;
                    if (target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target) && target.isPotionApplicable(potionEffect)) {
                        target.addPotionEffect(potionEffect);
                    }
                }
            }
        }

        if(!this.getEntityWorld().isRemote && this.updateTick % 100 == 0 && this.myceliumRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
            int range = this.myceliumRadius;
            for (int w = -((int) Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++) {
                for (int d = -((int) Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++) {
                    for (int h = -((int) Math.ceil(this.height) + range); h <= Math.ceil(this.height); h++) {
                        BlockPos blockPos = this.getPosition().add(w, h, d);
                        IBlockState blockState = this.getEntityWorld().getBlockState(blockPos);
                        IBlockState upperIBlockState = this.getEntityWorld().getBlockState(blockPos.up());
                        if (upperIBlockState.getBlock() == Blocks.AIR && blockState.getBlock() == Blocks.DIRT) {
                            this.getEntityWorld().setBlockState(blockPos, Blocks.MYCELIUM.getDefaultState());
                        }
                    }
                }
            }
        }

        if(this.getEntityWorld().isRemote) {
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, 0.0D, 0.0D, 0.0D);
                this.getEntityWorld().spawnParticle(EnumParticleTypes.TOWN_AURA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
            return false;
        return true;
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
    
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 0F;
        else return super.getDamageModifier(damageSrc);
    }
    
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(ObjectManager.getEffect("immunization"), 105, 0));
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
                this.applyStaminaCost();
                
                double distance = 10.0D;
                List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
                    if(!possibleTarget.isEntityAlive()
                            || possibleTarget == EntityEechetik.this
                            || EntityEechetik.this.isRidingOrBeingRiddenBy(possibleTarget)
                            || EntityEechetik.this.isOnSameTeam(possibleTarget)
                            || !EntityEechetik.this.canAttackClass(possibleTarget.getClass())
                            || !EntityEechetik.this.canAttackEntity(possibleTarget))
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
                            if (ObjectManager.getEffect("plague") != null) {
                                possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("plague"), this.getEffectDuration(20), 2));
                                possibleTarget.addPotionEffect(new PotionEffect(MobEffects.POISON, 400, 2));
                            }
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
