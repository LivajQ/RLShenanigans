package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.client.model.entity.adapted.ModelShycoAdapted;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;

public class ParasiteRandomRenderHandler {

    public static void init(EntityParasiteBase parasite, ModelBase model) {
        //if (model instanceof ModelInfEnderman) infEndermanSit((ModelInfEnderman) model);
        if (model instanceof ModelShycoAdapted) adaLongarmsHug(parasite, (ModelShycoAdapted) model);

    }
    
    public static void infEndermanSit(ModelInfEnderman model) {
        
        GlStateManager.translate(0.0F, 1.35F, 0.0F);
        
        model.jointLL0.rotateAngleX = (float)Math.toRadians(243.4F);
        model.jointLL1.rotateAngleX = (float)Math.toRadians(50.7F);
        model.jointLL2.rotateAngleX = (float)Math.toRadians(329.6F);
        
        model.jointRL0.rotateAngleX = (float)Math.toRadians(243.4F);
        model.jointRL1.rotateAngleX = (float)Math.toRadians(50.7F);
        model.jointRL2.rotateAngleX = (float)Math.toRadians(329.6F);
        
        model.jointLA0.rotateAngleX = (float)Math.toRadians(309.3F);
        model.jointLA1.rotateAngleX = 0.0F;
        model.jointLA2.rotateAngleX = (float)Math.toRadians(278.9F);
        
        model.jointLA0.rotateAngleY = 0.0F;
        model.jointLA1.rotateAngleY = (float)Math.toRadians(48.7F);
        model.jointLA2.rotateAngleY = 0.0F;
        
        model.jointLA0.rotateAngleZ = 0.0F;
        model.jointLA1.rotateAngleZ = 0.0F;
        model.jointLA2.rotateAngleZ = 0.0F;
        
        model.jointRA0.rotateAngleX = (float)Math.toRadians(309.3F);
        model.jointRA1.rotateAngleX = 0.0F;
        model.jointRA2.rotateAngleX = (float)Math.toRadians(278.9F);
        
        model.jointRA0.rotateAngleY = 0.0F;
        model.jointRA1.rotateAngleY = (float)Math.toRadians(294.1F);
        model.jointRA2.rotateAngleY = 0.0F;
        
        model.jointRA0.rotateAngleZ = 0.0F;
        model.jointRA1.rotateAngleZ = 0.0F;
        model.jointRA2.rotateAngleZ = 0.0F;
    }
    
    public static void adaLongarmsHug(EntityParasiteBase longarms, ModelShycoAdapted model) {
        //why srp...
        model.arm.rotateAngleX = 0.0F;
        model.arm.rotateAngleY = 0.0F;
        model.arm.rotateAngleZ = -0.4886922F;
        
        model.arm_1.rotateAngleX = 2.0420353F;
        model.arm_1.rotateAngleY = 2.4085543F;
        model.arm_1.rotateAngleZ = 0.0F;
        
        model.arm_5.rotateAngleX = (float)Math.PI / 5F;
        model.arm_5.rotateAngleY = 0.0F;
        model.arm_5.rotateAngleZ = 0.6806784F;
        
        model.arm_11.rotateAngleX = 0.0F;
        model.arm_11.rotateAngleY = 0.0F;
        model.arm_11.rotateAngleZ = 0.4886922F;
        
        model.arm_12.rotateAngleX = 2.0420353F;
        model.arm_12.rotateAngleY = -2.4085543F;
        model.arm_12.rotateAngleZ = 0.0F;
        
        model.arm_16.rotateAngleX = (float)Math.PI / 5F;
        model.arm_16.rotateAngleY = 0.0F;
        model.arm_16.rotateAngleZ = -0.6806784F;
        
        if (longarms.getPassengers().isEmpty()) return;
        
        model.jointLA.rotateAngleX = 0.0F;
        model.jointLA.rotateAngleY = 0.0F;
        model.jointLA.rotateAngleZ = 0.0F;
        
        model.jointRA.rotateAngleX = 0.0F;
        model.jointRA.rotateAngleY = 0.0F;
        model.jointRA.rotateAngleZ = 0.0F;
        
        model.jointLA1.rotateAngleX = 0.0F;
        model.jointLA1.rotateAngleY = 0.0F;
        model.jointLA1.rotateAngleZ = 0.0F;
        
        model.jointRA1.rotateAngleX = 0.0F;
        model.jointRA1.rotateAngleY = 0.0F;
        model.jointRA1.rotateAngleZ = 0.0F;
        
        model.arm.rotateAngleX = 0.0F;
        model.arm.rotateAngleY = (float)Math.toRadians(-12.5F);
        model.arm.rotateAngleZ = (float)Math.toRadians(-110.0F);
        
        model.arm_1.rotateAngleX = (float)Math.toRadians(-78.5F);
        model.arm_1.rotateAngleY = (float)Math.toRadians(42.0F);
        model.arm_1.rotateAngleZ = (float)Math.toRadians(180.0F);
        
        model.arm_5.rotateAngleX = (float)Math.toRadians(74.5F);
        model.arm_5.rotateAngleY = (float)Math.toRadians(-24.4F);
        model.arm_5.rotateAngleZ = (float)Math.toRadians(-34.0F);
        
        model.arm_11.rotateAngleX = 0.0F;
        model.arm_11.rotateAngleY = (float)Math.toRadians(12.5F);
        model.arm_11.rotateAngleZ = (float)Math.toRadians(110.0F);
        
        model.arm_12.rotateAngleX = (float)Math.toRadians(-78.5F);
        model.arm_12.rotateAngleY = (float)Math.toRadians(-42.0F);
        model.arm_12.rotateAngleZ = (float)Math.toRadians(180.0F);
        
        model.arm_16.rotateAngleX = (float)Math.toRadians(63.1F);
        model.arm_16.rotateAngleY = (float)Math.toRadians(30.7F);
        model.arm_16.rotateAngleZ = (float)Math.toRadians(40.2F);
    }
    
    /*
    @Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
    static class Handler {
        
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (!(event.getEntityLiving() instanceof EntityPlayer) || !(event.getSource().getTrueSource() instanceof EntityShycoAdapted)) return;
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            EntityShycoAdapted longarms =  (EntityShycoAdapted)event.getSource().getTrueSource();
            
            if (!longarms.getPassengers().isEmpty()) return;
            player.startRiding(longarms);
            longarms.targetTasks.taskEntries.removeIf(entry ->
                    entry.action instanceof EntityAINearestAttackableTargetStatus || entry.action instanceof EntityAIWanderStatus);
            longarms.tasks.addTask(5, new EntityAIWanderStatus(longarms, (double)1.0F, 20, 0.001F, true));
            longarms.setAttackTarget(null);
            
            Vec3d target = RandomPositionGenerator.findRandomTarget(longarms, 10, 7);
            if (target != null) longarms.getNavigator().tryMoveToXYZ(target.x, target.y, target.z, 1.0D);
            
            RLSPacketHandler.INSTANCE.sendToAll(
                    new ParticlePulsePacket(longarms, EnumParticleTypes.HEART, 200, 30));
            
            event.setCanceled(true);
        }
    }
     */
}