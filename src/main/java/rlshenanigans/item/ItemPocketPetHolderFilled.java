package rlshenanigans.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import rlshenanigans.RLShenanigans;
import rlshenanigans.handlers.ModRegistry;

import javax.annotation.Nonnull;

public class ItemPocketPetHolderFilled extends Item {
    public ItemPocketPetHolderFilled() {
        this.setMaxStackSize(1);
        this.setTranslationKey("pocket_pet_holder_filled");
        this.setRegistryName(RLShenanigans.MODID, "pocket_pet_holder_filled");
    }
    
    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        stack.setTagCompound(new NBTTagCompound());
    }
    
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (hand != EnumHand.MAIN_HAND) return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (!stack.hasTagCompound()) return new ActionResult<>(EnumActionResult.FAIL, stack);
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("MobRegistry") || !tag.hasKey("MobData")) return new ActionResult<>(EnumActionResult.FAIL, stack);
        
        if (!world.isRemote) {
            String mobRegistry = tag.getString("MobRegistry");
            ResourceLocation entityResource = new ResourceLocation(mobRegistry);
            Entity entity = EntityList.createEntityByIDFromName(entityResource, world);
            
            if (entity == null) return new ActionResult<>(EnumActionResult.FAIL, stack);
            
            if (entity instanceof EntityLiving) {
                NBTTagCompound mobNBT = tag.getCompoundTag("MobData");
                entity.readFromNBT(mobNBT);
                entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                world.spawnEntity(entity);
                
                ItemStack emptyHolder = new ItemStack(ModRegistry.pocketPetHolderEmpty);
                player.setHeldItem(hand, emptyHolder);
            }
        }
        
        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.25F);
        
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}