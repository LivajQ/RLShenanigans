package rlshenanigans.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCube extends ModelBase {
    private final ModelRenderer cube;
    
    public ModelCube() {
        this.textureWidth = 16;
        this.textureHeight = 16;
        
        cube = new ModelRenderer(this, 0, 0);
        cube.addBox(-8F, 0, -8F, 16, 16, 16);
    }
    
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        cube.render(scale);
    }
}