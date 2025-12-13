package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rlshenanigans.handlers.ForgeConfigHandler;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class ItemSpellBlink extends ItemSpellBase {

    public ItemSpellBlink(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        double maxDistance = 50.0D;
        World world = caster.getEntityWorld();
        
        RayTraceResult hit = caster.rayTrace(maxDistance, 1.0F);
        
        double targetX;
        double targetY;
        double targetZ;
        
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            BlockPos originalPos = pos;
            
            final int maxOffset = 5;
            boolean success = false;
            
            for (int x = 1; x <= 300; x++) {
                if (world.isAirBlock(pos) && world.isAirBlock(pos.up()) && pos.getY() > 5.0F) {
                    success = true;
                    break;
                }
                pos = new BlockPos(originalPos.getX() + randomOffset(maxOffset),originalPos.getY() + randomOffset(maxOffset), originalPos.getZ() + randomOffset(maxOffset));
            }
            
            if (!success) {
                this.castFailed = true;
                return;
            }
            
            targetX = pos.getX() + 0.5;
            targetY = pos.getY() + 1;
            targetZ = pos.getZ() + 0.5;
        }
        
        else {
            Vec3d lookVec = caster.getLookVec();
            targetX = caster.posX + lookVec.x * maxDistance;
            targetY = caster.posY + lookVec.y * maxDistance;
            targetZ = caster.posZ + lookVec.z * maxDistance;
        }
        
        world.playSound(null, caster.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL,1.0F, 1.2F);
        world.playEvent(2003, caster.getPosition(), 0);
        
        caster.setPositionAndUpdate(targetX, targetY, targetZ);
        caster.motionY = 0.0;
        
        world.playSound(null, caster.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL,1.0F, 1.2F);
        world.playEvent(2003, caster.getPosition(), 0);
    }
    
    private int randomOffset(int maxOffset) {
        int offset = RLSRAND.nextInt(maxOffset + 1);
        return RLSRAND.nextBoolean() ? offset : -offset;
    }
}