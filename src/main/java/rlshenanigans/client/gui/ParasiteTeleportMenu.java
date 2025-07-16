package rlshenanigans.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParasiteTeleportPacket;
import rlshenanigans.util.TamedParasiteInfo;

import java.util.List;
import java.util.UUID;

public class ParasiteTeleportMenu extends GuiScreen {
    private final List<TamedParasiteInfo> availableMobs;
    
    public ParasiteTeleportMenu(List<TamedParasiteInfo> availableMobs) {
        this.availableMobs = availableMobs;
    }
 
    
    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        this.buttonList.clear();
        for (int i = 0; i < availableMobs.size(); i++) {
            TamedParasiteInfo mobInfo = availableMobs.get(i);
            String label = mobInfo.name + " (" + mobInfo.strainId + ")";
            this.buttonList.add(new GuiButton(i, centerX - 70, centerY - 80 + i * 25, 140, 20, label));
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        UUID mobUUID = availableMobs.get(button.id).mobUUID;
        RLSPacketHandler.INSTANCE.sendToServer(new ParasiteTeleportPacket(mobUUID));
        this.mc.displayGuiScreen(null);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}