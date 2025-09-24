package rlshenanigans.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityAIMineToTarget extends EntityAIBase {
    private final EntityLiving entity;
    private final float blockHardness;
    private final int range;
    private final int cooldownMax;
    
    private int cooldown;
    private Vec3d lastPos;
    private int stuckTicks = 0;
    
    public EntityAIMineToTarget(EntityLiving entity, float blockHardness, int range, int cooldownMax) {
        this.entity = entity;
        this.blockHardness = blockHardness;
        this.range = range;
        this.cooldownMax = cooldownMax;
        this.cooldown = cooldownMax;
        this.lastPos = entity.getPositionVector();
    }
    
    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = entity.getAttackTarget();
        return target != null && target.isEntityAlive();
    }
    
    @Override
    public void updateTask() {
        if (cooldown > 0) cooldown--;
        
        Vec3d currentPos = entity.getPositionVector();
        double moved = currentPos.distanceTo(lastPos);
        
        if (moved < 2.0) stuckTicks++;
        else {
            stuckTicks = 0;
            lastPos = currentPos;
        }
        
        EntityLivingBase target = entity.getAttackTarget();
        if (stuckTicks < 40 || cooldown > 0 || target == null || entity.canEntityBeSeen(target)) return;
        
        Vec3d start = entity.getPositionEyes(1.0F);
        Vec3d end = target.getPositionEyes(1.0F);
        RayTraceResult result = entity.world.rayTraceBlocks(start, end);
        
        boolean mined = false;
        
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = result.getBlockPos();
            double hitDistance = start.distanceTo(new Vec3d(hitPos).add(0.5, 0.5, 0.5));
            
            if (hitDistance > range) return;
            
            for (int dy = -1; dy <= 1; dy++) {
                BlockPos pos = hitPos.up(dy);
                IBlockState state = entity.world.getBlockState(pos);
                Block block = state.getBlock();
                float hardness = state.getBlockHardness(entity.world, pos);
                
                if (hardness > 0.0F && hardness <= blockHardness &&
                        block != Blocks.AIR &&
                        block.canEntityDestroy(state, entity.world, pos, entity) &&
                        ForgeEventFactory.onEntityDestroyBlock(entity, pos, state)) {
                    
                    entity.world.playEvent(2001, pos, Block.getStateId(state));
                    SoundType sound = block.getSoundType(state, entity.world, pos, entity);
                    entity.world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS,
                            (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                    
                    block.dropBlockAsItem(entity.world, pos, state, 0);
                    entity.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                    mined = true;
                }
            }
        }
        
        if (mined) {
            cooldown = cooldownMax + entity.getRNG().nextInt(5);
            stuckTicks = 0;
            lastPos = entity.getPositionVector();
        }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return true;
    }
}