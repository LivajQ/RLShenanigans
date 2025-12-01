package rlshenanigans.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import rlshenanigans.handlers.CombatAssistHandler;
import rlshenanigans.item.ItemSpellBase;
import rlshenanigans.item.ItemSpellList;

import javax.vecmath.Color4f;
import java.util.*;

public class EntitySpellChainLightning extends EntitySpellBase implements ISpellLightning {
    private EntityLivingBase target;
    private EntityLivingBase previousTarget;
    private int lifetime;
    private final Set<EntityLivingBase> affectedEntities = new HashSet<>();
    private static final int TEXTURE_INDEX = ItemSpellBase.getTextureIndexFromEnum(EnumParticleTypes.SPELL_WITCH);
    private static final Color4f COLOR = new Color4f(1.0f, 1.0f, 0.0f, 0.7f);
    private static final DataParameter<Integer> TARGET_ID = EntityDataManager.createKey(EntitySpellChainLightning.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> PREVIOUS_TARGET_ID = EntityDataManager.createKey(EntitySpellChainLightning.class, DataSerializers.VARINT);
    
    public EntitySpellChainLightning(World world) {
        this(world, null, 300);
    }
    
    public EntitySpellChainLightning(World world, EntityLivingBase caster, int lifetime) {
        super(world, caster, 0.0F, 0.0F, 0.0F, 0.0F);
        this.noClip = true;
        this.lifetime = lifetime;
        this.setSize(0.5F, 0.5F);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TARGET_ID, -1);
        this.dataManager.register(PREVIOUS_TARGET_ID, -1);
        if (caster != null) {
            target = caster;
            affectedEntities.add(target);
            this.dataManager.set(TARGET_ID, target.getEntityId());
        }
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (this.ticksExisted < 10) return;
        if (target != null) this.setPosition(target.posX, target.posY + 0.5F * target.height, target.posZ);
        if (this.world.isRemote) return;
        
        if (lifetime-- <= 0) {
            this.setDead();
            return;
        }
        
        ItemSpellList.SPELL_CHAIN_LIGHTNING.spawnCastParticle(this, TEXTURE_INDEX, 1, 0.2F);
        
        if (this.ticksExisted % 10 == 0) {
            List<EntityLivingBase> entities = this.getEntityWorld().getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    this.getEntityBoundingBox().grow(10),
                    e -> e.canEntityBeSeen(this) && shouldTarget(e, caster, target)
            );
            
            List<EntityLivingBase> unaffectedEntities = new ArrayList<>();
            
            for (EntityLivingBase entity : entities) {
                if (!affectedEntities.contains(entity)) unaffectedEntities.add(entity);
            }
            
            if (!unaffectedEntities.isEmpty()) pickNewTarget(unaffectedEntities);
            else if (!entities.isEmpty()) pickNewTarget(entities);
            else {
                previousTarget = target;
                target = caster;
                this.dataManager.set(PREVIOUS_TARGET_ID, previousTarget != null ? previousTarget.getEntityId() : -1);
                this.dataManager.set(TARGET_ID, target != null ? target.getEntityId() : -1);
            }
            
            if (target != null && target != caster && !target.isDead) target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, caster), 10.0F);
        }
    }
    
    private boolean shouldTarget(EntityLivingBase e, EntityLivingBase caster, EntityLivingBase currentTarget) {
        if (e == currentTarget || e == caster) return false;
        if (caster instanceof EntityPlayer) return !CombatAssistHandler.isEntityTamedByPlayer(e, (EntityPlayer) caster);
        return true;
    }
    
    private void pickNewTarget(List<EntityLivingBase> candidates) {
        if (candidates.isEmpty()) return;
        
        previousTarget = target;
        this.dataManager.set(PREVIOUS_TARGET_ID, previousTarget != null ? previousTarget.getEntityId() : -1);
        
        int idx = rand.nextInt(candidates.size());
        target = candidates.get(idx);
        affectedEntities.add(target);
        
        this.dataManager.set(TARGET_ID, target != null ? target.getEntityId() : -1);
    }
    
    public EntityLivingBase getTargetData() {
        int id = this.dataManager.get(TARGET_ID);
        Entity entity = this.world.getEntityByID(id);
        return id == -1 || !(entity instanceof EntityLivingBase) ? null : (EntityLivingBase) entity;
    }
    
    public EntityLivingBase getPreviousTargetData() {
        int id = this.dataManager.get(PREVIOUS_TARGET_ID);
        Entity entity = this.world.getEntityByID(id);
        return id == -1 || !(entity instanceof EntityLivingBase) ? null : (EntityLivingBase) entity;
    }
    
    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (!this.world.isRemote) return;
        
        if (TARGET_ID.equals(key) || PREVIOUS_TARGET_ID.equals(key)) {
            target = getTargetData();
            previousTarget = getPreviousTargetData();
        }
    }
    
    @Override
    public EntityLivingBase getTarget() {
        return target;
    }
    
    @Override
    public EntityLivingBase getPreviousTarget() {
        return previousTarget;
    }
    
    @Override
    public Color4f getColor() {
        return COLOR;
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.setDead();
    }
}