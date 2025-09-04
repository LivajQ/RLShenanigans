package rlshenanigans.entity.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.gui.PaintingResizeMenu;
import rlshenanigans.handlers.RLSEntityHandler;
import rlshenanigans.item.ItemPaintingSpawner;

public class EntityPaintingTemplate extends Entity implements IEntityAdditionalSpawnData {
    
    private EnumFacing facing = EnumFacing.NORTH;
    private String texture;
    private int frames = 1;
    private int currentFrame = 1;
    private String suffix = "";
    private int width = 1;
    private int height = 1;
    
    public EntityPaintingTemplate(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
    }
    
    public EntityPaintingTemplate(World world, double x, double y, double z, String texture, int frames, String suffix, EnumFacing facing) {
        this(world);
        this.setPosition(x, y, z);
        this.texture = texture;
        this.frames = frames;
        this.suffix = suffix;
        this.facing = facing;
    }
    
    @Override
    protected void entityInit() {}
    
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        texture = compound.getString("Texture");
        frames = compound.getInteger("Frames");
        currentFrame = compound.getInteger("CurrentFrame");
        suffix = compound.getString("Suffix");
        width = compound.getInteger("Width");
        height = compound.getInteger("Height");
        facing = EnumFacing.byIndex(compound.getInteger("Facing"));
    }
    
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setString("Texture", texture);
        compound.setInteger("Frames", frames);
        compound.setInteger("CurrentFrame", currentFrame);
        compound.setString("Suffix", suffix);
        compound.setInteger("Width", width);
        compound.setInteger("Height", height);
        compound.setInteger("Facing", facing.getIndex());
    }
    
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(width);
        buffer.writeInt(height);
        buffer.writeInt(facing.getIndex());
        buffer.writeInt(frames);
        buffer.writeInt(currentFrame);
        ByteBufUtils.writeUTF8String(buffer, texture);
        ByteBufUtils.writeUTF8String(buffer, suffix);
    }
    
    @Override
    public void readSpawnData(ByteBuf buffer) {
        width = buffer.readInt();
        height = buffer.readInt();
        facing = EnumFacing.byIndex(buffer.readInt());
        frames = buffer.readInt();
        currentFrame = buffer.readInt();
        texture = ByteBufUtils.readUTF8String(buffer);
        suffix = ByteBufUtils.readUTF8String(buffer);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (player.world.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new PaintingResizeMenu(this));
        }
        return true;
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public AxisAlignedBB getEntityBoundingBox() {
        switch (facing) {
            case NORTH:
                return new AxisAlignedBB(
                        posX + 0.5F,
                        posY,
                        posZ + 0.4F,
                        posX + 0.5F - width,
                        posY + height,
                        posZ + 0.5F
                );
            case SOUTH:
                return new AxisAlignedBB(
                        posX - 0.5F,
                        posY,
                        posZ - 0.4F,
                        posX - 0.5F + width,
                        posY + height,
                        posZ - 0.5F
                );
            case WEST:
                return new AxisAlignedBB(
                        posX + 0.4F,
                        posY,
                        posZ - 0.5F,
                        posX + 0.5F,
                        posY + height,
                        posZ - 0.5F + width
                );
            case EAST:
                return new AxisAlignedBB(
                        posX - 0.4F,
                        posY,
                        posZ + 0.5F,
                        posX - 0.5F,
                        posY + height,
                        posZ + 0.5F - width
                );
            default:
                return new AxisAlignedBB(
                        posX - 0.5F,
                        posY,
                        posZ - 0.5F,
                        posX - 0.5F + width,
                        posY + height,
                        posZ - 0.4F
                );
        }
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getEntityBoundingBox();
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (frames > 1) {
            if (currentFrame >= frames) currentFrame = 1;
            else currentFrame++;
        }
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isDead || this.world.isRemote) return false;
        
        this.setDead();
        
        world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        
        if (source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            if (player.capabilities.isCreativeMode) return true;
        }
        
        ItemPaintingSpawner item = RLSEntityHandler.PAINTING_ITEMS.get(suffix);
        if (item != null) {
            ItemStack drop = new ItemStack(item);
            this.entityDropItem(drop, 0.1F);
        }
        
        return true;
    }
    
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return false;
    }
    
    public ResourceLocation getCurrentTexture() {
        return frames == 1
                ? new ResourceLocation("rlshenanigans", texture + ".png")
                : new ResourceLocation("rlshenanigans", texture + "_" + currentFrame + ".png");
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public EnumFacing getFacing() {
        return this.facing;
    }
    
    public static EnumFacing getFacingFromPlayer(EntityPlayer player) {
        int yaw = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        switch (yaw) {
            case 0: return EnumFacing.NORTH;
            case 1: return EnumFacing.EAST;
            case 2: return EnumFacing.SOUTH;
            case 3: return EnumFacing.WEST;
        }
        return EnumFacing.NORTH;
    }
}