package rlshenanigans.entity.npc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.ai.*;

import java.util.ArrayList;
import java.util.List;

public class EntityNPCJohnMinecraft extends EntityNPCBase{
    private final BossInfoServer bossInfo = new BossInfoServer(new TextComponentString(this.name), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS);
    
    public EntityNPCJohnMinecraft(World world) {
        super(world);
        this.enablePersistence();
    }
    
    @Override
    protected void applyEntityAI() {
        this.tasks.addTask(1, new EntityAINPCAttackMelee(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIShieldBlock(this, false));
        this.tasks.addTask(3, new EntityAIThrowPearl(this, -0.2F, 20));
        this.tasks.addTask(4, new EntityAIMineToTarget(this, 2137, 5, 10));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityNPCSummon.class, true));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityNPCGeneric.class, true));
        this.targetTasks.addTask(6, new EntityAISelfDefense<>(this, EntityLivingBase.class));
        this.targetTasks.addTask(7, new EntityAIHuntUntamed<>(this, EntityLivingBase.class, 10, true, false, true, target ->
                target instanceof IMob
        ));
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation("potioncore", "cure"));
        if (potion != null) this.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE, 0, false, true));
        return livingdata;
    }
    
    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        bossInfo.addPlayer(player);
    }
    
    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo.removePlayer(player);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }
    
    @Override
    protected void createCharacter() {
        if (this.world.isRemote) return;
        this.name = "§6§lJohn Minecraft§r";
        this.skin = new ResourceLocation(RLShenanigans.MODID, "textures/entity/npc/npc_johnminecraft.png");
        this.preferredWeapon = null;
        this.offhandItem = new ResourceLocation("bountifulbaubles", "shieldankh");
        this.offhandItemCount = 1;
    }
    
    @Override
    public List<PotionType> getNegativePotions() {
        String[] effectIds = {
                "potioncore:broken_magic_shield",
                "potioncore:perplexity",
                "potioncore:rust",
                "potioncore:weight",
                "potioncore:spin",
                "lycanitesmobs:instability",
                "lycanitesmobs:paralysis"
        };
        
        List<PotionType> types = new ArrayList<>();
        for (String id : effectIds) {
            PotionType type = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(id));
            if (type != null) types.add(type);
        }
        
        return types;
    }
    
    @Override
    protected double getCharacterStatMultiplier() {
        return 6.0F;
    }
    
    @Override
    protected int getExtraEnchantmentCount() {
        return 5;
    }
    
    @Override
    protected double getEnchantabilityMultiplier() {
        return 3.0F;
    }
    
    @Override
    protected int getExperienceValue() {
        return super.getExperienceValue() * 50;
    }
    
    @Override
    protected int potionCooldown() {
        return 40;
    }
    
    @Override
    protected float potionThrowerChance() {
        return 1.0F;
    }
}