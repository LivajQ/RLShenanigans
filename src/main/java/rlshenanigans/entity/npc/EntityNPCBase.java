package rlshenanigans.entity.npc;

import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.items.ItemStackHandler;
import rlshenanigans.entity.ai.EntityAINPCAttackMelee;
import rlshenanigans.entity.ai.EntityAIShieldBlock;
import rlshenanigans.handlers.ForgeConfigHandler;
import rlshenanigans.util.WeaponRegistry;

import java.util.*;
import java.util.stream.Collectors;

public abstract class EntityNPCBase extends EntityCreature implements IEntityAdditionalSpawnData {
    public ResourceLocation skin;
    public String name;
    public WeaponRegistry.WeaponTypes preferredWeapon;
    public ResourceLocation offhandItem;
    public int offhandItemCount;
    public double baseReach = ForgeConfigHandler.npc.baseReach;
    public boolean potionThrower;
    protected float characterStrength;
    private final ItemStackHandler handStorage = new ItemStackHandler(1);
    
    public EntityNPCBase(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.setCanPickUpLoot(true);
        this.potionThrower = rand.nextFloat() < potionThrowerChance();
        this.createCharacter();
        this.setCustomNameTag(name);
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
    }
    
    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIMoveIndoors(this));
        this.tasks.addTask(2, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 0.65D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.applyEntityAI();
    }
    protected void applyEntityAI() {
        this.tasks.addTask(1, new EntityAINPCAttackMelee(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIShieldBlock(this));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(0.0D);
    }
    
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.updateArmSwingProgress();
        
        if (this.ticksExisted % potionCooldown() == 0 && this.potionThrower) potionTargeter();
    }
    
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        
        // --- Character strength ---
        EntityPlayer closestPlayer = world.getClosestPlayer(posX, posY, posZ, 128.0D, false);
        this.characterStrength = (float) (closestPlayer != null ? scaleCharacter(closestPlayer) : rand.nextFloat() * 300);
        this.characterStrength = MathHelper.clamp(this.characterStrength, 0.0F, 300.0F);
        
        // --- Build quality weights ---
        Map<WeaponRegistry.WeaponQualities, Integer> weights = new HashMap<>();
        final int maxRange = 50;
        for (WeaponRegistry.WeaponQualities quality : WeaponRegistry.WeaponQualities.values()) {
            int distance = Math.abs(quality.powerLevel - Math.round(this.characterStrength));
            int weight = Math.max(0, maxRange - distance);
            if (weight > 0) weights.put(quality, weight);
        }
        
        WeaponRegistry.WeaponQualities chosenQuality = pickWeighted(weights, rand)
                .orElse(WeaponRegistry.WeaponQualities.WOOD);
        
        ItemStack chosenWeapon = equipMainhand(chosenQuality);
        if (chosenWeapon != null) enchantWeapon(chosenWeapon);
        scaleStatistics();
        this.experienceValue = getExperienceValue();
        
        if (this.offhandItem != null) {
            Item item = ForgeRegistries.ITEMS.getValue(this.offhandItem);
            if (item != null) this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(item, offhandItemCount));
        }
        
        return livingdata;
    }
    
    private Optional<WeaponRegistry.WeaponQualities> pickWeighted(Map<WeaponRegistry.WeaponQualities, Integer> weights, Random rand) {
        int total = weights.values().stream().mapToInt(Integer::intValue).sum();
        if (total <= 0) return Optional.empty();
        
        int roll = rand.nextInt(total);
        
        int cumulative = 0;
        for (Map.Entry<WeaponRegistry.WeaponQualities, Integer> e : weights.entrySet()) {
            cumulative += e.getValue();
            if (roll < cumulative) return Optional.of(e.getKey());
        }
        return Optional.empty();
    }
    
    private ItemStack equipMainhand(WeaponRegistry.WeaponQualities quality) {
        List<WeaponRegistry> candidates;
        
        if (this.preferredWeapon != null) {
            candidates = WeaponRegistry.WEAPONS.stream()
                    .filter(w -> w.type == this.preferredWeapon && w.quality == quality)
                    .collect(Collectors.toList());
            
            if (candidates.isEmpty() && quality == WeaponRegistry.WeaponQualities.SPECIAL) {
                candidates = WeaponRegistry.WEAPONS.stream()
                        .filter(w -> w.quality == WeaponRegistry.WeaponQualities.SPECIAL)
                        .collect(Collectors.toList());
            }
            
            if (candidates.isEmpty()) {
                candidates = WeaponRegistry.WEAPONS.stream()
                        .filter(w -> w.type == this.preferredWeapon)
                        .collect(Collectors.toList());
            }
        } else {
            candidates = WeaponRegistry.WEAPONS.stream()
                    .filter(w -> w.quality == quality)
                    .collect(Collectors.toList());
        }
        
        if (candidates.isEmpty()) candidates = new ArrayList<>(WeaponRegistry.WEAPONS);
        
        if (!candidates.isEmpty()) {
            WeaponRegistry selected = candidates.get(rand.nextInt(candidates.size()));
            Item item = ForgeRegistries.ITEMS.getValue(selected.id);
            if (item != null) {
                ItemStack chosenWeapon = new ItemStack(item);
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, chosenWeapon);
                return chosenWeapon;
            }
        }
        return null;
    }
    
    protected void enchantWeapon(ItemStack weapon) {
        List<Enchantment> applicableEnchants = new ArrayList<>();
        final String[] bannedEnchants = bannedEnchants();
        
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
            if (enchantment.canApply(weapon) || enchantment.canApplyAtEnchantingTable(weapon)) {
                ResourceLocation id = enchantment.getRegistryName();
                if (id == null) return;
                String enchantmentName = id.getPath();

                boolean banned = false;
                for (String bannedEnchant : bannedEnchants) {
                    if (enchantmentName.contains(bannedEnchant)) {
                        banned = true;
                        break;
                    }
                }
                if (!banned) applicableEnchants.add(enchantment);
            }
        }
        
        int enchantmentAmount = (int) Math.floor((this.characterStrength / 20.0F) + getExtraEnchantmentCount());
        int enchantability = (int) Math.round(this.characterStrength / 2.5F * getEnchantabilityMultiplier());
        
        if (enchantmentAmount <= 0) return;
        
        Collections.shuffle(applicableEnchants);
        Map<Enchantment, Integer> finalEnchants = new LinkedHashMap<>();
        
        for (Enchantment enchantment : applicableEnchants) {
            if (finalEnchants.size() >= enchantmentAmount) break;
            
            boolean conflicts = false;
            for (Enchantment applied : finalEnchants.keySet()) {
                if (!enchantment.isCompatibleWith(applied)) {
                    conflicts = true;
                    break;
                }
            }
            if (conflicts) continue;
            
            int maxLevel = enchantment.getMaxLevel();
            int level = 0;
            for (int l = maxLevel; l >= 1; l--) {
                if (enchantment.getMinEnchantability(l) <= enchantability) {
                    level = l;
                    break;
                }
            }
            
            if (level > 0) {
                finalEnchants.put(enchantment, level);
            }
        }
        EnchantmentHelper.setEnchantments(finalEnchants, weapon);
    }

    protected String[] bannedEnchants() {
        return new String[]{"possession", "vanishing", "decay", "binding"};
    }
    
    protected void scaleStatistics() {
        double globalHealthMultiplier = ForgeConfigHandler.npc.globalHealthMultiplier;
        double globalDamageMultiplier = ForgeConfigHandler.npc.globalDamageMultiplier;
        double globalArmorMultiplier = ForgeConfigHandler.npc.globalArmorMultiplier;
        double globalArmorToughnessMultiplier = ForgeConfigHandler.npc.globalArmorToughnessMultiplier;
        
        double healthBonus = (this.characterStrength / 2) * getCharacterStatMultiplier() * statisticRandomFactor() * globalHealthMultiplier;
        double damageBonus = (this.characterStrength / 6) * getCharacterStatMultiplier() * statisticRandomFactor() * globalDamageMultiplier;
        double armorBonus = (this.characterStrength / 10) * getCharacterStatMultiplier() * statisticRandomFactor() * globalArmorMultiplier;
        double armorToughnessBonus = (this.characterStrength / 10) * getCharacterStatMultiplier() * statisticRandomFactor() * globalArmorToughnessMultiplier;
        
        IAttributeInstance health = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        IAttributeInstance attackDamage = this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
        IAttributeInstance armor = this.getEntityAttribute(SharedMonsterAttributes.ARMOR);
        IAttributeInstance toughness = this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
        
        health.setBaseValue(health.getBaseValue() + healthBonus);
        attackDamage.setBaseValue(attackDamage.getBaseValue() + damageBonus);
        armor.setBaseValue(armor.getBaseValue() + armorBonus);
        toughness.setBaseValue(toughness.getBaseValue() + armorToughnessBonus);
        
        this.setHealth(this.getMaxHealth());
    }
    
    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET,
        }) {
            ItemStack stack = this.getItemStackFromSlot(slot);
            if (!stack.isEmpty()) this.entityDropItem(stack.copy(), 0.5F);
        }
        
        ItemStack mainHand = getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        
        if (!mainHand.isEmpty() && !canDropThisWeapon(mainHand)) {
            NBTTagCompound enchantments = mainHand.getTagCompound();
            equipMainhand(replacedWeaponQuality());
            
            ItemStack newMainHand = getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            if (enchantments != null) newMainHand.setTagCompound(enchantments.copy());
        }
    }
    
    protected boolean canDropThisWeapon(ItemStack weapon) {
        Item weaponItem = weapon.getItem();
        ResourceLocation registryName = weaponItem.getRegistryName();
        return registryName == null || !registryName.toString().contains("srparasites");
    }
    
    protected WeaponRegistry.WeaponQualities replacedWeaponQuality() {
        return WeaponRegistry.WeaponQualities.DRAGONBONEINF;
    }
    
    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        Item item = itemstack.getItem();
        
        if (item instanceof ItemArmor) {
            ItemArmor newArmor = (ItemArmor) item;
            EntityEquipmentSlot slot = newArmor.armorType;
            ItemStack currentArmor = this.getItemStackFromSlot(slot);
            
            if (currentArmor.isEmpty()) {
                this.setItemStackToSlot(slot, itemstack.copy());
                itemEntity.setDead();
                return;
            }
            
            boolean newEnchanted = itemstack.isItemEnchanted();
            boolean currentEnchanted = currentArmor.isItemEnchanted();
            
            if (newEnchanted && !currentEnchanted) {
                this.entityDropItem(currentArmor.copy(), 0.0F);
                this.setItemStackToSlot(slot, itemstack.copy());
                itemEntity.setDead();
                return;
            }
            
            if (newEnchanted == currentEnchanted) {
                int newArmorValue = ((ItemArmor) itemstack.getItem()).damageReduceAmount;
                int currentArmorValue = ((ItemArmor) currentArmor.getItem()).damageReduceAmount;
                
                if (newArmorValue > currentArmorValue) {
                    this.entityDropItem(currentArmor.copy(), 0.0F);
                    this.setItemStackToSlot(slot, itemstack.copy());
                    itemEntity.setDead();
                    return;
                }
            }
        }
        
        //
    }
    
    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isInWater()) super.travel(strafe * 3.0F, vertical, forward * 3.0F);
        else super.travel(strafe, vertical, forward);
    }
    
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (hand == EnumHand.OFF_HAND && isWillingToTalk()) {
        
        }
        return false;
    }
    
    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.isBlocking()) return false;
        this.swingArm(EnumHand.MAIN_HAND);
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;
        
        if (entityIn instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }
        
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
        
        if (flag) {
            if (i > 0 && entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase)entityIn).knockBack(this, (float)i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
            
            int j = EnchantmentHelper.getFireAspectModifier(this);
            
            if (j > 0) {
                entityIn.setFire(j * 4);
            }
            
            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;
                ItemStack itemstack = this.getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
                
                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack1.getItem().isShield(itemstack1, entityplayer)) {
                    float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
                    
                    if (this.rand.nextFloat() < f1) {
                        entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                        this.world.setEntityState(entityplayer, (byte)30);
                    }
                }
            }
            
            this.applyEnchantments(this, entityIn);
        }
        
        return flag;
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isBlocking()) return false;
        return super.attackEntityFrom(source, amount);
    }
    
    @Override
    protected float applyArmorCalculations(DamageSource source, float damage) {
        this.damageArmor(damage);
        damage = CombatRules.getDamageAfterAbsorb(damage, (float)this.getTotalArmorValue(), (float)this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }
    
    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }
    
    public boolean isBlocking() {
        return this.isHandActive() &&
                this.getActiveHand() == EnumHand.OFF_HAND &&
                this.getHeldItemOffhand().getItem() instanceof ItemShield;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PLAYER_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }
    
    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {}
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("NPCName", this.name);
        compound.setString("NPCSkin", this.skin.toString());
        if (this.preferredWeapon != null) compound.setString("PreferredWeapon", this.preferredWeapon.name());
        if (this.offhandItem != null) compound.setString("OffhandItem", this.offhandItem.toString());
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.name = compound.getString("NPCName");
        this.skin = new ResourceLocation(compound.getString("NPCSkin"));
        if (compound.hasKey("PreferredWeapon")) {
            try {this.preferredWeapon = WeaponRegistry.WeaponTypes.valueOf(compound.getString("PreferredWeapon"));
            } catch (IllegalArgumentException e) {this.preferredWeapon = null;}
        }
        if (compound.hasKey("OffhandItem")) this.offhandItem = new ResourceLocation(compound.getString("OffhandItem"));
    }
    
    @Override
    public void writeSpawnData(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.name);
        ByteBufUtils.writeUTF8String(buffer, this.skin.toString());
        ByteBufUtils.writeUTF8String(buffer, this.preferredWeapon != null ? this.preferredWeapon.name() : "");
        ByteBufUtils.writeUTF8String(buffer, this.offhandItem != null ? this.offhandItem.toString() : "");
    }
    
    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.name = ByteBufUtils.readUTF8String(buffer);
        this.skin = new ResourceLocation(ByteBufUtils.readUTF8String(buffer));
        String weaponStr = ByteBufUtils.readUTF8String(buffer);
        if (!weaponStr.isEmpty()) {
            try {this.preferredWeapon = WeaponRegistry.WeaponTypes.valueOf(weaponStr);
            } catch (IllegalArgumentException e) {this.preferredWeapon = null;}
        }
        String offhandStr = ByteBufUtils.readUTF8String(buffer);
        if (!offhandStr.isEmpty()) this.offhandItem = new ResourceLocation(offhandStr);
    }
    
    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        //speech
    }
    
    public ResourceLocation getSkin() {
        return this.skin;
    }
    
    protected void createCharacter() {
        if (this.world.isRemote) return;
        this.name = "Noname";
        this.skin = new ResourceLocation("minecraft", "textures/entity/steve.png");
        this.preferredWeapon = null;
        this.offhandItem = null;
        this.offhandItemCount = 0;
    }
    
    protected double scaleCharacter(EntityPlayer closestPlayer) {
        float gameStage = 0.0F;
        gameStage += Math.min(world.getTotalWorldTime() / 48000.0F, 100.0F);
        gameStage += closestPlayer.getMaxHealth() - 20;
        
        int weaponTotalMaxEnchantability = 0;
        
        ItemStack bestWeapon = getBestWeapon(closestPlayer);
        if (bestWeapon.getItem() instanceof ItemSword) {
            gameStage += ((ItemSword) bestWeapon.getItem()).getAttackDamage() * 10;
            
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(bestWeapon);
            
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchant = entry.getKey();
                int level = entry.getValue();
                weaponTotalMaxEnchantability += enchant.getMaxEnchantability(level);
            }
            gameStage += weaponTotalMaxEnchantability / 20.0F;
        }

        int armorPoints = 0;
        int armorTotalMaxEnchantability = 0;
        
        for (ItemStack armorStack : closestPlayer.inventory.armorInventory) {
            if (!armorStack.isEmpty() && armorStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) armorStack.getItem();
                
                armorPoints += armor.damageReduceAmount;
                
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(armorStack);
                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    Enchantment enchant = entry.getKey();
                    int level = entry.getValue();
                    armorTotalMaxEnchantability += enchant.getMaxEnchantability(level);
                }
            }
        }
        
        gameStage += armorPoints / 3.0F;
        gameStage += armorTotalMaxEnchantability / 20.0F;
        
        return gameStage * getGameStageMultiplier();
    }
    
    protected void potionTargeter() {
        EntityLivingBase target = this.getAttackTarget();
        
        if (target != null && this.canEntityBeSeen(target)) throwPotion(target, false);
        else if (this.getHealth() / this.getMaxHealth() < 0.75F) throwPotion(this, true, PotionTypes.REGENERATION);
    }
    
    protected void throwPotion(EntityLivingBase target, boolean positive) {
        throwPotion(target, positive, null);
    }
    
    //stolen witch code
    protected void throwPotion(EntityLivingBase target, boolean positive, PotionType potion) {
        double d0 = target.posY + (double) target.getEyeHeight() - 1.1D;
        double d1 = target.posX + target.motionX - this.posX;
        double d2 = d0 - this.posY;
        double d3 = target.posZ + target.motionZ - this.posZ;
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
        
        PotionType potionType;
        
        if (potion != null) potionType = potion;
        
        else {
            List<PotionType> potions = positive ? getPositivePotions() : getNegativePotions();
            if (potions.isEmpty()) return;
            potionType = potions.get(rand.nextInt(potions.size()));
        }
        
        EntityPotion entitypotion = new EntityPotion(this.world, this, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potionType));
        entitypotion.rotationPitch -= -20.0F;
        if (target == this) entitypotion.shoot(0.0F, -0.2F, 0.0F, 0.75F, 8.0F);
        else entitypotion.shoot(d1, d2 + (double) (f * 0.2F), d3, 0.75F, 8.0F);
        this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
        this.world.spawnEntity(entitypotion);
    }
    
    protected List<PotionType> getNegativePotions() {
        return Arrays.asList(
                PotionTypes.POISON,
                PotionTypes.HARMING,
                PotionTypes.SLOWNESS,
                PotionTypes.WEAKNESS
        );
    }
    
    protected List<PotionType> getPositivePotions() {
        return Arrays.asList(
                PotionTypes.HEALING,
                PotionTypes.REGENERATION,
                PotionTypes.STRENGTH,
                PotionTypes.SWIFTNESS
        );
    }
    
    protected double getGameStageMultiplier() {
        return ForgeConfigHandler.npc.gameStageMultiplier;
    }
    
    protected double getCharacterStatMultiplier() {
        return 1.0F;
    }
    
    protected double statisticRandomFactor() {
        return 0.8D + (rand.nextDouble() * 0.4D);
    }
    
    protected int getExtraEnchantmentCount() {
        return 0;
    }
    
    protected double getEnchantabilityMultiplier() {
        return 1.0F;
    }
    
    protected boolean isWillingToTalk() {
        return false;
    }
    
    protected int getExperienceValue() {
        return 10 + Math.round(this.characterStrength / 5);
    }
    
    protected int potionCooldown() {
        return 300;
    }
    
    protected float potionThrowerChance() {
        return 0.35F;
    }
    
    protected ItemStack getBestWeapon(EntityPlayer player) {
        ItemStack bestWeapon = ItemStack.EMPTY;
        int maxEnchantCount = -1;
        float maxDamage = -1F;
        
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem() instanceof ItemSword) {
                int enchantCount = stack.getEnchantmentTagList().tagCount();
                float baseDamage = ((ItemSword) stack.getItem()).getAttackDamage();
                
                if (enchantCount > maxEnchantCount) {
                    bestWeapon = stack;
                    maxEnchantCount = enchantCount;
                    maxDamage = baseDamage;
                }
                else if (enchantCount == 0 && maxEnchantCount == 0 && baseDamage > maxDamage) {
                    bestWeapon = stack;
                    maxDamage = baseDamage;
                }
            }
        }
        
        return bestWeapon;
    }
    
    public float getRandomOffhandChance() {
        return 0.25F;
    }
}