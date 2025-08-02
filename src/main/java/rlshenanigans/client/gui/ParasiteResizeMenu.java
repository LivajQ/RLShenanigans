package rlshenanigans.client.gui;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.action.ParasiteCommand;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParasiteCommandPacket;

@SideOnly(Side.CLIENT)
public class ParasiteResizeMenu extends GuiScreen
{
    
    private final int parasiteEntityId;
    private final GuiScreen parent;
    private GuiSlider resizeSlider;
    private static final int SLIDER_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    
    public ParasiteResizeMenu(int parasiteEntityId, GuiScreen parent) {
        this.parasiteEntityId = parasiteEntityId;
        this.parent = parent;
    }
    
    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.buttonList.clear();
        
        float sizeMultiplier = 1.0f;
        if (Minecraft.getMinecraft().world != null) {
            Entity e = Minecraft.getMinecraft().world.getEntityByID(this.parasiteEntityId);
            if (e instanceof EntityParasiteBase) {
                float synced = e.getEntityData().getFloat("SizeMultiplier");
                if (!Float.isNaN(synced) && synced > 0f) {
                    sizeMultiplier = synced;
                }
            }
        }
        
        int defaultSliderValue = Math.round(sizeMultiplier * 100.0f);
        defaultSliderValue = Math.max(25, Math.min(800, defaultSliderValue));
        
        resizeSlider = new GuiSlider(new GuiPageButtonList.GuiResponder() {
            @Override public void setEntryValue(int id, float value) {}
            @Override public void setEntryValue(int id, boolean value) {}
            @Override public void setEntryValue(int id, String value) {}
        }, 0, centerX - SLIDER_WIDTH / 2, centerY - 10, "Resize", 25, 800, defaultSliderValue, (id, name, value) -> name + ": " + (int) value);
        
        this.buttonList.add(resizeSlider);
        
        this.buttonList.add(new GuiButton(1, centerX - 75, centerY + 20, 70, BUTTON_HEIGHT, "Apply"));
        
        this.buttonList.add(new GuiButton(2, centerX + 5, centerY + 20, 70, BUTTON_HEIGHT, "Cancel"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        System.out.println("I start");
        if (button.id == 1) {
            float value = Math.round(resizeSlider.getSliderValue()) / 100.0F;
            RLSPacketHandler.INSTANCE.sendToServer(new ParasiteCommandPacket(parasiteEntityId, ParasiteCommand.RESIZE, value));
            this.mc.displayGuiScreen(null);
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(parent);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}