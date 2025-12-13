package rlshenanigans.item.spell;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import rlshenanigans.handlers.CombatAssistHandler;
import rlshenanigans.handlers.ForgeConfigHandler;

import java.util.List;
import java.util.stream.Collectors;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class ItemSpellFrenzy extends ItemSpellBase {

    public ItemSpellFrenzy(String registryName, ForgeConfigHandler.SpellOptions options) {
        super(registryName, options);
    }
    
    @Override
    public void castSpell(EntityLivingBase caster) {
        this.playCastSound(caster, SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 0.6F);
        this.playCastSound(caster, SoundEvents.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
        
        WorldServer world = (WorldServer) caster.world;
        double x = caster.posX;
        double y = caster.posY + caster.height / 2.0;
        double z = caster.posZ;
        
        int range = 10;
        int[][] dirs = {
                {1,0,0},
                {-1,0,0},
                {0,0,1},
                {0,0,-1},
                {0,1,0},
                {0,-1,0}
        };
        
        for (int[] dir : dirs) {
            for (int i = 1; i <= range; i++) {
                double px = x + dir[0] * i;
                double py = y + dir[1] * i;
                double pz = z + dir[2] * i;
                
                world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 10, 0.25D, 0.25D, 0.25D, 0.0D);
            }
        }
        
        List<EntityLiving> nearby = caster.world.getEntitiesWithinAABB(EntityLiving.class, caster.getEntityBoundingBox().grow(24.0D));
        
        List<EntityLiving> candidates;
        if (caster instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) caster;
            candidates = nearby.stream()
                    .filter(e -> !CombatAssistHandler.isEntityTamedByPlayer(e, player))
                    .collect(Collectors.toList());
        }
        else candidates = nearby;
        
        if (candidates.size() <= 1) return;
        
        for (EntityLiving mob : candidates) {
            EntityLiving target;
            do target = candidates.get(RLSRAND.nextInt(candidates.size()));
            while (target == mob && candidates.size() > 1);
            
            mob.setRevengeTarget(target);
            mob.setAttackTarget(target);
            this.spawnCastParticle(mob, getTextureIndexFromEnum(EnumParticleTypes.VILLAGER_ANGRY), 25);
        }
    }
}