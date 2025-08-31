package rlshenanigans.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.client.model.entity.item.ModelPaintingTemplate;
import rlshenanigans.item.ItemPaintingSpawner;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class RenderRLSItemPainting extends TileEntityItemStackRenderer {
    private final ModelPaintingTemplate model = new ModelPaintingTemplate();
    private static final ResourceLocation PAINTING_WOOD = new ResourceLocation("rlshenanigans", "textures/entity/item/painting_template_wood.png");
    private static final float SCALE_BASE = 0.0625F;
    private static long currentTick = 0;
    @Override
    public void renderByItem(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemPaintingSpawner)) return;
        
        GlStateManager.pushMatrix();
      
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.scale(1F, -1F, -1F);
        GlStateManager.rotate(30F, 0F, 1F, 0F);
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(PAINTING_WOOD);
        model.frame.render(SCALE_BASE);
        model.back.render(SCALE_BASE);
        
        GlStateManager.enableCull();
        ItemPaintingSpawner item = (ItemPaintingSpawner) stack.getItem();
        ResourceLocation tex = item.getCurrentTexture(currentTick);
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
        model.painting.render(SCALE_BASE);
        GlStateManager.disableCull();
        
        GlStateManager.popMatrix();
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getMinecraft().world == null) return;
        currentTick = Minecraft.getMinecraft().world.getTotalWorldTime();
    }
}