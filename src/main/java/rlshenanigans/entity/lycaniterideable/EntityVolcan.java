package rlshenanigans.entity.lycaniterideable;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;

public class EntityVolcan extends RideableCreatureEntity implements IMob
{
    
    public int blockMeltingRadius = 2;
    
    public EntityVolcan(World world) {
        super(world);
        
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();
        
        this.stepHeight = 1.0F;
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(2, new AttackMeleeGoal(this).setLongMemory(true));
    }
    
    @Override
    public void loadCreatureFlags() {
        this.blockMeltingRadius = this.creatureInfo.getFlag("blockMeltingRadius", this.blockMeltingRadius);
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0) {
            List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
            for(Object entityObj : aoeTargets) {
                EntityLivingBase target = (EntityLivingBase)entityObj;
                if(target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
                    target.setFire(2);
                }
            }
        }
        
        if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.blockMeltingRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
            int range = this.blockMeltingRadius;
            for (int w = -((int) Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++) {
                for (int d = -((int) Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++) {
                    for (int h = -((int) Math.ceil(this.height) + range); h <= Math.ceil(this.height); h++) {
                        Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
                        if (block == Blocks.COBBLESTONE || block == Blocks.GRAVEL) {
                            IBlockState blockState = Blocks.FLOWING_LAVA.getStateFromMeta(11);
                            this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
                        }
						/*else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.ICE || block == Blocks.SNOW) {
							this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), Blocks.AIR.getDefaultState(), 3);
						}*/
                    }
                }
            }
        }
        
        if(this.getEntityWorld().isRemote) {
            for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
                this.getEntityWorld().spawnParticle(EnumParticleTypes.DRIP_LAVA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
            if(this.ticksExisted % 10 == 0)
                for(int i = 0; i < 2; ++i) {
                    this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
                }
        }
    }
    
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
            return false;
        
        if(target instanceof EntitySilverfish) {
            target.setDead();
        }
        
        return true;
    }

    @Override
    public boolean isFlying() { return true; }
    
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
        HashMap<Integer, String> commands = new HashMap<>();
        commands.putAll(super.getInteractCommands(player, hand, itemStack));
        
        if(itemStack != null) {
            
            if(itemStack.getItem() == Items.BUCKET && this.isTamed())
                commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Water");
        }
        
        return commands;
    }
    
    @Override
    public boolean performCommand(String command, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
        
        if(command.equals("Water")) {
            this.replacePlayersItem(player, hand, itemStack, new ItemStack(Items.LAVA_BUCKET));
            return true;
        }
        
        return super.performCommand(command, player, hand, itemStack);
    }
    
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
        if(type.equals("cactus") || type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    @Override
    public void riderEffects(EntityLivingBase rider) {
    
    }
    
    @Override
    public void mountAbility(Entity rider) {
        if (!this.getEntityWorld().isRemote && !this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
                this.applyStaminaCost();
                
                String projectileName = "magma";
                float velocity = 0.4F;
                float scale = 2.0F;
                float inaccuracy = 0.0F;
                float angle = 360.0F;
                double spawnHeightOffset = 3.0;
                int randomCount = 5;
                
                for (int i = 0; i < randomCount; i++) {
                    ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(projectileName);
                    if (projectileInfo != null) {
                        BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
                        
                        if (projectile != null) {
                            projectile.setProjectileScale(scale);
                            
                            float pitch = this.getRNG().nextFloat() * 10.0F;
                            float yaw = this.getRNG().nextFloat() * angle;
                            
                            projectile.setPosition(this.posX, this.posY + spawnHeightOffset, this.posZ);
                            projectile.shoot(this, pitch, yaw, 0.0F, velocity, inaccuracy);
                            
                            
                            this.getEntityWorld().spawnEntity(projectile);
                        }
                    }
                }
                
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
