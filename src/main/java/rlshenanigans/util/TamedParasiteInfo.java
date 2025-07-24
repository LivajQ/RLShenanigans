package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class TamedParasiteInfo {
    public final UUID mobUUID;
    public final UUID ownerId;
    public final String name;
    public final Class<? extends EntityParasiteBase> mobClass;
    public final int skin;
    public final double maxHealth;
    public final double attackDamage;
    public final double armor;
    public final String strainId;
    
    public TamedParasiteInfo(EntityParasiteBase mob, EntityPlayer owner) {
        NBTTagCompound tag = new NBTTagCompound();
        mob.writeToNBT(tag);
        
        this.mobUUID = mob.getUniqueID();
        this.ownerId = owner.getUniqueID();
        this.name = mob.hasCustomName() ? mob.getCustomNameTag() : mob.getName();
        this.mobClass = mob.getClass().asSubclass(EntityParasiteBase.class);
        this.skin = mob.getSkin();
        this.strainId = "ggggg";  //bitch was crashing everything. gotta find a new way to display it later
        this.maxHealth = mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
        this.attackDamage = mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
        this.armor = mob.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue();
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
            return new TamedParasiteInfo(mobUUID, ownerId, name, strainId, raw.asSubclass(EntityParasiteBase.class), skin, health, damage, armor);
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
    }
    public TamedParasiteInfo(UUID mobUUID, UUID ownerId, String name, String strainId, Class<? extends EntityParasiteBase> mobClass, int skin, double maxHealth, double attackDamage, double armor) {
        this.mobUUID = mobUUID;
        this.ownerId = ownerId;
        this.name = name;
        this.mobClass = mobClass;
        this.strainId = strainId;
        this.skin = skin;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.armor = armor;
    }
}