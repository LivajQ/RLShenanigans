package rlshenanigans.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rlshenanigans.entity.item.EntityPaintingTemplate;

public class ItemPaintingSpawner extends Item
{
    private final String texture;
    private final int frames;
    
    public ItemPaintingSpawner(String texture, int frames)
    {
        this.texture = texture;
        this.frames = frames;
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        if (!world.isRemote)
        {
            RayTraceResult result = player.rayTrace(5.0D, 1.0F);
            if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK)
            {
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
}
