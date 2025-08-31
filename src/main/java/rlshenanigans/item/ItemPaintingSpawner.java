package rlshenanigans.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.item.EntityPaintingTemplate;

import java.util.List;

public class ItemPaintingSpawner extends Item {
    private final String texture;
    private final int frames;
    
    public ItemPaintingSpawner(String texture, int frames) {
        this.texture = texture;
        this.frames = frames;
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String key = getTranslationKey() + ".tooltip.1";
        String line = I18n.format(key);
        if (!line.equals(key)) {
            tooltip.add(line);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            RayTraceResult result = player.rayTrace(5.0D, 1.0F);
            if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
            }
            
            BlockPos hitPos = result.getBlockPos();
            EnumFacing hitFace = result.sideHit;
            BlockPos spawnPos = hitPos.offset(hitFace);
            
            double x = spawnPos.getX() + 0.5;
            double y = spawnPos.getY();
            double z = spawnPos.getZ() + 0.5;
            
            EnumFacing facing = player.getHorizontalFacing().getOpposite();
            
            EntityPaintingTemplate entity = new EntityPaintingTemplate(
                    world, x, y, z, this.texture, this.frames, 1, 1, facing
            );
            world.spawnEntity(entity);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
    
    public ResourceLocation getCurrentTexture(long currentTick) {
        if (frames == 1) return new ResourceLocation("rlshenanigans", texture + ".png");
        else {
            int frameIndex = (int)(currentTick % frames) + 1;
            return new ResourceLocation("rlshenanigans", texture + "_" + frameIndex + ".png");
        }
    }
}