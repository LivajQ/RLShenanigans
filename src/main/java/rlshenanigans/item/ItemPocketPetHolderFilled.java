package rlshenanigans.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.*;
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

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPocketPetHolderFilled extends Item {
    public ItemPocketPetHolderFilled() {
        this.setMaxStackSize(1);
        this.setTranslationKey("pocket_pet_holder_filled");
        this.setRegistryName(RLShenanigans.MODID, "pocket_pet_holder_filled");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!stack.hasTagCompound()) {
            return;
        }
        
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("MobRegistry") || !tag.hasKey("MobData")) {
            return;
        }
        
        String mobName = "None";
        String mobDisplayClass = "Unknown";
        float mobHealth = -1;
        float mobMaxHealth = -1;
        
        String mobRegistry = tag.getString("MobRegistry");
        if (mobRegistry != null && !mobRegistry.isEmpty()) {
            ResourceLocation rl = new ResourceLocation(mobRegistry);
            
            Entity tempEntity = EntityList.createEntityByIDFromName(rl, worldIn);
            if (tempEntity != null) {
                mobDisplayClass = tempEntity.getDisplayName().getUnformattedText();
                
                if (tempEntity instanceof EntityLivingBase) {
                    mobMaxHealth = (float) ((EntityLivingBase) tempEntity)
                            .getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                            .getBaseValue();
                }
                
                tempEntity.setDead();
            }
        }
        
        NBTTagCompound mobNBT = tag.getCompoundTag("MobData");
        
        if (mobNBT.hasKey("CustomName") && !mobNBT.getString("CustomName").isEmpty()) {
            mobName = mobNBT.getString("CustomName");
        }
        
        if (mobNBT.hasKey("Health")) {
            mobHealth = mobNBT.getFloat("Health");
        }
        
        if (mobNBT.hasKey("MaxHealth")) {
            mobMaxHealth = mobNBT.getFloat("MaxHealth");
        }
        
        tooltip.add("§eName: §f" + mobName);
        tooltip.add("§eType: §f" + mobDisplayClass);
        
        if (mobHealth >= 0) {
            String healthDisplay = mobHealth + (mobMaxHealth > 0 ? " / " + mobMaxHealth : "");
            tooltip.add("§eHealth: §f" + healthDisplay);
        } else {
            tooltip.add("§eHealth: §fUnknown");
        }
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