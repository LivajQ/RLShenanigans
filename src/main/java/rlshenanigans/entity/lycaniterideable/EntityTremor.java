package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntityTremor extends RideableCreatureEntity implements IMob
{

    public EntityTremor(World world) {
        super(world);

        this.attribute = EnumCreatureAttribute.UNDEFINED;
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
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote && this.isRareVariant() && !this.isPetType("familiar")) {

            if(this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
                if(this.posY - 1 > this.getAttackTarget().posY)
                    this.leap(6.0F, -1.0D, this.getAttackTarget());
                else if(this.posY + 1 < this.getAttackTarget().posY)
                    this.leap(6.0F, 1.0D, this.getAttackTarget());
                else
                    this.leap(6.0F, 0D, this.getAttackTarget());
            }
        }

        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
    }
    
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
            return false;
        
        int explosionStrength = Math.max(0, this.creatureInfo.getFlag("explosionStrength", 1));
        boolean damageTerrain = explosionStrength > 0 && this.getEntityWorld().getGameRules().getBoolean("mobGriefing");
        if(this.isPetType("familiar")) {
            explosionStrength = 1;
            damageTerrain = false;
        }
        this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionStrength, damageTerrain);
        
        return true;
    }
    
    @Override
    public boolean isFlying() { return true; }

    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(source.isExplosion()) {
            this.heal(damage);
            return false;
        }
        if(type.equals("cactus") || type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean isDamageEntityApplicable(Entity entity) {
        if(entity instanceof EntityWither) {
            return false;
        }
        return super.isDamageEntityApplicable(entity);
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect effectInstance) {
        if(effectInstance.getPotion() == MobEffects.WITHER) {
            return false;
        }
        return super.isPotionApplicable(effectInstance);
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean canBeTargetedBy(EntityLivingBase entity) {
        if(entity instanceof EntityWither) {
            return false;
        }
        return super.canBeTargetedBy(entity);
    }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
    
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost())
            {
                this.applyStaminaCost();
                this.playAttackSound();
                
                World world = this.world;
                double x = this.posX;
                double y = this.posY;
                double z = this.posZ;
                
                world.createExplosion(this, x, y, z, 20.0F, true);
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
