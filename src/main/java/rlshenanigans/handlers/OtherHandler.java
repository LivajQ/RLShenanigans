package rlshenanigans.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.lycaniterideable.EntityGrue;
import rlshenanigans.entity.lycaniterideable.EntityWraith;
import rlshenanigans.util.SplashTextEntries;

import java.lang.reflect.Field;

import static rlshenanigans.RLShenanigans.RLSRAND;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class OtherHandler {
    public static boolean canChangeSplash = true;
    public static String currentSplash = "";
    
    @SubscribeEvent
    public static void lavaChickenDrop(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        EntityLivingBase deadMob = event.getEntityLiving();
        if (!(deadMob instanceof EntityZombie) && !(deadMob instanceof EntityChicken)) return;
        ItemStack lavaChickenDisc = new ItemStack(ModRegistry.musicDiscLavaChicken);
        
        if (deadMob instanceof EntityZombie) {
            if (deadMob.getRidingEntity() instanceof EntityChicken) deadMob.entityDropItem(lavaChickenDisc, 0.5F);
        }
        else {
            if (deadMob.getPassengers().stream().anyMatch(e -> e instanceof EntityZombie)) deadMob.entityDropItem(lavaChickenDisc, 0.5F);
        }
    }
    
    @SubscribeEvent
    public static void replaceInvalidLycanites(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        World world = event.getWorld();
        if (world.isRemote) return;
        
        if (entity instanceof com.lycanitesmobs.core.entity.creature.EntityGrue) {
            EntityGrue rlsGrue = new EntityGrue(world);
            rlsGrue.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            rlsGrue.setCustomNameTag("Shadow Clown");
            world.spawnEntity(rlsGrue);
            event.setCanceled(true);
        }
        
        else if (entity instanceof  com.lycanitesmobs.core.entity.creature.EntityWraith) {
            EntityWraith rlsWraith = new EntityWraith(world);
            rlsWraith.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
            world.spawnEntity(rlsWraith);
            event.setCanceled(true);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void splashScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        GuiScreen gui = event.getGui();
        String className = gui.getClass().getName();
        
        if (!(gui instanceof GuiMainMenu) && !className.equals("lumien.custommainmenu.gui.GuiCustom")) return;
        if (!Loader.isModLoaded("rlmixins")) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) return;
        
        if (currentSplash.isEmpty() && canChangeSplash) {
            Field splashField;
            try {
                try {
                    splashField = GuiMainMenu.class.getDeclaredField("field_73975_c");
                } catch (NoSuchFieldException ex) {
                    splashField = GuiMainMenu.class.getDeclaredField("splashText");
                }
                splashField.setAccessible(true);
                currentSplash = (String) splashField.get(gui);
            } catch (Exception ignored) {}
            
            if (currentSplash == null || currentSplash.isEmpty()) {
                if (RLSRAND.nextDouble() * 100 < ForgeConfigHandler.client.splashTextChance) currentSplash = SplashTextEntries.getRandomSplash();
            }
            
            canChangeSplash = false;
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(gui.width / 2.0F + 90.0F, gui.height / 5.0F + 36.0F, 0.0F);
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
        float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * ((float)Math.PI * 2F)) * 0.1F);
        f = f * 100.0F / (float)(mc.fontRenderer.getStringWidth(currentSplash) + 32);
        GlStateManager.scale(f, f, f);
        mc.fontRenderer.drawStringWithShadow(currentSplash, -mc.fontRenderer.getStringWidth(currentSplash) / 2f, -8f, -256);
        GlStateManager.popMatrix();
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void splashRefresher(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen gui = event.getGui();
        String className = gui.getClass().getName();
        
        if (!(gui instanceof GuiMainMenu) && !className.equals("lumien.custommainmenu.gui.GuiCustom")) return;
        if (!Loader.isModLoaded("rlmixins")) return;
        
        canChangeSplash = true;
        currentSplash = "";
    }
}