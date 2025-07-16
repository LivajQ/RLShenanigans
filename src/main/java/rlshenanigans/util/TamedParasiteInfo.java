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
    public final float maxHealth;
    public final float attackDamage;
    public final String strainId;
    
    public TamedParasiteInfo(EntityParasiteBase mob, EntityPlayer owner) {
        this.mobUUID = mob.getUniqueID();
        this.ownerId = owner.getUniqueID();
        this.name = mob.hasCustomName() ? mob.getCustomNameTag() : mob.getName();
        this.mobClass = mob.getClass().asSubclass(EntityParasiteBase.class);
        this.skin = mob.getSkin();
        this.maxHealth = (float) mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
        this.attackDamage = (float) mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
        this.strainId = mob.getClass().getSimpleName();
    }
    
    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setUniqueId("MobUUID", mobUUID);
        tag.setUniqueId("OwnerUUID", ownerId);
        tag.setString("Name", name);
        tag.setString("Class", mobClass.getName());
        tag.setInteger("Skin", skin);
        tag.setFloat("MaxHealth", maxHealth);
        tag.setFloat("AttackDamage", attackDamage);
        tag.setString("StrainID", strainId);
        return tag;
    }
    
    public static TamedParasiteInfo fromNBT(NBTTagCompound tag) {
        try {
            UUID mobUUID = tag.getUniqueId("MobUUID");
            UUID ownerId = tag.getUniqueId("OwnerUUID");
            String name = tag.getString("Name");
            Class<?> raw = Class.forName(tag.getString("Class"));
            int skin = tag.getInteger("Skin");
            float health = tag.getFloat("MaxHealth");
            float damage = tag.getFloat("AttackDamage");
            return new TamedParasiteInfo(mobUUID, ownerId, name, raw.asSubclass(EntityParasiteBase.class), skin, health, damage);
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
    }
    public TamedParasiteInfo(UUID mobUUID, UUID ownerId, String name, Class<? extends EntityParasiteBase> mobClass, int skin, float maxHealth, float attackDamage) {
        this.mobUUID = mobUUID;
        this.ownerId = ownerId;
        this.name = name;
        this.mobClass = mobClass;
        this.strainId = mobClass.getSimpleName();
        this.skin = skin;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
    }
}