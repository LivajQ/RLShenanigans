package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.*;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.*;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.*;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.*;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityGothol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.*;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.*;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.*;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;

import java.util.Arrays;
import java.util.List;

public class ParasiteEvolutionRegistry {
    
    public final Class<? extends EntityParasiteBase> inferiorClassName;
    public final Class<? extends EntityParasiteBase> superiorClassName;
    
    public ParasiteEvolutionRegistry(Class<? extends EntityParasiteBase> inferiorClassName, Class<? extends EntityParasiteBase> superiorClassName) {
        this.inferiorClassName = inferiorClassName;
        this.superiorClassName = superiorClassName;
    }
    
    public static final List<ParasiteEvolutionRegistry> EVOLUTIONS = Arrays.asList(
            new ParasiteEvolutionRegistry(EntityBano.class, EntityBanoAdapted.class),
            new ParasiteEvolutionRegistry(EntityCanra.class, EntityCanraAdapted.class),
            new ParasiteEvolutionRegistry(EntityEmana.class, EntityEmanaAdapted.class),
            new ParasiteEvolutionRegistry(EntityHull.class, EntityHullAdapted.class),
            new ParasiteEvolutionRegistry(EntityNogla.class, EntityNoglaAdapted.class),
            new ParasiteEvolutionRegistry(EntityRanrac.class, EntityRanracAdapted.class),
            new ParasiteEvolutionRegistry(EntityShyco.class, EntityShycoAdapted.class),
            new ParasiteEvolutionRegistry(EntityPheon.class, EntityTerla.class),
            new ParasiteEvolutionRegistry(EntityInfPlayer.class, EntityMes.class),
            new ParasiteEvolutionRegistry(EntityInhooS.class, EntityInhooM.class),
            new ParasiteEvolutionRegistry(EntityDod.class, EntityDodSII.class),
            new ParasiteEvolutionRegistry(EntityDodSII.class, EntityDodSIII.class),
            new ParasiteEvolutionRegistry(EntityDodSIII.class, EntityDodSIV.class),
            new ParasiteEvolutionRegistry(EntityVenkrol.class, EntityVenkrolSII.class),
            new ParasiteEvolutionRegistry(EntityVenkrolSII.class, EntityVenkrolSIII.class),
            new ParasiteEvolutionRegistry(EntityVenkrolSIII.class, EntityVenkrolSIV.class),
            new ParasiteEvolutionRegistry(EntityInfBear.class, EntityFerBear.class),
            new ParasiteEvolutionRegistry(EntityInfCow.class, EntityFerCow.class),
            new ParasiteEvolutionRegistry(EntityInfEnderman.class, EntityFerEnderman.class),
            new ParasiteEvolutionRegistry(EntityInfHorse.class, EntityFerHorse.class),
            new ParasiteEvolutionRegistry(EntityInfHuman.class, EntityFerHuman.class),
            new ParasiteEvolutionRegistry(EntityInfPig.class, EntityFerPig.class),
            new ParasiteEvolutionRegistry(EntityInfSheep.class, EntityFerSheep.class),
            new ParasiteEvolutionRegistry(EntityInfVillager.class, EntityFerVillager.class),
            new ParasiteEvolutionRegistry(EntityHiGolem.class, EntityCrux.class),
            new ParasiteEvolutionRegistry(EntityMudo.class, EntityNuuh.class),
            new ParasiteEvolutionRegistry(EntityGothol.class, EntityRathol.class),
            new ParasiteEvolutionRegistry(EntityDorpa.class, EntityOrch.class),
            new ParasiteEvolutionRegistry(EntityIki.class, EntityAlafha.class),
            new ParasiteEvolutionRegistry(EntityOmboo.class, EntityJinjo.class),
            new ParasiteEvolutionRegistry(EntityFlog.class, EntityEsor.class),
            new ParasiteEvolutionRegistry(EntityHeed.class, EntityVesta.class)
    );

}