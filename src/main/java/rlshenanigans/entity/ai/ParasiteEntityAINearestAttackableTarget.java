package rlshenanigans.entity.ai;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import rlshenanigans.potion.PotionPookie;

public class ParasiteEntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget {
    protected final Class<T> targetClass;
    private final int targetChance;
    protected final Sorter sorter;
    protected final Predicate<? super T> targetEntitySelector;
    protected T targetEntity;
    
    public ParasiteEntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
        this(creature, classTarget, checkSight, false);
    }
    
    public ParasiteEntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
        this(creature, classTarget, 10, checkSight, onlyNearby, null);
    }
    
    public ParasiteEntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.targetClass = classTarget;
        this.targetChance = chance;
        this.sorter = new Sorter(creature);
        this.setMutexBits(1);
        
        this.targetEntitySelector = new Predicate<T>() {
            public boolean apply(@Nullable T target) {
                if (target == null) return false;
                
                if (target instanceof EntityPlayer && ((EntityPlayer) target).isPotionActive(PotionPookie.INSTANCE)) {
                    return false;
                }
                
                if (targetSelector != null && !targetSelector.apply(target)) {
                    return false;
                }
                
                return EntitySelectors.NOT_SPECTATING.apply(target)
                        && ParasiteEntityAINearestAttackableTarget.this.isSuitableTarget(target, false);
            }
        };
    }
    
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        }
        
        if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
            List<T> list = this.taskOwner.world.getEntitiesWithinAABB(
                    this.targetClass,
                    this.getTargetableArea(this.getTargetDistance()),
                    this.targetEntitySelector
            );
            
            if (list.isEmpty()) return false;
            
            Collections.sort(list, this.sorter);
            this.targetEntity = list.get(0);
            return true;
        } else {
            EntityPlayer player = this.taskOwner.world.getNearestAttackablePlayer(
                    this.taskOwner.posX,
                    this.taskOwner.posY + (double) this.taskOwner.getEyeHeight(),
                    this.taskOwner.posZ,
                    this.getTargetDistance(),
                    this.getTargetDistance(),
                    new Function<EntityPlayer, Double>() {
                        @Nullable
                        public Double apply(@Nullable EntityPlayer p) {
                            ItemStack itemstack = p.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                            if (itemstack.getItem() == Items.SKULL) {
                                int i = itemstack.getItemDamage();
                                boolean skeletonMatch = taskOwner instanceof EntitySkeleton && i == 0;
                                boolean zombieMatch = taskOwner instanceof EntityZombie && i == 2;
                                boolean creeperMatch = taskOwner instanceof EntityCreeper && i == 4;
                                if (skeletonMatch || zombieMatch || creeperMatch) {
                                    return 0.5D;
                                }
                            }
                            return 1.0D;
                        }
                    },
                    (Predicate<EntityPlayer>) this.targetEntitySelector
            );
            
            if (player == null || player.isPotionActive(PotionPookie.INSTANCE)) return false;
            if (!targetClass.isInstance(player)) return false;
            
            this.targetEntity = (T) player;
            return true;
        }
    }
    
    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
    }
    
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
    
    public static class Sorter implements Comparator<Entity> {
        private final Entity entity;
        
        public Sorter(Entity entityIn) {
            this.entity = entityIn;
        }
        
        public int compare(Entity a, Entity b) {
            double d0 = entity.getDistanceSq(a);
            double d1 = entity.getDistanceSq(b);
            return Double.compare(d0, d1);
        }
    }
}