package rlshenanigans.item;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Color3f;

public class ItemSpellArmyOfDarkness extends ItemSpellBase {
    
    public ItemSpellArmyOfDarkness(String registryName, int manaCost, int castTime, int stackSize) {
        super(registryName, manaCost, castTime, stackSize);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.ENTITY_WITHER_SPAWN, 1.0F, 0.7F);
        this.playCastSound(caster, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
        this.particle(caster);
        
        World world = caster.world;
        Vec3d look = caster.getLookVec().normalize();
        Vec3d origin = caster.getPositionVector();
        DifficultyInstance difficulty = caster.world.getDifficultyForLocation(caster.getPosition());
        
        for (int i = -1; i <= 1; i++) {
            Vec3d frontPos = origin.add(look.scale(2.0)).add(look.rotateYaw(90).scale(i));
            Vec3d backPos = origin.subtract(look.scale(2.0)).add(look.rotateYaw(90).scale(i));
            
            EntityZombie zombie = new EntityZombie(world);
            zombie.setCustomNameTag(caster.getName() + "'s Zombie");
            zombie.getEntityData().setUniqueId("OwnerUUID", caster.getUniqueID());
            zombie.getEntityData().setBoolean("MiscTamed", true);
            zombie.getEntityData().setBoolean("Unrideable", true);
            zombie.getEntityData().setInteger("RLSSummonLifetime", 1200);
            zombie.onInitialSpawn(difficulty, null);
            zombie.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            trySpawnWithFallback(caster, zombie, frontPos, world);
            
            EntitySkeleton skeleton = new EntitySkeleton(world);
            skeleton.setCustomNameTag(caster.getName() + "'s Skeleton");
            skeleton.getEntityData().setUniqueId("OwnerUUID", caster.getUniqueID());
            skeleton.getEntityData().setBoolean("MiscTamed", true);
            skeleton.getEntityData().setBoolean("Unrideable", true);
            skeleton.getEntityData().setInteger("RLSSummonLifetime", 1200);
            skeleton.onInitialSpawn(difficulty, null);
            skeleton.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            trySpawnWithFallback(caster, skeleton, backPos, world);
        }
    }
    
    private void trySpawnWithFallback(EntityLivingBase caster, EntityLiving entity, Vec3d basePos, World world) {
        entity.setPosition(basePos.x, basePos.y, basePos.z);
        if (entity.getCanSpawnHere()) {
            entity.setPosition(entity.posX, entity.posY + 1, entity.posZ);
            world.spawnEntity(entity);
            this.particle(entity);
            return;
        }
        
        for (int i = 0; i < 10; i++) {
            double offsetX = (world.rand.nextDouble() - 0.5) * 6.0;
            double offsetZ = (world.rand.nextDouble() - 0.5) * 6.0;
            BlockPos tryPos = new BlockPos(basePos.x + offsetX, basePos.y, basePos.z + offsetZ);
            entity.setPosition(tryPos.getX() + 0.5, tryPos.getY(), tryPos.getZ() + 0.5);
            if (entity.getCanSpawnHere()) {
                entity.setPosition(entity.posX, entity.posY + 1, entity.posZ);
                world.spawnEntity(entity);
                this.particle(entity);
                return;
            }
        }
        
        entity.setPosition(caster.posX, caster.posY + 1, caster.posZ);
        world.spawnEntity(entity);
        this.particle(entity);
    }
    
    private void particle(EntityLivingBase entity) {
        int textureIndex = getTextureIndexFromEnum(EnumParticleTypes.REDSTONE);
        int particleCount = 20;
        int particleAge = 30;
        double baseX = entity.posX;
        double baseY = entity.posY;
        double baseZ = entity.posZ;
        
        for (int i = 0; i < particleCount; i++) {
            double upward = 0.1D + entity.world.rand.nextDouble() * 0.2D;
            double sideX = (entity.world.rand.nextDouble() - 0.5D) * 0.4D;
            double sideZ = (entity.world.rand.nextDouble() - 0.5D) * 0.4D;
            
            spawnCastParticle(textureIndex, 1, particleAge, baseX, baseY, baseZ, sideX, upward, sideZ);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Color3f getParticleColor() {
        return new Color3f(0.6F, 0.0F, 0.0F);
    }
}