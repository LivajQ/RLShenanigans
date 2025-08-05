package rlshenanigans.mixin.lycanitesmobs;

import com.lycanitesmobs.core.info.altar.AltarInfoLunarGrue;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rlshenanigans.entity.lycaniterideable.EntityGrue;

@Mixin(AltarInfoLunarGrue.class)
public class AltarInfoLunarGrueMixin {
    
    @Inject(method = "activate", at = @At("HEAD"), cancellable = true, remap = false)
    public void redirectGrueSummon(Entity entity, World world, BlockPos pos, int variant, CallbackInfoReturnable<Boolean> cir) {
        if (world.isRemote) {
            cir.setReturnValue(true);
            cir.cancel();
            return;
        }
        
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        
        int size = 4;
        for (int xTarget = x - size; xTarget <= x + size; xTarget++) {
            for (int zTarget = z - size; zTarget <= z + size; zTarget++) {
                for (int yTarget = y - size; yTarget <= y + size; yTarget++) {
                    BlockPos clearPos = new BlockPos(xTarget, yTarget, zTarget);
                    if (yTarget > 0 && world.getTileEntity(clearPos) == null) {
                        world.setBlockToAir(clearPos);
                    }
                }
            }
        }
        
        EntityGrue entityGrue = new EntityGrue(world);
        entityGrue.altarSummoned = true;
        entityGrue.forceBossHealthBar = true;
        entityGrue.applyVariant(3);
        entityGrue.setLocationAndAngles(x, y - 2, z, 0, 0);
        world.spawnEntity(entityGrue);
        
        cir.setReturnValue(true);
        cir.cancel();
    }
}