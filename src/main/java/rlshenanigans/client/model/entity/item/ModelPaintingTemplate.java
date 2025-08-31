package rlshenanigans.client.model.entity.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPaintingTemplate extends ModelBase {
    public final ModelRenderer body;
    public final ModelRenderer frame;
    public final ModelRenderer painting;
    public final ModelRenderer back;
    
    public ModelPaintingTemplate() {
        textureWidth = 16;
        textureHeight = 16;
        
        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);
        
        
        frame = new ModelRenderer(this);
        frame.setRotationPoint(0.0F, 0.0F, -0.5F);
        body.addChild(frame);
        frame.cubeList.add(new ModelBox(frame, -14, 0, -8.0F, -8.0F, -1.0F, 16, 1, 2, 0.0F, false));
        frame.cubeList.add(new ModelBox(frame, -14, 0, -8.0F, 7.0F, -1.0F, 16, 1, 2, 0.0F, false));
        frame.cubeList.add(new ModelBox(frame, 1, 0, -8.0F, -7.0F, -1.0F, 1, 14, 2, 0.0F, false));
        frame.cubeList.add(new ModelBox(frame, 1, 0, 7.0F, -7.0F, -1.0F, 1, 14, 2, 0.0F, false));
        
        painting = new ModelRenderer(this);
        painting.setRotationPoint(0.0F, 0.0F, -1.0F);
        body.addChild(painting);
        painting.cubeList.add(new ModelBox(painting, 0, 0, -7.0F, -7.0F, 0.0F, 14, 14, 0, 0.0F, false));
        
        back = new ModelRenderer(this);
        back.setRotationPoint(0.0F, 0.0F, -1.0F);
        body.addChild(back);
        back.cubeList.add(new ModelBox(back, 0, 0, -7.0F, -7.0F, 0.5F, 14, 14, 1, 0.0F, false));
    }
    
    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        body.render(f5);
    }
    
    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}