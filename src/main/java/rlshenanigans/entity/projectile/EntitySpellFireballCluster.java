package rlshenanigans.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySpellFireballCluster extends EntitySpellFireball {
    public EntitySpellFireballCluster(World world) {
        super(world);
    }
    
    public EntitySpellFireballCluster(World worldIn, EntityLivingBase shooter, float damage, float size, float strength) {
        this(worldIn, shooter, damage, size, strength, 0.0F);
    }
    
    public EntitySpellFireballCluster(World worldIn, EntityLivingBase shooter, float damage, float size, float strength, float gravity) {
        super(worldIn, shooter, damage, size, strength, gravity);
    }
    
    @Override
    protected void onImpact(RayTraceResult result) {
        if (shooter == null || this.world.isRemote) return;
        
        double[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, // N, S, E, W
                {1, 1}, {-1, 1}, {1, -1}, {-1, -1} // NE, NW, SE, SW
        };
        
        for (double[] dir : directions) {
            EntitySpellFireball mini = new EntitySpellFireball(world, shooter, damage * 0.25F, 0.3F, 2.0F, 0.1F);
            mini.setPosition(posX + dir[0], posY + 2.0D, posZ + dir[1]);
            double speed = 0.55 + rand.nextDouble() * 0.2;
            mini.motionX = dir[0] * speed;
            mini.motionY = 1.2 + rand.nextDouble() * 0.5;
            mini.motionZ = dir[1] * speed;
            world.spawnEntity(mini);
        }
        
        super.onImpact(result);
    }
}