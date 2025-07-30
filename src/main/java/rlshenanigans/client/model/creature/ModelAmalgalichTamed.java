package rlshenanigans.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelCreatureObj;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlshenanigans.entity.creature.EntityAmalgalichTamed;

@SideOnly(Side.CLIENT)
public class ModelAmalgalichTamed extends ModelCreatureObj {
	
    public ModelAmalgalichTamed() {
        this(1.0F);
    }

    public ModelAmalgalichTamed(float shadowSize) {

    	this.initModel("amalgalich", LycanitesMobs.modInfo, "entity/amalgalich");
		
		this.lookHeadScaleX = 0F;
		this.lookHeadScaleY = 0.75F;
		this.lookNeckScaleX = 0.25F;
		this.lookNeckScaleY = 0.25F;
		
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
	
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerCreatureEffect(renderer, "fire", true, LayerCreatureEffect.BLEND.ADD.id, true));
	}

	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
    	if(layer != null && entity instanceof EntityAmalgalichTamed) {
    		return ((EntityAmalgalichTamed)entity).getConsumptionAnimation() > 0;
		}
    	return super.canRenderPart(partName, entity, layer, trophy);
	}

	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		float posX = 0F;
		float posY = 0F;
		float posZ = 0F;
		float angleX = 0F;
		float angleY = 0F;
		float angleZ = 0F;
		float rotation = 0F;
		float rotX = 0F;
		float rotY = 0F;
		float rotZ = 0F;
		
		if(entity instanceof EntityAmalgalichTamed && ((EntityAmalgalichTamed)entity).getConsumptionAnimation() > 0) {
			float consumptionScale = ((EntityAmalgalichTamed)entity).getConsumptionAnimation();
			if(consumptionScale == 1) {
				loop *= 8 * consumptionScale;
			}
			if(partName.equals("body")) {
				posY -= 8F * consumptionScale;
			}
			if(partName.equals("legleftfront01") || partName.equals("legleftback01")) {
				rotZ += 65F * consumptionScale;
			}
			if(partName.equals("legrightfront01") || partName.equals("legrightback01")) {
				rotZ -= 65F * consumptionScale;
			}
			if(partName.equals("legleftfront03") || partName.equals("legleftback03")) {
				rotZ -= 25F * consumptionScale;
			}
			if(partName.equals("legrightfront03") || partName.equals("legrightback03")) {
				rotZ += 25F * consumptionScale;
			}
			if(partName.equals("neck")) {
				rotX += 20F * consumptionScale;
			}
			if(partName.equals("head")) {
				rotX -= 20F * consumptionScale;
			}
			if(partName.equals("mouth")) {
				rotX += 40F * consumptionScale;
			}
			if(partName.equals("armleft01")) {
				rotZ += 35F * consumptionScale;
			}
			if(partName.equals("armright01")) {
				rotZ -= 35F * consumptionScale;
			}
		}
		
		if(partName.equals("mouth")) {
			this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.1F - 0.1F), 0.0F, 0.0F);
		}
		if(partName.equals("neck")) {
			this.rotate((float) -Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
		}
		if(partName.contains("armleft")) {
			rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
			rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
		}
		if(partName.contains("armright")) {
			rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
			rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
		}
		if(partName.contains("tail")) {
			rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
			rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
		}
		
		else if(partName.equals("fingerleft01") || partName.equals("fingerright01")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F), 0, 0);
		}
		else if(partName.equals("fingerleft02") || partName.equals("fingerright02")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 20) * 0.2F) * 0.2F), 0, 0);
		}
		else if(partName.equals("fingerleft03") || partName.equals("fingerright03")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 40) * 0.2F) * 0.2F), 0, 0);
		}
		
		float walkSwing = 0.1F;
		if(partName.contains("armleft")) {
			rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
			rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
		}
		if(partName.contains("armright")) {
			rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
			rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
		}
		if(partName.contains("legrightfront") || partName.contains("legleftback"))
			rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
		if(partName.contains("legleftfront") || partName.contains("legrightback"))
			rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
		
		this.angle(rotation, angleX, angleY, angleZ);
		this.rotate(rotX, rotY, rotZ);
		this.translate(posX, posY, posZ);
	}
}
