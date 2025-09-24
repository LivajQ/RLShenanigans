package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOrch;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityElvia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityLencia;
import com.dhanantry.scapeandrunparasites.entity.projectile.*;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.init.SoundEvents;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParasiteRegistry
{
    
    public final String name;
    public final String texture;
    public final String category;
    
    public ParasiteRegistry(String name, String texture, String category)
    {
        this.name = name;
        this.texture = texture;
        this.category = category;
    }
    
    public static final List<ParasiteRegistry> PARASITES = Arrays.asList(
            new ParasiteRegistry("BanoAdapted", "banoa", "adapted"),
            new ParasiteRegistry("EmanaAdapted", "emanaa", "adapted"),
            new ParasiteRegistry("HullAdapted", "hulla", "adapted"),
            new ParasiteRegistry("RanracAdapted", "ranraca", "adapted"),
            new ParasiteRegistry("RanracAdapted", "ranraca", "adapted"),
            new ParasiteRegistry("ShycoAdapted", "shycoa", "adapted"),
            new ParasiteRegistry("Crux", "cruxa", "crude"),
            new ParasiteRegistry("Heed", "heed", "crude"),
            new ParasiteRegistry("Host", "host", "crude"),
            new ParasiteRegistry("HostII", "hostii", "crude"),
            new ParasiteRegistry("InhooS", "inhoos", "crude"),
            new ParasiteRegistry("Mes", "mes", "crude"),
            new ParasiteRegistry("Nak", "nak", "deterrent"),
            new ParasiteRegistry("Venkrol", "venkrol", "deterrent.nexus"),
            new ParasiteRegistry("FerEnderman", "ferenderman", "feral"),
            new ParasiteRegistry("Ata", "gnat", "inborn"),
            new ParasiteRegistry("Lesh", "lesh", "inborn"),
            new ParasiteRegistry("Lodo", "lodo", "inborn"),
            new ParasiteRegistry("Mudo", "mudo", "inborn"),
            new ParasiteRegistry("Nuuh", "nuuh", "inborn"),
            new ParasiteRegistry("InfCow", "cow", "infected"),
            new ParasiteRegistry("InfDragonE", "infdragone", "infected"),
            new ParasiteRegistry("InfEnderman", "infenderman", "infected"),
            new ParasiteRegistry("InfHorse", "infhorse", "infected"),
            new ParasiteRegistry("InfHuman", "human", "infected"),
            new ParasiteRegistry("InfPlayer", "infplayer", "infected"),
            new ParasiteRegistry("InfSquid", "squid", "infected"),
            new ParasiteRegistry("InfWolf", "wolf", "infected"),
            new ParasiteRegistry("Bano", "bano", "primitive"),
            new ParasiteRegistry("Canra", "canra", "primitive"),
            new ParasiteRegistry("Hull", "hull", "primitive"),
            new ParasiteRegistry("Nogla", "nogla", "primitive"),
            new ParasiteRegistry("Ranrac", "ranrac", "primitive"),
            new ParasiteRegistry("Shyco", "shyco", "primitive"),
            new ParasiteRegistry("Iki", "vermin", "primitive"),
            new ParasiteRegistry("Wymo", "wymo", "primitive"),
            new ParasiteRegistry("Alafha", "alafha","pure"),
            new ParasiteRegistry("Esor", "esor", "pure"),
            new ParasiteRegistry("Flog", "flog", "pure"),
            new ParasiteRegistry("Ganro", "ganro", "pure"),
            new ParasiteRegistry("Orch", "orch", "pure"),
            new ParasiteRegistry("Pheon", "pheon", "pure.preeminent")
    );
    
    public static final Map<Class<?>, ProjectileLauncher> RANGED_PARASITES = new HashMap<>();
    
    static {
        RANGED_PARASITES.put(EntityAnged.class, new ProjectileLauncher(EntityProjectileAngedball.class, SRPSounds.EMANA_SHOOTING, 50L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityDorpa.class, new ProjectileLauncher(EntityProjectileWebball.class, SRPSounds.DORPA_RANGE, 50L, 2.0F, 1.0F));
        RANGED_PARASITES.put(EntityInfDragonE.class, new ProjectileLauncher(EntityProjectileDragonE.class, SoundEvents.ENTITY_BLAZE_SHOOT, 300L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityEmanaAdapted.class, new ProjectileLauncher(EntityProjectileSpineball.class, SRPSounds.EMANA_SHOOTING, 200L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityEmana.class, new ProjectileLauncher(EntityProjectileSpineball.class, SRPSounds.EMANA_SHOOTING, 200L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityUnvo.class, new ProjectileLauncher(EntityProjectileSpineball.class, SRPSounds.EMANA_SHOOTING, 50L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityAlafha.class, new ProjectileLauncher(EntityProjectileAlafhaBall.class, SRPSounds.ALAFHA_SHOOTING, 250L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityOrch.class, new ProjectileLauncher(EntityProjectileWebball.class, SRPSounds.DORPA_RANGE, 50L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityLencia.class, new ProjectileLauncher(EntityProjectileLenciaBall.class, SRPSounds.DORPA_RANGE, 500L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityElvia.class, new ProjectileLauncher(EntityProjectileLenciaBall.class, SRPSounds.DORPA_RANGE, 500L,2.0F, 1.0F));
        RANGED_PARASITES.put(EntityOronco.class, new ProjectileLauncher(EntityProjectileLenciaBall.class, SRPSounds.ORONCO_SHOOTING, 250L,2.0F, 1.0F));
    }
}