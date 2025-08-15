package rlshenanigans.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.ModRegistry;

import java.util.List;

public class ItemPocketPetHolderEmpty extends Item {
    
    public ItemPocketPetHolderEmpty() {
        this.setMaxStackSize(16);
        this.setTranslationKey("pocket_pet_holder_empty");
        this.setRegistryName(RLShenanigans.MODID, "pocket_pet_holder_empty");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for (int i = 0; i < 10; i++) {
            String key = getTranslationKey() + ".tooltip." + i;
            String line = I18n.format(key);
            if (!line.equals(key)) {
                tooltip.add(line);
            } else {
                break;
            }
        }
    }
    
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        stack.setTagCompound(new NBTTagCompound());
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isRemote) return;
        
        NBTTagCompound stackTag = stack.getTagCompound();
        if (stackTag == null) return;
        
        if (stackTag.getBoolean("SwapGreenlight")) {
            NBTTagCompound filledNBT = stackTag.getCompoundTag("FilledHolderNBT");
            ItemStack filledHolder = new ItemStack(filledNBT);
            
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                player.setHeldItem(EnumHand.MAIN_HAND, filledHolder);
            }
            
            stackTag.removeTag("FilledHolderNBT");
            stackTag.setBoolean("SwapGreenlight", false);
            stack.setTagCompound(stackTag);
        }
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!(target instanceof EntityLiving)) return false;
        NBTTagCompound entityData = target.getEntityData();
        if (!entityData.getBoolean("MiscTamed")) return false;
        if (!entityData.hasUniqueId("OwnerUUID")) return false;
        if (!entityData.getUniqueId("OwnerUUID").equals(player.getUniqueID())) return false;
        if (hand != EnumHand.MAIN_HAND) return false;
        
        ResourceLocation rl = EntityList.getKey(target);
        if (rl == null) return false;
        
        ItemStack filledHolder = new ItemStack(ModRegistry.pocketPetHolderFilled);
        
        NBTTagCompound filledTag = new NBTTagCompound();
        
        filledTag.setString("MobRegistry", rl.toString());
        
        NBTTagCompound mobNBT = new NBTTagCompound();
        target.writeToNBT(mobNBT);
        filledTag.setTag("MobData", mobNBT);
        
        filledHolder.setTagCompound(filledTag);
        
        if (!player.world.isRemote) {
            if (stack.getCount() == 1 && !player.capabilities.isCreativeMode) {
                NBTTagCompound stackTag = stack.getTagCompound();
                if (stackTag == null) stackTag = new NBTTagCompound();
                
                stackTag.setBoolean("SwapGreenlight", true);
                
                NBTTagCompound filledNBT = new NBTTagCompound();
                filledHolder.writeToNBT(filledNBT);
                
                stackTag.setTag("FilledHolderNBT", filledNBT);
                
                stack.setTagCompound(stackTag);
            } else {
                stack.shrink(1);
                if (!player.inventory.addItemStackToInventory(filledHolder))
                {
                    player.dropItem(filledHolder, false);
                }
            }
            
            target.setDead();
        }
        
        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS, 1.0F, 1.25F);
        
        return true;
    }
}