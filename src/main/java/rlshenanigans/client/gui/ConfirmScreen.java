package rlshenanigans.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.action.ParasiteCommand;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParasiteCommandPacket;

@SideOnly(Side.CLIENT)
public class ConfirmScreen extends GuiScreen {
    private final int parasiteEntityId;
    private final GuiScreen parent;
    
    public ConfirmScreen(int parasiteEntityId, GuiScreen parent) {
        this.parasiteEntityId = parasiteEntityId;
        this.parent = parent;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.buttonList.add(new GuiButton(0, centerX - 60, centerY - 10, 120, 20, "Confirm"));
        this.buttonList.add(new GuiButton(1, centerX - 60, centerY + 15, 120, 20, "Cancel"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            RLSPacketHandler.INSTANCE.sendToServer(new ParasiteCommandPacket(parasiteEntityId, ParasiteCommand.DISCARD));
            this.mc.displayGuiScreen(null);
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(parent);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRenderer, "Are you sure?", this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}