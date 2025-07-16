package rlshenanigans.potion;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import rlshenanigans.entity.ai.*;

import java.util.List;

public class PotionPookie extends PotionBase {
    
    public static final PotionPookie INSTANCE = new PotionPookie();
    public static final double EFFECT_RADIUS = 32.0D;
    public static final double EFFECT_RADIUS_SQ = EFFECT_RADIUS * EFFECT_RADIUS;
    
    public PotionPookie() {
        super("Pookie", false, 0xFFC0CB);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living.world.isRemote || !(living instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) living;
        World world = player.world;
        
        List<EntityParasiteBase> parasites = world.getEntitiesWithinAABB(
                EntityParasiteBase.class,
                player.getEntityBoundingBox().grow(EFFECT_RADIUS)
        );
        
        for (EntityParasiteBase parasite : parasites) {
            NBTTagCompound tag = parasite.getEntityData();
            
            if (tag.getBoolean("Tamed")) continue;
            
            parasite.targetTasks.taskEntries.removeIf(entry ->
            {
                String name = entry.action.getClass().getSimpleName().toLowerCase();
                return name.contains("near");
            });
            
            parasite.targetTasks.taskEntries.removeIf(entry ->
            {
                String name = entry.action.getClass().getSimpleName().toLowerCase();  //comment out to enable retaliation
                return name.contains("hurt");
            });
            
            boolean hasFollowTask = parasite.tasks.taskEntries.stream().anyMatch(entry ->
                    entry.action instanceof ParasiteEntityAIFollow
            );
            
           if(!hasFollowTask)
           {
               parasite.tasks.addTask(6, new ParasiteEntityAIFollow(parasite, player, 1.0D, 2.0F, 12.0F));
           }
            
            if (parasite.getAttackTarget() == player || parasite.getRevengeTarget() == player) {
                parasite.setAttackTarget(null);
                parasite.setRevengeTarget(null);
            }
            
            if (!tag.getBoolean("PookieAffected")) {
                tag.setBoolean("PookieAffected", true);
                tag.setBoolean("DropsGone", true);
            }
        }
    }
}