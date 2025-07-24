package rlshenanigans.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.action.ParasiteCommand;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParasiteCommandPacket;

@SideOnly(Side.CLIENT)
public class ParasiteContextMenu extends GuiScreen
{
    
    private final int parasiteEntityId;
    
    public ParasiteContextMenu(int parasiteEntityId) {
        this.parasiteEntityId = parasiteEntityId;
    }
    
    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, centerX - 50, centerY - 100, 120, 20, "Follow"));
        this.buttonList.add(new GuiButton(1, centerX - 50, centerY - 70, 120, 20, "Roam"));
        this.buttonList.add(new GuiButton(2, centerX - 50, centerY - 40, 120, 20, "Ride"));
        this.buttonList.add(new GuiButton(3, centerX - 50, centerY - 10, 120, 20, "Smooch"));
        this.buttonList.add(new GuiButton(4, centerX - 50, centerY + 20, 120, 20, "Ask For Drops"));
        this.buttonList.add(new GuiButton(5, centerX - 50, centerY + 50, 120, 20, "Spontaneously Combust"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        ParasiteCommand command = ParasiteCommand.values()[button.id];
        RLSPacketHandler.INSTANCE.sendToServer(new ParasiteCommandPacket(parasiteEntityId, command));
        this.mc.displayGuiScreen(null);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}