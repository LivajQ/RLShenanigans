package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntitySpectre extends RideableCreatureEntity implements IMob, IGroupHeavy
{
    
    protected int pullRange = 6;
    protected int pullEnergy = 0;
    protected int pullEnergyMax = 2 * 20;
    protected int pullEnergyRecharge = 0;
    protected int pullEnergyRechargeMax = 4 * 20;
    protected boolean pullRecharging = true;
    
    public EntitySpectre(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();
        
        this.stepHeight = 1.0F;
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote) {
            if(this.pullRecharging) {
                if(++this.pullEnergyRecharge >= this.pullEnergyRechargeMax) {
                    this.pullRecharging = false;
                    this.pullEnergy = this.pullEnergyMax;
                    this.pullEnergyRecharge = 0;
                }
            }
            this.pullEnergy = Math.min(this.pullEnergy, this.pullEnergyMax);
            if(this.canPull()) {
                for (EntityLivingBase entity : this.getNearbyEntities(EntityLivingBase.class, null, this.pullRange)) {
                    if (entity == this || entity == this.getControllingPassenger() || entity instanceof IGroupBoss || entity instanceof IGroupHeavy || entity.isPotionActive(ObjectManager.getEffect("weight")) || !this.canAttackEntity(entity))
                        continue;
                    EntityPlayerMP player = null;
                    if (entity instanceof EntityPlayerMP) {
                        player = (EntityPlayerMP) entity;
                        if (player.capabilities.isCreativeMode)
                            continue;
                    }
                    double xDist = this.posX - entity.posX;
                    double zDist = this.posZ - entity.posZ;
                    double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
                    double factor = 0.1D;
                    double motionCap = 10;
                    if(entity.motionX < motionCap && entity.motionX > -motionCap && entity.motionZ < motionCap && entity.motionZ > -motionCap) {
                        entity.addVelocity(
                                xDist / xzDist * factor + entity.motionX * factor,
                                0,
                                zDist / xzDist * factor + entity.motionZ * factor
                        );
                    }
                    if (player != null)
                        player.connection.sendPacket(new SPacketEntityVelocity(entity));
                }
                if(--this.pullEnergy <= 0) {
                    this.pullRecharging = true;
                    this.pullEnergyRecharge = 0;
                }
            }
        }
        
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
    }

    public boolean extraAnimation01() {
        if(this.getEntityWorld().isRemote) {
            return super.extraAnimation01();
        }
        return this.canPull();
    }
    
    public boolean canPull() {
        if(this.getEntityWorld().isRemote) {
            return this.extraAnimation01();
        }
        
        return !this.pullRecharging && this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) <= (this.pullRange * 3);
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
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(ObjectManager.getEffect("repulsion"), 105, 0));
        Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation("potioncore", "reach"));
        if (potion == null) return; rider.addPotionEffect(new PotionEffect(potion, 105, 1, true, true));
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
            
            }
        }
    }
    
    @Override
    public float getStaminaCost() {
        return 50.0F;
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