package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;

import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class CombatAssistParasiteHandler {
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        
        if (!(attacker instanceof EntityPlayer) || target instanceof EntityParasiteBase) return;
        
        EntityPlayer player = (EntityPlayer) attacker;
        World world = player.world;
        if (world.isRemote) return;
        
        List<EntityParasiteBase> parasites = player.world.getEntitiesWithinAABB(EntityParasiteBase.class,
                player.getEntityBoundingBox().grow(32.0D),
                p -> p != null && !p.isDead && p.getAttackTarget() == null &&
                        p.getEntityData().getBoolean("PookieAffected") && !p.isBeingRidden());
        
        for (EntityParasiteBase parasite : parasites) {
            parasite.setAttackTarget(target);
            player.world.playSound(null, parasite.getPosition(),
                    SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 0.4F, 1.5F);
        }
    }
}
