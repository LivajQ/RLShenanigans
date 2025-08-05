package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GetBlockGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.GetItemGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class EntityKobold extends RideableCreatureEntity implements IMob
{
    public boolean griefing = true;
    public boolean theivery = true;
    
    public EntityKobold(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;
        
        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }
    
    @Override
    protected void initEntityAI() {
        this.tasks.addTask(this.nextIdleGoalIndex++, new GetItemGoal(this).setDistanceMax(8).setSpeed(1.2D));
        if(this.griefing)
            this.tasks.addTask(this.nextIdleGoalIndex++, new GetBlockGoal(this).setDistanceMax(8).setSpeed(1.2D).setBlockName("torch").setTamedLooting(false));
        
        super.initEntityAI();
        
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }
    
    @Override
    public void loadCreatureFlags() {
        this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
        this.theivery = this.creatureInfo.getFlag("theivery", this.theivery);
    }
    
    @Override
    protected void despawnEntity() {
        super.despawnEntity();
        if (this.isDead && !this.isTamed() && this.inventory.hasBagItems()) {
            this.inventory.dropInventory();
        }
    }
    
    private int torchLootingTime = 20;
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.griefing) {
            if(this.torchLootingTime-- <= 0) {
                this.torchLootingTime = 60;
                int distance = 2;
                String targetName = "torch";
                List possibleTargets = new ArrayList<BlockPos>();
                for(int x = (int)this.posX - distance; x < (int)this.posX + distance; x++) {
                    for(int y = (int)this.posY - distance; y < (int)this.posY + distance; y++) {
                        for(int z = (int)this.posZ - distance; z < (int)this.posZ + distance; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            Block searchBlock = this.getEntityWorld().getBlockState(pos).getBlock();
                            if(searchBlock != Blocks.AIR) {
                                BlockPos possibleTarget = null;
                                if(ObjectLists.isName(searchBlock, targetName)) {
                                    this.getEntityWorld().destroyBlock(pos, true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean shouldCreatureGroupRevenge(EntityLivingBase target) {
        if(target instanceof EntityPlayer && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
            return true;
        return super.shouldCreatureGroupRevenge(target);
    }
    
    @Override
    public boolean shouldCreatureGroupHunt(EntityLivingBase target) {
        if(target instanceof EntityPlayer && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
            return true;
        return super.shouldCreatureGroupHunt(target);
    }
    
    @Override
    public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
        if(target instanceof EntityPlayer && (target.getHealth() / target.getMaxHealth()) <= 0.5F)
            return false;
        return super.shouldCreatureGroupFlee(target);
    }
    
    @Override
    public boolean canAttackEntity(EntityLivingBase targetEntity) {
        if(!this.isTamed() && (targetEntity.getHealth() / targetEntity.getMaxHealth()) > 0.5F)
            return false;
        return super.canAttackEntity(targetEntity);
    }
    
    @Override
    public boolean canPickupItems() {
        return this.theivery;
    }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(MobEffects.LUCK, 105, 9));
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
                this.applyStaminaCost();
                this.playAttackSound();
                
                int radius = 15;
                BlockPos mobPos = new BlockPos(this.posX, this.posY, this.posZ);
                
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos checkPos = mobPos.add(x, y, z);
                            IBlockState state = this.world.getBlockState(checkPos);
                            Block block = state.getBlock();
                            
                            if (block == Blocks.TORCH || block.getRegistryName().toString().contains("torch")) {
                                this.world.setBlockToAir(checkPos);
                                this.world.playEvent(2001, checkPos, Block.getStateId(state));
                                block.dropBlockAsItem(this.world, checkPos, state, 0);
                            }
                        }
                    }
                }
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