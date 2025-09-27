package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCrux;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.*;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static rlshenanigans.util.ParasiteRegistry.RANGED_PARASITES;

public class RideMobUtils {
    
    private static final Map<UUID, Long> attackCooldowns = new HashMap<>();
    
    protected static boolean isValidMob(EntityLiving mob) {
        boolean isParasite = mob instanceof EntityParasiteBase;
        return (isParasite && mob.getEntityData().getBoolean("Tamed")) ||
                (!isParasite && mob.getEntityData().getBoolean("MiscTamed"));
    }
    
    protected static void parasiteCorrectEyeHeight(EntityPlayerSP player) {
        if (player.getRidingEntity() instanceof EntityInfDragonE) player.eyeHeight = 3.5F;
        if (player.getRidingEntity() instanceof EntityCrux) player.eyeHeight = 2.5F;
        if (player.getRidingEntity() instanceof EntityMes) player.eyeHeight = 2.0F;
        if (player.getRidingEntity() instanceof EntityAlafha) player.eyeHeight = 3.0F;
        if (player.getRidingEntity() instanceof EntityVenkrol) player.eyeHeight = 2.0F;
        if (player.getRidingEntity() instanceof EntityVenkrolSII) player.eyeHeight = 2.5F;
        if (player.getRidingEntity() instanceof EntityVenkrolSIII) player.eyeHeight = 2.5F;
        if (player.getRidingEntity() instanceof EntityVenkrolSIV) player.eyeHeight = 3.5F;
        if (player.getRidingEntity() instanceof EntityDodSIV) player.eyeHeight = 5.0F;
        if (player.getRidingEntity() instanceof EntityGanro) player.eyeHeight = 2.5F;
        if (player.getRidingEntity() instanceof EntityEsor) player.eyeHeight = 2.5F;
        if (player.getRidingEntity() instanceof EntityJinjo) player.eyeHeight = 6.5F;
        if (player.getRidingEntity() instanceof EntityPheon) player.eyeHeight = 10.0F;
        if (player.getRidingEntity() instanceof EntityVesta) player.eyeHeight = 4.0F;
        if (player.getRidingEntity() instanceof EntityOronco) player.eyeHeight = 5.5F;
        if (player.getRidingEntity() instanceof EntityTerla) player.eyeHeight = 10.0F;
    }
    
    protected static void purgeAttackCooldown(EntityLiving mob) {
        attackCooldowns.remove(mob.getUniqueID());
    }
    
    protected static void fireParasiteProjectile(EntityLivingBase shooter) {
        if (shooter.world.isRemote) return;
        
        ProjectileLauncher launcher = RANGED_PARASITES.get(shooter.getClass());
        
        UUID id = shooter.getUniqueID();
        long now = System.currentTimeMillis();
        
        if (shooter instanceof EntityJinjo) {
            if (!canFire(id, 1500L)) return;
            EntityJinjo jinjo = (EntityJinjo) shooter;
            float damage = (float) jinjo.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()
                    * SRPConfigMobs.jinjoExplotionMult;
            spawnBomb(jinjo, 4.0F, 2, damage, 7, SRPConfigMobs.jinjoGriefing);
            return;
        }
        
        if (shooter instanceof EntityOmboo) {
            if (!canFire(id, 1500L)) return;
            spawnBomb((EntityOmboo) shooter, 1.0F, 1,(float) SRPAttributes.OMBOO_BOMBDAMAGE, 4, SRPConfigMobs.ombooGriefing);
            return;
        }
        
        if (launcher == null) return;
        
        long last = attackCooldowns.getOrDefault(id, 500L);
        if (now - last < launcher.cooldown) return;
        attackCooldowns.put(id, now);
        
        Vec3d look = shooter.getLook(1.0F);
        double forwardOffset = 2.0;
        double x = shooter.posX + look.x * forwardOffset;
        double y = shooter.posY + shooter.getEyeHeight() - 0.2 + look.y * forwardOffset;
        double z = shooter.posZ + look.z * forwardOffset;
        
        try {
            Constructor<? extends Entity> constructor = launcher.projectileClass.getConstructor(
                    World.class, EntityLivingBase.class, double.class, double.class, double.class);
            
            Entity projectile = constructor.newInstance(shooter.world, shooter, look.x, look.y, look.z);
            projectile.setPosition(x, y, z);
            
            projectile.motionX = look.x;
            projectile.motionY = look.y;
            projectile.motionZ = look.z;
            
            shooter.world.spawnEntity(projectile);
            shooter.playSound(launcher.sound, launcher.volume, launcher.pitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void spawnBomb(EntityParasiteBase shooter, float stren, int skin, float damage, int damageType, boolean griefing) {
        EntityBomb bomb = new EntityBomb(shooter.world, shooter, griefing);
        bomb.copyLocationAndAnglesFrom(shooter);
        bomb.setFuse(80);
        bomb.setStren(stren);
        bomb.setSkin(skin);
        bomb.setDamage(damage, damageType);
        bomb.updateSTR();
        shooter.world.spawnEntity(bomb);
        shooter.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
    }
    
    private static boolean canFire(UUID id, long cooldown) {
        long now = System.currentTimeMillis();
        long last = attackCooldowns.getOrDefault(id, 500L);
        if (now - last < cooldown) return false;
        attackCooldowns.put(id, now);
        return true;
    }
}
