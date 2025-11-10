package rlshenanigans.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.Collections;

public abstract class EntitySpellBase extends EntityLivingBase implements IEntityAdditionalSpawnData {
    public float red;
    public float green;
    public float blue;
    public float alpha;
    
    public EntitySpellBase(World world) {
        this(world, 1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public EntitySpellBase(World world, float red, float green, float blue, float alpha) {
        super(world);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.setHealth(2137);
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
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {}
    
    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }
    
    @Override
    public AxisAlignedBB getEntityBoundingBox() {
        float halfHeight = this.height / 2.0F;
        return new AxisAlignedBB(
                this.posX - this.width / 2.0F,
                this.posY - halfHeight,
                this.posZ - this.width / 2.0F,
                this.posX + this.width / 2.0F,
                this.posY + halfHeight,
                this.posZ + this.width / 2.0F
        );
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("Red", red);
        compound.setFloat("Green", green);
        compound.setFloat("Blue", blue);
        compound.setFloat("Alpha", alpha);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        red = compound.getFloat("Red");
        green = compound.getFloat("Green");
        blue = compound.getFloat("Blue");
        alpha = compound.getFloat("Alpha");
    }
    
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(this.red);
        buffer.writeFloat(this.green);
        buffer.writeFloat(this.blue);
        buffer.writeFloat(this.alpha);
    }
    
    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.red = buffer.readFloat();
        this.green = buffer.readFloat();
        this.blue = buffer.readFloat();
        this.alpha = buffer.readFloat();
    }
    
    public boolean movingTexture() {
        return false;
    }
    
    public float textureScale() {
        return 1.0F;
    }
}