package rlshenanigans.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.lycanitesmobs.core.entity.creature.EntityDarkling;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rlshenanigans.RLShenanigans;
import rlshenanigans.entity.creature.EntityAmalgalichTamed;
import rlshenanigans.potion.PotionPookie;

import java.util.List;

@Mod.EventBusSubscriber(modid = RLShenanigans.MODID)
public class CombatAssistHandler {
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getTrueSource() instanceof EntityLivingBase)) return;
        EntityLivingBase target = event.getEntityLiving();
        EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
        
        EntityPlayer player;
        EntityLivingBase entity;
        if (attacker instanceof EntityPlayer && !(target instanceof EntityParasiteBase)) {
            player = (EntityPlayer) attacker;
            entity = target;
        }
        else if (!(attacker instanceof EntityParasiteBase) && target instanceof EntityPlayer) {
            player = (EntityPlayer) target;
            entity = attacker;
        }
        else return;
        if (player.world.isRemote) return;
        if (player.getActivePotionEffect(PotionPookie.INSTANCE) == null) return;
        
        List<EntityParasiteBase> parasites = player.world.getEntitiesWithinAABB(EntityParasiteBase.class,
                player.getEntityBoundingBox().grow(24.0D),
                p -> p != null && !p.isDead && p.getAttackTarget() != entity && !p.getEntityData().getBoolean("Tamed") &&
                        p.getEntityData().getBoolean("PookieAffected") && !p.isBeingRidden());
        
        for (EntityParasiteBase parasite : parasites) {
            parasite.setAttackTarget(entity);
        }
    }
    
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        
        if (event.getEntityLiving() instanceof EntityDarkling) {
            EntityDarkling darkling = (EntityDarkling) event.getEntityLiving();
            
            if (!darkling.isTamed() && darkling.getAttackTarget() instanceof EntityPlayer) {
                EntityPlayer targetPlayer = (EntityPlayer) darkling.getAttackTarget();
                
                List<EntityAmalgalichTamed> amalgalichList = darkling.world.getEntitiesWithinAABB(
                        EntityAmalgalichTamed.class,
                        targetPlayer.getEntityBoundingBox().grow(10.0D),
                        amalgalich -> amalgalich.isTamed() && amalgalich.getPlayerOwner() != null
                );
                
                for (EntityAmalgalichTamed amalgalich : amalgalichList) {
                    if (amalgalich.getPlayerOwner().getUniqueID().equals(targetPlayer.getUniqueID())) {
                        darkling.setPlayerOwner(targetPlayer);
                        darkling.onTamedByPlayer();
                        darkling.setAggressive(true);
                        break;
                    }
                }
            }
        }
    }
}
