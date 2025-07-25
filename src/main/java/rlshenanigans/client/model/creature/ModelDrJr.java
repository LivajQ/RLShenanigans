package rlshenanigans.client.model.creature;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelDrJr extends ModelBase {
    public final ModelRenderer body;
    public final ModelRenderer leftArm;
    public final ModelRenderer rightArm;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightLeg;
    public final ModelRenderer leftAntenna;
    public final ModelRenderer rightAntenna;
    
    public ModelDrJr() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-2.5F, 10F, -2.5F, 5, 10, 5);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);
        
        leftArm = new ModelRenderer(this, 0, 15);
        leftArm.addBox(-0.5F, 0F, -0.5F, 1, 6, 1);
        leftArm.setRotationPoint(-3F, 14F, 0F);
        
        rightArm = new ModelRenderer(this, 4, 15);
        rightArm.addBox(-0.5F, 0F, -0.5F, 1, 6, 1);
        rightArm.setRotationPoint(3F, 14F, 0F);
        
        leftAntenna = new ModelRenderer(this, 8, 15);
        leftAntenna.addBox(-0.5F, -4F, -0.5F, 1, 4, 1);
        leftAntenna.setRotationPoint(-1F, 10F, 0F);
        
        rightAntenna = new ModelRenderer(this, 12, 15);
        rightAntenna.addBox(-0.5F, -4F, -0.5F, 1, 4, 1);
        rightAntenna.setRotationPoint(1F, 10F, 0F);
        
        leftLeg = new ModelRenderer(this, 20, 0);
        leftLeg.addBox(-0.5F, 0F, -0.5F, 1, 4, 1);
        leftLeg.setRotationPoint(-2F, 20F, 0F);
        
        rightLeg = new ModelRenderer(this, 16, 15);
        rightLeg.addBox(-0.5F, 0F, -0.5F, 1, 4, 1);
        rightLeg.setRotationPoint(2F, 20F, 0F);
    }
    
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        body.render(scale);
        leftArm.render(scale);
        rightArm.render(scale);
        leftLeg.render(scale);
        rightLeg.render(scale);
        leftAntenna.render(scale);
        rightAntenna.render(scale);
    }
    
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount,
                                  float ageInTicks, float netHeadYaw, float headPitch,
                                  float scaleFactor, Entity entityIn) {
        float swing = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
        
        leftArm.rotateAngleX = swing;
        rightArm.rotateAngleX = -swing;
        
        leftLeg.rotateAngleX = swing;
        rightLeg.rotateAngleX = -swing;
        
        rightAntenna.rotateAngleX = MathHelper.sin(ageInTicks * 0.3F) * 0.1F;
        leftAntenna.rotateAngleX  = MathHelper.cos(ageInTicks * 0.3F) * 0.1F;
    }
}