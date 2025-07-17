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
    public final String category;
    
    public ParasiteRegistry(String name, String category)
    {
        this.name = name;
        this.category = category;
    }
    
    
    public static final List<ParasiteRegistry> PARASITES = Arrays.asList(
            new ParasiteRegistry("BanoAdapted", "adapted"),
            new ParasiteRegistry("CanraAdapted", "adapted"),
            new ParasiteRegistry("EmanaAdapted", "adapted"),
            new ParasiteRegistry("HullAdapted", "adapted"),
            new ParasiteRegistry("NoglaAdapted", "adapted"),
            new ParasiteRegistry("RanracAdapted", "adapted"),
            new ParasiteRegistry("ShycoAdapted", "adapted"),
            new ParasiteRegistry("Oronco", "ancient"),
            new ParasiteRegistry("OroncoTen", "ancient"),
            new ParasiteRegistry("Terla", "ancient"),
            new ParasiteRegistry("OroncoAW", "awakened"),
            new ParasiteRegistry("OroncoAWFL", "awakened"),
            new ParasiteRegistry("CruxA", "crude"),
            new ParasiteRegistry("Heed", "crude"),
            new ParasiteRegistry("Host", "crude"),
            new ParasiteRegistry("HostII", "crude"),
            new ParasiteRegistry("InhooM", "crude"),
            new ParasiteRegistry("InhooS", "crude"),
            new ParasiteRegistry("Mes", "crude"),
            new ParasiteRegistry("Heblu", "derived"),
            new ParasiteRegistry("Dod", "deterrent.nexus"),
            new ParasiteRegistry("DodSII", "deterrent.nexus"),
            new ParasiteRegistry("DodSIII", "deterrent.nexus"),
            new ParasiteRegistry("DodSIV", "deterrent.nexus"),
            new ParasiteRegistry("DodSIVH", "deterrent.nexus"),
            new ParasiteRegistry("Leem", "deterrent.nexus"),
            new ParasiteRegistry("LeemSII", "deterrent.nexus"),
            new ParasiteRegistry("LeemSIII", "deterrent.nexus"),
            new ParasiteRegistry("LeemSIV", "deterrent.nexus"),
            new ParasiteRegistry("Venkrol", "deterrent.nexus"),
            new ParasiteRegistry("VenkrolSII", "deterrent.nexus"),
            new ParasiteRegistry("VenkrolSIII", "deterrent.nexus"),
            new ParasiteRegistry("VenkrolSIV", "deterrent.nexus"),
            new ParasiteRegistry("VenkrolSV", "deterrent.nexus"),
            new ParasiteRegistry("DodT", "deterrent"),
            new ParasiteRegistry("LeemB", "deterrent"),
            new ParasiteRegistry("Nak", "deterrent"),
            new ParasiteRegistry("Rof", "deterrent"),
            new ParasiteRegistry("Tonro", "deterrent"),
            new ParasiteRegistry("Unvo", "deterrent"),
            new ParasiteRegistry("FerBear", "feral"),
            new ParasiteRegistry("FerCow", "feral"),
            new ParasiteRegistry("FerEnderman", "feral"),
            new ParasiteRegistry("FerHorse", "feral"),
            new ParasiteRegistry("FerHuman", "feral"),
            new ParasiteRegistry("FerPig", "feral"),
            new ParasiteRegistry("FerSheep", "feral"),
            new ParasiteRegistry("FerVillager", "feral"),
            new ParasiteRegistry("HiGolem", "hijacked"),
            new ParasiteRegistry("Ata", "inborn"),
            new ParasiteRegistry("Buthol", "inborn"),
            new ParasiteRegistry("Gothol", "inborn"),
            new ParasiteRegistry("Kol", "inborn"),
            new ParasiteRegistry("Lesh", "inborn"),
            new ParasiteRegistry("Lodo", "inborn"),
            new ParasiteRegistry("Mor", "inborn"),
            new ParasiteRegistry("Mudo", "inborn"),
            new ParasiteRegistry("Nuuh", "inborn"),
            new ParasiteRegistry("Rathol", "inborn"),
            new ParasiteRegistry("Dorpa", "infected"),
            new ParasiteRegistry("InfBear", "infected"),
            new ParasiteRegistry("InfCow", "infected"),
            new ParasiteRegistry("InfDragonE", "infected"),
            new ParasiteRegistry("InfEnderman", "infected"),
            new ParasiteRegistry("InfHorse", "infected"),
            new ParasiteRegistry("InfHuman", "infected"),
            new ParasiteRegistry("InfPig", "infected"),
            new ParasiteRegistry("InfPlayer", "infected"),
            new ParasiteRegistry("InfSheep", "infected"),
            new ParasiteRegistry("InfSquid", "infected"),
            new ParasiteRegistry("InfVillager", "infected"),
            new ParasiteRegistry("InfWolf", "infected"),
            new ParasiteRegistry("Bano", "primitive"),
            new ParasiteRegistry("Canra", "primitive"),
            new ParasiteRegistry("Emana", "primitive"),
            new ParasiteRegistry("Hull", "primitive"),
            new ParasiteRegistry("Iki", "primitive"),
            new ParasiteRegistry("Lum", "primitive"),
            new ParasiteRegistry("Nogla", "primitive"),
            new ParasiteRegistry("Ranrac", "primitive"),
            new ParasiteRegistry("Shyco", "primitive"),
            new ParasiteRegistry("Wymo", "primitive"),
            new ParasiteRegistry("Elvia", "pure.preeminent"),
            new ParasiteRegistry("Flam", "pure.preeminent"),
            new ParasiteRegistry("Jinjo", "pure.preeminent"),
            new ParasiteRegistry("Lencia", "pure.preeminent"),
            new ParasiteRegistry("Pheon", "pure.preeminent"),
            new ParasiteRegistry("Vesta", "pure.preeminent"),
            new ParasiteRegistry("Alafha", "pure"),
            new ParasiteRegistry("Anged", "pure"),
            new ParasiteRegistry("Esor", "pure"),
            new ParasiteRegistry("Flog", "pure"),
            new ParasiteRegistry("Ganro", "pure"),
            new ParasiteRegistry("Omboo", "pure"),
            new ParasiteRegistry("Orch", "pure")
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