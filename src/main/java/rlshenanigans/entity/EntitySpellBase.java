package rlshenanigans.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.Collections;
import java.util.UUID;

public abstract class EntitySpellBase extends Entity implements IEntityAdditionalSpawnData {
    public float red;
    public float green;
    public float blue;
    public float alpha;
    public EntityLivingBase caster;
    private UUID casterUUID;
    
    public EntitySpellBase(World world) {
        this(world, null, 1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public EntitySpellBase(World world, EntityLivingBase caster, float red, float green, float blue, float alpha) {
        super(world);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.caster = caster;
        this.casterUUID = this.caster != null ? this.caster.getUniqueID() : null;
        this.setSize(1.0F, 1.0F);
    }
    
    @Override
    protected void entityInit() {}
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if ((this.ticksExisted < 20 || this.ticksExisted % 20 == 0) && caster == null && this.casterUUID != null) {
            for (Entity e : world.loadedEntityList) {
                if (this.casterUUID.equals(e.getUniqueID()) && e instanceof EntityLivingBase) {
                    this.caster = (EntityLivingBase) e;
                    break;
                }
            }
        }
    }
    
    @Override
    public boolean hasNoGravity() {
        return true;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }
    
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }
    
    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return Collections.emptyList();
    }
    
    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {}
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setFloat("Red", red);
        compound.setFloat("Green", green);
        compound.setFloat("Blue", blue);
        compound.setFloat("Alpha", alpha);
        if (casterUUID != null) compound.setUniqueId("CasterUUID", casterUUID);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        red = compound.getFloat("Red");
        green = compound.getFloat("Green");
        blue = compound.getFloat("Blue");
        alpha = compound.getFloat("Alpha");
        if (compound.hasUniqueId("CasterUUID")) casterUUID = compound.getUniqueId("CasterUUID");
    }
    
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.alpha);
        
        if (this.casterUUID != null) {
            buffer.writeLong(casterUUID.getMostSignificantBits());
            buffer.writeLong(casterUUID.getLeastSignificantBits());
        }
        else {
            buffer.writeLong(-1);
            buffer.writeLong(-1);
        }
    }
    
    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.red = buffer.readFloat();
        this.green = buffer.readFloat();
        this.blue = buffer.readFloat();
        this.alpha = buffer.readFloat();
        long mostSignificant = buffer.readLong();
        long leastSignificant = buffer.readLong();
        if (mostSignificant != -1 && leastSignificant != -1) this.casterUUID = new UUID(mostSignificant, leastSignificant);
    }
    
    public boolean movingTexture() {
        return false;
    }
    
    public float textureScale() {
        return 1.0F;
    }
}