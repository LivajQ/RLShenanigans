package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityAsmodeus;
import com.lycanitesmobs.core.entity.creature.EntityAstaroth;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EntityTrite extends RideableCreatureEntity implements IMob {
    
    public EntityTrite(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(this.hasAttackTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0)
            this.leap(6.0F, 0.6D, this.getAttackTarget());
    }
    
    @Override
    public boolean canAttackEntity(EntityLivingBase target) {
        if(target instanceof EntityAstaroth ||  target instanceof EntityAsmodeus)
            return false;
        return super.canAttackEntity(target);
    }
    
    @Override
    public boolean canClimb() { return true; }
    
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    
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
    public float getFallResistance() {
        return 10;
    }
    
    @Override
    public boolean webProof() { return true; }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(ObjectManager.getEffect("immunization"), 105, 0));
        rider.addPotionEffect(new PotionEffect(ObjectManager.getEffect("leech"), 105, 1));
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {

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
