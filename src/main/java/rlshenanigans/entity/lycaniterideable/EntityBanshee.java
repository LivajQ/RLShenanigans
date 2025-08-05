package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityShade;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityBanshee extends RideableCreatureEntity implements IMob
{
    
    private int strafeTime = 60;
    public EntityBanshee(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.trueSight = true;
        this.setupMob();
        
        this.noClip = true;
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }
    
    @Override
    public void onLivingUpdate() {
        this.noClip = !this.isBeingRidden();
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
            if(this.strafeTime-- <= 0) {
                this.strafeTime = 60 + this.getRNG().nextInt(40);
                this.strafe(this.getRNG().nextBoolean() ? -1F : 1F, 0D);
            }
        }
        
        if(this.getEntityWorld().isRemote)
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
    }
    
    @Override
    public boolean isFlying() { return true; }
    
    @Override
    public boolean useDirectNavigator() {
        return !isTamed();
    }
    
    @Override
    public boolean canEntityBeSeen(Entity target) {
        return true;
    }
    
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
    
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
                this.applyStaminaCost();
                
                double distance = 10.0D;
                List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
                    if(!possibleTarget.isEntityAlive()
                            || possibleTarget == EntityBanshee.this
                            || EntityBanshee.this.isRidingOrBeingRiddenBy(possibleTarget)
                            || EntityBanshee.this.isOnSameTeam(possibleTarget)
                            || !EntityBanshee.this.canAttackClass(possibleTarget.getClass())
                            || !EntityBanshee.this.canAttackEntity(possibleTarget))
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
                            if (ObjectManager.getEffect("fear") != null)
                                possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("fear"), this.getEffectDuration(5), 1));
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

