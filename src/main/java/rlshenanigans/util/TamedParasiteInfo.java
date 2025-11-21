package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.UUID;

public class TamedParasiteInfo {
    public final UUID mobUUID;
    public final UUID ownerId;
    public String name;
    public final Class<? extends EntityParasiteBase> mobClass;
    public final int skin;
    public final double maxHealth;
    public final double attackDamage;
    public final double armor;
    public final String strainId;
    public final float baseWidth;
    public final float baseHeight;
    public final float sizeMultiplier;
    public final long lastDropTime;
    
    public TamedParasiteInfo(EntityParasiteBase mob, EntityPlayer owner, boolean includeAttributes) {
        NBTTagCompound tag = new NBTTagCompound();
        mob.writeToNBT(tag);
        this.mobUUID = mob.getUniqueID();
        this.ownerId = owner.getUniqueID();
        this.name = mob.hasCustomName() ? mob.getCustomNameTag() : mob.getName();
        this.mobClass = mob.getClass().asSubclass(EntityParasiteBase.class);
        this.skin = mob.getSkin();
        this.strainId = getBaseEntityName(mob);
        
        this.maxHealth = includeAttributes ? mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue()
                : mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
        this.attackDamage = includeAttributes ? mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()
                : mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
        this.armor = includeAttributes ? mob.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue()
                : mob.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue();
        
        this.baseWidth = mob.getEntityData().getFloat("BaseWidth");
        this.baseHeight = mob.getEntityData().getFloat("BaseHeight");
        this.sizeMultiplier = mob.getEntityData().getFloat("SizeMultiplier");
        this.lastDropTime = mob.getEntityData().getLong("LastDropTime");
        
        if (includeAttributes) {
            mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.attackDamage);
            mob.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(this.armor);
            mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.maxHealth);
            mob.setHealth((float)this.maxHealth);
        }
    }
    
    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUniqueId("MobUUID", mobUUID);
        tag.setUniqueId("OwnerUUID", ownerId);
        tag.setString("Name", name);
        tag.setString("Class", mobClass.getName());
        tag.setInteger("Skin", skin);
        tag.setDouble("MaxHealth", maxHealth);
        tag.setDouble("AttackDamage", attackDamage);
        tag.setDouble("Armor", armor);
        tag.setString("StrainID", strainId);
        tag.setFloat("BaseWidth", baseWidth);
        tag.setFloat("BaseHeight", baseHeight);
        tag.setFloat("SizeMultiplier", sizeMultiplier);
        tag.setLong("LastDropTime", lastDropTime);
        return tag;
    }
    
    public static TamedParasiteInfo fromNBT(NBTTagCompound tag) {
        try {
            UUID mobUUID = tag.getUniqueId("MobUUID");
            UUID ownerId = tag.getUniqueId("OwnerUUID");
            String name = tag.getString("Name");
            String strainId = tag.getString("StrainID");
            Class<?> raw = Class.forName(tag.getString("Class"));
            int skin = tag.getInteger("Skin");
            double health = tag.getDouble("MaxHealth");
            double damage = tag.getDouble("AttackDamage");
            double armor = tag.getDouble("Armor");
            float baseWidth = tag.getFloat("BaseWidth");
            float baseHeight = tag.getFloat("BaseHeight");
            float sizeMultiplier = tag.getFloat("SizeMultiplier");
            long lastDropTime = tag.getLong("LastDropTime");
            return new TamedParasiteInfo(mobUUID, ownerId, name, strainId, raw.asSubclass(EntityParasiteBase.class), skin, health, damage, armor, baseWidth, baseHeight, sizeMultiplier, lastDropTime);
        } catch (Exception e) {
            System.err.println("[TamedParasiteInfo] Load failed: " + e.getMessage());
            return null;
        }
    }
    
    public TamedParasiteInfo(UUID mobUUID, String name, String strainId, int skin) {
        this.mobUUID = mobUUID;
        this.ownerId = null;
        this.name = name;
        this.mobClass = EntityParasiteBase.class;
        this.strainId = strainId;
        this.skin = skin;
        this.maxHealth = 0;
        this.attackDamage = 0;
        this.armor = 0;
        this.baseWidth = 0;
        this.baseHeight = 0;
        this.sizeMultiplier = 0;
        this.lastDropTime = 0;
    }
    public TamedParasiteInfo(UUID mobUUID, UUID ownerId, String name, String strainId, Class<? extends EntityParasiteBase> mobClass, int skin,
                             double maxHealth, double attackDamage, double armor, float baseWidth, float baseHeight, float sizeMultiplier, long lastDropTime) {
        this.mobUUID = mobUUID;
        this.ownerId = ownerId;
        this.name = name;
        this.mobClass = mobClass;
        this.strainId = strainId;
        this.skin = skin;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.armor = armor;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        this.sizeMultiplier = sizeMultiplier;
        this.lastDropTime = lastDropTime;
    }
    
    public void setName(String newName) {
        this.name = newName;
    }
    
    private static String getBaseEntityName(EntityLivingBase mob) {
        ResourceLocation key = EntityList.getKey(mob);
        if (key != null) {
            String translationKey = "entity." + key.getNamespace() + "." + key.getPath() + ".name";
            ITextComponent comp = new TextComponentTranslation(translationKey);
            return comp.getUnformattedText();
        } else {
            String simple = mob.getClass().getSimpleName();
            return simple.replaceAll("Entity", "").replaceAll("([a-z])([A-Z])", "$1 $2").trim();
        }
    }
}