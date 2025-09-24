package rlshenanigans.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rlshenanigans.entity.npc.EntityNPCBase;
import rlshenanigans.util.WeaponRegistry;

public class EntityAINPCAttackMelee extends EntityAIAttackMelee {
    protected final EntityNPCBase npc;
    private final double speedTowardsTarget;
    
    public EntityAINPCAttackMelee(EntityNPCBase npc, double speedIn, boolean useLongMemory) {
        super(npc, speedIn, useLongMemory);
        this.npc = npc;
        this.speedTowardsTarget = speedIn;
    }
    
    @Override
    protected double getAttackReachSqr(EntityLivingBase target) {
        double baseReach = npc.baseReach;
        
        ItemStack weapon = this.attacker.getHeldItemMainhand();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(weapon.getItem());
        
        WeaponRegistry registry = WeaponRegistry.getWeaponByResourceLocation(id);
        double bonus = registry != null ? registry.getReachBonus() : 0.0;
        
        double effectiveReach = Math.max(1.0, baseReach + bonus);
        return effectiveReach * effectiveReach;
    }
    
    @Override
    public void updateTask() {
        if (this.attacker.getAttackTarget() == null) return;
        super.updateTask();
        
        EntityLivingBase target = this.attacker.getAttackTarget();
        double distanceSq = this.attacker.getDistanceSq(target);
        double reachSq = getAttackReachSqr(target);
        
        if (distanceSq <= reachSq) this.attacker.getNavigator().tryMoveToEntityLiving(target, 0.01);
         else this.attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget);
    }
    
    @Override
    protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
        if (npc.isBlocking() || !npc.canEntityBeSeen(enemy)) return;
        super.checkAndPerformAttack(enemy, distToEnemySqr);
    }
}