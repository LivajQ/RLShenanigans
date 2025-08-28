package rlshenanigans.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

public class TileEntityPaintingTemplate extends TileEntity implements ITickable
{
    private String painting;
    private int frames;
    private int currentFrame = 1;
    
    public TileEntityPaintingTemplate() {}
    
    public TileEntityPaintingTemplate(String painting, int frames) {
        this.painting = painting;
        this.frames = frames;
    }
    
    @Override
    public void update() {
        if (frames > 1) {
            currentFrame = (currentFrame % frames) + 1;
        }
    }
    
    public ResourceLocation getPainting() {
        return frames == 1
                ? new ResourceLocation("rlshenanigans", painting + ".png")
                : new ResourceLocation("rlshenanigans", painting + "_" + currentFrame + ".png");
    }
}