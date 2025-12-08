package rlshenanigans.entity;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import rlshenanigans.handlers.CombatAssistHandler;
import rlshenanigans.item.ItemSpellBase;
import rlshenanigans.item.ItemSpellList;

import javax.vecmath.Color4f;
import java.util.*;

public class EntitySpellChainLightning extends EntitySpellBase implements ISpellLightning {
    private EntityLivingBase parent;
    private EntityLivingBase child;
    private int lifetime;
    private final Set<EntityLivingBase> affectedEntities = new HashSet<>();
    private final Map<EntityLivingBase, Set<EntityLivingBase>> lightningConnections = new HashMap<>();
    private static final int TEXTURE_INDEX = ItemSpellBase.getTextureIndexFromEnum(EnumParticleTypes.SPELL_WITCH);
    private static final Color4f COLOR = new Color4f(1.0f, 1.0f, 0.0f, 0.7f);

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
    public void onUpdate() {
        super.onUpdate();
        
        if (this.ticksExisted < 10) return;
        if (parent != null) this.setPosition(parent.posX, parent.posY + 0.5F * parent.height, parent.posZ);
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
                    e -> e.canEntityBeSeen(this) && shouldTarget(e, caster, parent)
            );
            
            List<EntityLivingBase> unaffectedEntities = new ArrayList<>();
            
            for (EntityLivingBase entity : entities) {
                if (!affectedEntities.contains(entity)) unaffectedEntities.add(entity);
            }
            
            if (!unaffectedEntities.isEmpty()) pickNewTarget(unaffectedEntities);
            else if (!entities.isEmpty()) pickNewTarget(entities);
            else {
                child = parent;
                parent = caster;
            }
            
            lightningConnections.clear();
            Set<EntityLivingBase> childConnection = new HashSet<>();
            childConnection.add(child);
            lightningConnections.put(parent, childConnection);
            this.syncConnections(this);
            
            if (parent != null && parent != caster && !parent.isDead) {
                parent.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, caster), 10.0F);
                Potion paralysis = ObjectManager.getEffect("paralysis");
                if (paralysis != null) parent.addPotionEffect(new PotionEffect(paralysis, 100, 0, false, true));
            }
        }
    }
    
    private boolean shouldTarget(EntityLivingBase e, EntityLivingBase caster, EntityLivingBase parent) {
        if (e == parent || e == caster) return false;
        if (caster instanceof EntityPlayer) return !CombatAssistHandler.isEntityTamedByPlayer(e, (EntityPlayer) caster);
        return true;
    }
    
    private void pickNewTarget(List<EntityLivingBase> candidates) {
        if (candidates.isEmpty()) return;
        
        child = parent;
        int idx = rand.nextInt(candidates.size());
        parent = candidates.get(idx);
        affectedEntities.add(parent);
    }
    
    @Override
    public Map<EntityLivingBase, Set<EntityLivingBase>> getConnections() {
        return lightningConnections;
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