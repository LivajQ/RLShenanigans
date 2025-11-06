package rlshenanigans.entity.npc;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.client.particle.ParticleNPCPhantomSummon;
import rlshenanigans.handlers.RLSSoundHandler;

public abstract class EntityNPCPhantom extends EntityNPCBase {
    protected int phantomFadeTime;
    protected int phantomFadeTimeMax;
    protected boolean isDespawning = false;
    protected boolean firstSpawn = true;
    
    public EntityNPCPhantom(World world) {
        this(world, 100);
    }
    
    public EntityNPCPhantom (World world, int phantomFadeTime){
        super(world);
        this.phantomFadeTime = phantomFadeTime;
        this.phantomFadeTimeMax = this.phantomFadeTime;
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (doesFadeFreezePhantom()) {
            if (phantomFadeTime > 1) this.setNoAI(true);
            else if (phantomFadeTime == 1) this.setNoAI(false);
        }
        if (phantomFadeTime > 0) {
            phantomFadeTime--;
            
            if (world.isRemote) spawnFadeParticles();
        }
        
        else if (firstSpawn) firstSpawn = false;
        
        if (phantomFadeTime == 0 && isDespawning) this.setDead();
        
        this.setAir(300);
        
        Potion cothEffect = ForgeRegistries.POTIONS.getValue(new ResourceLocation("srparasites", "coth"));
        if (cothEffect != null && this.isPotionActive(cothEffect)) {
            this.removePotionEffect(cothEffect);
        }
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        world.playSound(null, this.posX, this.posY, this.posZ, getPhantomSpawnSound(), SoundCategory.NEUTRAL, 4.0F, 1.0F);
        return  livingdata;
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (doesFadeFreezePhantom() && phantomFadeTime > 0) return false;
        return super.attackEntityFrom(source, amount);
    }
    
    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {}
    
    @Override
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender() {return 15728880;}
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("PhantomFadeTime", this.phantomFadeTime);
        compound.setInteger("PhantomFadeTimeMax", this.phantomFadeTimeMax);
        compound.setBoolean("IsDespawning", this.isDespawning);
        compound.setBoolean("FirstSpawn", this.firstSpawn);
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.phantomFadeTime = compound.getInteger("PhantomFadeTime");
        this.phantomFadeTimeMax = compound.getInteger("PhantomFadeTimeMax");
        this.isDespawning = compound.getBoolean("IsDespawning");
        this.firstSpawn = compound.getBoolean("FirstSpawn");
    }
    
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.phantomFadeTime);
        buffer.writeInt(this.phantomFadeTimeMax);
        buffer.writeBoolean(this.isDespawning);
        buffer.writeBoolean(this.firstSpawn);
    }
    
    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        this.phantomFadeTime = buffer.readInt();
        this.phantomFadeTimeMax = buffer.readInt();
        this.isDespawning = buffer.readBoolean();
        this.firstSpawn = buffer.readBoolean();
    }
    
    @Override
    public float getRandomOffhandChance() {
        return 0.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public abstract Vec3d getPhantomGlowColor();
    
    protected boolean doesFadeFreezePhantom() {
        return true;
    }
    
    protected SoundEvent getPhantomSpawnSound() {
        return RLSSoundHandler.PHANTOM_SPAWN;
    }
    
    public boolean spawnInRadius(World world, BlockPos center, int minRadius, int maxRadius, boolean positionOnly) {
        int attempts = 30;
        
        for (int i = 0; i < attempts; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = minRadius + rand.nextDouble() * (maxRadius - minRadius);
            int x = center.getX() + (int)(Math.cos(angle) * distance);
            int z = center.getZ() + (int)(Math.sin(angle) * distance);
            
            for (int y = center.getY() + 10; y >= center.getY() - 10; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockPos above = pos.up();
                IBlockState state = world.getBlockState(pos);
                
                if (state.getBlock().isTopSolid(state)) {
                    if (world.isAirBlock(above) && world.isAirBlock(above.up())) {
                        this.setPosition(x + 0.5, y + 1, z + 0.5);
                        if (!positionOnly) world.spawnEntity(this);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public float getFadeLevel() {
        float base = (float) (phantomFadeTimeMax - phantomFadeTime) / phantomFadeTimeMax;
        return isDespawning ? 1.0F - base : base;
    }
    
    public boolean getIsDespawning() {
        return isDespawning;
    }
    
    public void setPhantomFadeTime(int time) {
        this.phantomFadeTime = time;
    }
    
    public int getPhantomFadeTime() {
        return this.phantomFadeTime;
    }
    
    @SideOnly(Side.CLIENT)
    protected void spawnFadeParticles() {
        double x = posX + (rand.nextDouble() - 0.5) * 0.5;
        double y = posY;
        double z = posZ + (rand.nextDouble() - 0.5) * 0.5;
        
        double motionX = (rand.nextDouble() - 0.5) * 1.5;
        double motionY = 0.5 + rand.nextDouble() * 0.5;
        double motionZ = (rand.nextDouble() - 0.5) * 1.5;
        
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new ParticleNPCPhantomSummon(world, this, x, y, z, motionX, motionY, motionZ, 0)
        );
    }
}