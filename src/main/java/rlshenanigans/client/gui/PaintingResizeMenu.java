package rlshenanigans.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.item.EntityPaintingTemplate;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.PaintingResizePacket;

@SideOnly(Side.CLIENT)
public class PaintingResizeMenu extends GuiScreen {
    private final EntityPaintingTemplate paintingEntity;
    private GuiSlider widthSlider;
    private GuiSlider heightSlider;
    
    public PaintingResizeMenu(EntityPaintingTemplate entity) {
        this.paintingEntity = entity;
    }
    
    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.buttonList.clear();
        
        float width = paintingEntity.getWidth();
        float height = paintingEntity.getHeight();
        
        widthSlider = new GuiSlider(new GuiPageButtonList.GuiResponder() {
                    @Override public void setEntryValue(int id, float value) {}
                    @Override public void setEntryValue(int id, boolean value) {}
                    @Override public void setEntryValue(int id, String value) {}
                },
                   0, centerX - 75, centerY - 20, "Width", 1, 5, width, (id, name, value) -> name + ": " + (int) value
        );
        
        heightSlider = new GuiSlider(new GuiPageButtonList.GuiResponder() {
                    @Override public void setEntryValue(int id, float value) {}
                    @Override public void setEntryValue(int id, boolean value) {}
                    @Override public void setEntryValue(int id, String value) {}
        },
                   1, centerX - 75, centerY + 10, "Height", 1, 5, height, (id, name, value) -> name + ": " + (int) value
        );
        
        this.buttonList.add(widthSlider);
        this.buttonList.add(heightSlider);
        this.buttonList.add(new GuiButton(2, centerX - 35, centerY + 50, 70, 20, "Apply"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 2) {
            int width = Math.round(widthSlider.getSliderValue());
            int height = Math.round(heightSlider.getSliderValue());
            RLSPacketHandler.INSTANCE.sendToServer(new PaintingResizePacket(paintingEntity.getEntityId(), width, height));
            this.mc.displayGuiScreen(null);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}