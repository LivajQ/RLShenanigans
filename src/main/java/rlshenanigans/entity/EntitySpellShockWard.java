package rlshenanigans.entity;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import rlshenanigans.handlers.CombatAssistHandler;

import javax.vecmath.Color4f;
import java.util.*;

public class EntitySpellShockWard extends EntitySpellBase implements ISpellLightning {
    private int lifetime;
    private final Map<EntityLivingBase, Set<EntityLivingBase>> lightningConnections = new HashMap<>();
    private static final Color4f COLOR = new Color4f(1.0f, 1.0f, 0.0f, 0.7f);
    
    public EntitySpellShockWard(World world) {
        this(world, null, 1200);
    }
    
    public EntitySpellShockWard(World world, EntityLivingBase caster, int lifetime) {
        super(world, caster, 0.0F, 0.0F, 0.0F, 0.0F);
        this.noClip = true;
        this.lifetime = lifetime;
        this.setSize(0.5F, 0.5F);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if (this.ticksExisted < 10) return;
        if (caster != null) this.setPosition(caster.posX, caster.posY + 0.5F * caster.height, caster.posZ);
        if (this.world.isRemote) return;
        
        if (lifetime-- <= 0) {
            this.setDead();
            return;
        }
        
        if (this.ticksExisted % 10 == 0) {
            List<EntityLivingBase> entities = this.getEntityWorld().getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    this.getEntityBoundingBox().grow(12),
                    e -> e.canEntityBeSeen(this) && shouldTarget(e, caster)
            );
            
            lightningConnections.clear();
            lightningConnections.put(caster, new HashSet<>(entities));
            this.syncConnections(this);
            
            lightningConnections.get(caster).forEach(e -> {
                e.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, caster), 10.0F);
                Potion paralysis = ObjectManager.getEffect("paralysis");
                if (paralysis != null) e.addPotionEffect(new PotionEffect(paralysis, 20, 0, false, true));
            });
        }
    }
    
    private boolean shouldTarget(EntityLivingBase e, EntityLivingBase caster) {
        if (e == caster) return false;
        if (caster instanceof EntityPlayer) return !CombatAssistHandler.isEntityTamedByPlayer(e, (EntityPlayer) caster);
        return true;
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