package rlshenanigans.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.action.ParasiteCommand;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParasiteCommandPacket;

@SideOnly(Side.CLIENT)
public class ParasiteContextMenu extends GuiScreen {
    
    private final int parasiteEntityId;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int H_GAP = 10;
    private static final int V_GAP = 8;
    
    private static final String[] LABELS = new String[] {
            "Follow",
            "Roam",
            "Give Item",
            "Ride",
            "Ride (Reverse)",
            "Resize",
            "Smooch",
            "Ask For Drops",
            "Spontaneously Combust"
    };
    
    public ParasiteContextMenu(int parasiteEntityId) {
        this.parasiteEntityId = parasiteEntityId;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        int totalButtons = LABELS.length;
        int perColumn = (totalButtons + 1) / 2;
        
        int neededHeight = perColumn * BUTTON_HEIGHT + (perColumn - 1) * V_GAP;
        
        int maxAllowedHeight = this.height - 40;
        int effectiveVGap = V_GAP;
        if (neededHeight > maxAllowedHeight) {
            effectiveVGap = Math.max(2, (maxAllowedHeight - perColumn * BUTTON_HEIGHT) / Math.max(1, perColumn - 1));
            neededHeight = perColumn * BUTTON_HEIGHT + (perColumn - 1) * effectiveVGap;
        }
        
        int startY = (this.height - neededHeight) / 2;
        int col0X = (this.width / 2) - BUTTON_WIDTH - (H_GAP / 2);
        int col1X = (this.width / 2) + (H_GAP / 2);
        
        for (int i = 0; i < totalButtons; i++) {
            int column = i / perColumn;
            int row = i % perColumn;
            int x = (column == 0) ? col0X : col1X;
            int y = startY + row * (BUTTON_HEIGHT + effectiveVGap);
            this.buttonList.add(new GuiButton(i, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, LABELS[i]));
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < 0 || button.id >= ParasiteCommand.values().length) return;
        if (LABELS[button.id].equals("Resize")) {
            this.mc.displayGuiScreen(new ParasiteResizeMenu(this.parasiteEntityId, this));
            return;
        }
        
        ParasiteCommand command = ParasiteCommand.values()[button.id];
        RLSPacketHandler.INSTANCE.sendToServer(new ParasiteCommandPacket(parasiteEntityId, command));
        this.mc.displayGuiScreen(null);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}