package rlshenanigans.item;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import com.github.alexthe666.iceandfire.capability.entityeffect.EntityEffectProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.handlers.RLSSoundHandler;

import javax.vecmath.Color3f;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class ItemSpellRayOfFrost extends ItemSpellBase {
    Set<EntityLivingBase> hitEntities = new HashSet<>();

    public ItemSpellRayOfFrost(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        World world = caster.world;
        Vec3d start = caster.getPositionVector().add(0, caster.getEyeHeight(), 0);
        Vec3d look = caster.getLookVec().normalize();
        double maxDistance = 64.0;
        double speed = 0.025;
        
        this.playCastSound(caster, RLSSoundHandler.SPELL_RAY_OF_FROST, 1.0F, 0.8F);
        this.playCastSound(caster, SoundEvents.ENTITY_IRONGOLEM_HURT, 1.0F, 0.5F);
        
        double step = 0.5;
        
        for (double d = 0; d <= maxDistance; d += step) {
            Vec3d pos = start.add(look.scale(d));
            
            BlockPos blockPos = new BlockPos(pos);
            IBlockState state = world.getBlockState(blockPos);
            if (state.isOpaqueCube()) break;
            
            AxisAlignedBB box = new AxisAlignedBB(
                    pos.x - 0.25, pos.y - 0.25, pos.z - 0.25,
                    pos.x + 0.25, pos.y + 0.25, pos.z + 0.25
            );
            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, box,
                    e -> e != caster && e.isEntityAlive() && !hitEntities.contains(e));
            
            for (EntityLivingBase target : entities) {
                target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(caster, caster), 10.0F);
                IEntityEffectCapability cap = target.getCapability(EntityEffectProvider.ENTITY_EFFECT, null);
                if (cap != null) cap.setFrozen();
                
                hitEntities.add(target);
            }
            
            double xSpeed = (RLSRAND.nextDouble() * 2.0 - 1.0) * speed;
            double ySpeed = (RLSRAND.nextDouble() * 2.0 - 1.0) * speed;
            double zSpeed = (RLSRAND.nextDouble() * 2.0 - 1.0) * speed;
            
            this.spawnCastParticle(getTextureIndexFromEnum(EnumParticleTypes.REDSTONE), 1, 60, pos.x, pos.y, pos.z, xSpeed, ySpeed, zSpeed);
        }
        
        hitEntities.clear();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Color3f getParticleColor() {
        return new Color3f(0.4F, 0.7F, 1.0F);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public float getParticleAlpha() {
        return 0.6F;
    }
}