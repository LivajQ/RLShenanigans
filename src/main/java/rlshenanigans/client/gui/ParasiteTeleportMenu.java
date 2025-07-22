package rlshenanigans.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import rlshenanigans.handlers.RLSPacketHandler;
import rlshenanigans.packet.ParasiteTeleportPacket;
import rlshenanigans.util.TamedParasiteInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ParasiteTeleportMenu extends GuiScreen {
    private final List<TamedParasiteInfo> availableMobs;
    private GuiParasiteList list;
    
    public ParasiteTeleportMenu(List<TamedParasiteInfo> availableMobs) {
        this.availableMobs = availableMobs;
    }
    
    @Override
    public void initGui() {
        int top = 20;
        int bottom = 20;
        int slotHeight = 24;
        
        List<TamedParasiteInfo> reversedMobs = new ArrayList<>(availableMobs);
        Collections.reverse(reversedMobs);
        this.list = new GuiParasiteList(
                this.mc,
                this.width,
                this.height,
                top,
                this.height - bottom,
                slotHeight,
                reversedMobs
        );
        this.list.registerScrollButtons(0, 1);
        
        this.buttonList.clear();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0x80000000);
        
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        
        drawCenteredString(fontRenderer, "Select §dPookie §fto Teleport", width / 2, 10, 0xFFFFFF);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    private static class GuiParasiteList extends GuiSlot {
        private final List<TamedParasiteInfo> mobs;
        
        public GuiParasiteList(Minecraft mc, int width, int height,
                               int top, int bottom, int slotHeight,
                               List<TamedParasiteInfo> mobs) {
            super(mc, width, height, top, bottom, slotHeight);
            this.mobs = mobs;
        }
        
        @Override
        protected int getSize() {
            return mobs.size();
        }
        
        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            if (index >= 0 && index < mobs.size()) {
                UUID target = mobs.get(index).mobUUID;
                RLSPacketHandler.INSTANCE.sendToServer(new ParasiteTeleportPacket(target));
                this.mc.displayGuiScreen(null);
            }
        }
        
        @Override
        protected boolean isSelected(int index) {
            return false;
        }
        
        @Override
        protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
            TamedParasiteInfo info = mobs.get(index);
            String label = info.name + " (" + info.strainId + ")";
            mc.fontRenderer.drawString(label, x + 2, y + 2, 0xFFFFFF);
        }
        @Override
        protected void drawBackground() {}
        
        @Override
        protected void drawContainerBackground(Tessellator tessellator) {}
    }
}