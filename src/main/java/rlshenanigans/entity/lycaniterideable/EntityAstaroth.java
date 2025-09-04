package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityAsmodeus;
import com.lycanitesmobs.core.entity.creature.EntityCacodemon;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellShield;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;


public class EntityAstaroth extends RideableCreatureEntity implements IMob
{
    public EntityAstaroth(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.solidCollision = false;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;
        
        this.stepHeight = 2.0F;
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false).setRange(1D).setMaxChaseDistance(8.0F));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(40.0F).setMinChaseDistance(16.0F).setChaseTime(-1));
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(this.updateTick % 20 == 0) {
            if (this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityAsmodeus && ((BaseCreatureEntity)this.getMasterTarget()).getBattlePhase() > 0) {
                EntityHellShield projectile = new EntityHellShield(this.getEntityWorld(), this);
                projectile.setProjectileScale(3f);
                projectile.posY -= this.height * 0.35D;
                double dX = this.getMasterTarget().posX - this.posX;
                double dY = this.getMasterTarget().posY + (this.getMasterTarget().height * 0.75D) - projectile.posY;
                double dZ = this.getMasterTarget().posZ - this.posZ;
                double distance = MathHelper.sqrt(dX * dX + dZ * dZ) * 0.1F;
                float velocity = 0.8F;
                projectile.shoot(dX, dY + distance, dZ, velocity, 0.0F);
                this.getEntityWorld().spawnEntity(projectile);
            }
        }
    }

    @Override
    public boolean canAttackEntity(EntityLivingBase target) {
        if(target instanceof EntityTrite || target instanceof EntityCacodemon ||  target instanceof EntityAsmodeus)
            return false;
        return super.canAttackEntity(target);
    }
    
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("devilstar", target, range, 0, new Vec3d(0, -2.8F, 0), 1.2f, 1f, 1F);
        super.attackRanged(target, range);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if(!this.getEntityWorld().isRemote && CreatureManager.getInstance().getCreature("trite").enabled) {
            int j = 2 + this.rand.nextInt(5) + 2 - 1;
            if(this.isTamed()) {
                j = 3;
            }
            for(int k = 0; k < j; ++k) {
                EntityTrite trite = (EntityTrite)CreatureManager.getInstance().getCreature("trite").createEntity(this.getEntityWorld());
                this.summonMinion(trite, this.rand.nextFloat() * 360.0F, 0.5F);
                if(this.isTamed()) {
                    trite.setTemporary(5 * 20);
                }
            }
        }
        super.onDeath(damageSource);
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
    @Override
    public void mountAbility(Entity rider)
    {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled)
        {
            EntityPlayer player = (EntityPlayer) rider;
            if (this.getStamina() >= this.getStaminaCost())
            {
                this.applyStaminaCost();
                this.playAttackSound();
                
                ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("devilstar");
                if (projectileInfo == null)
                {
                    return;
                }
                
                RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), player, 15, 3);
                
                projectileEntry.setProjectileScale(1f);
                
                projectileEntry.posY -= this.height / 4;
                
                this.playSound(projectileEntry.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                this.getEntityWorld().spawnEntity(projectileEntry);
            }
        }
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