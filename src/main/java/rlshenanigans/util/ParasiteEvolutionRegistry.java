package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;

import java.util.Arrays;
import java.util.List;

public class ParasiteEvolutionRegistry {
    
    public final Class<?> inferiorClassName;
    public final Class<?> superiorClassName;
    
    public ParasiteEvolutionRegistry(Class<?> inferiorClassName, Class<?> superiorClassName) {
        this.inferiorClassName = inferiorClassName;
        this.superiorClassName = superiorClassName;
    }
    
    public static final List<ParasiteEvolutionRegistry> EVOLUTIONS = Arrays.asList(
            new ParasiteEvolutionRegistry(EntityBano.class, EntityBanoAdapted.class)

    );

}