package rlshenanigans.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntitySpellCloudPoison extends EntitySpellBase {
    private int lifetime = 25;
    private final EntityLivingBase caster;
    private final double mX;
    private final double mY;
    private final double mZ;
    private final Set<UUID> affectedEntities = new HashSet<>();
    
    public EntitySpellCloudPoison(World world) {
        this(world, null, 0, 0, 0, 0, 0, 0);
    }
    
    public EntitySpellCloudPoison(World world, EntityLivingBase caster, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, 0.0F, 0.0F, 0.0F, 0.0F);
        this.setPosition(x, y, z);
        this.mX = motionX * 0.4D;
        this.mY = motionY * 0.4D;
        this.mZ = motionZ * 0.4D;
        this.caster = caster;
        this.noClip = true;
        this.setSize(2.5F, 2.5F);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        this.move(MoverType.SELF, mX, mY, mZ);
        
        if (!world.isRemote) {
            List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox());
            for (EntityLivingBase target : targets) {
                if (target != caster && !affectedEntities.contains(target.getUniqueID())) {
                    target.addPotionEffect(new PotionEffect(MobEffects.POISON, 60, 1));
                    target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60, 1));
                    affectedEntities.add(target.getUniqueID());
                }
            }
        }
        
        if (--lifetime <= 0) this.setDead();
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.setDead();
    }
}