package rlshenanigans.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.*;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.*;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.*;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.*;

import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.*;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.*;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.*;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.*;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.*;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.*;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import net.minecraft.entity.EntityLivingBase;

import java.util.*;

public class ParasiteDeathMessages {
    public enum ParasiteTrait {
        LARGE, THIGHS, FLYING, GENERIC, HUGGER, DOGGO, GAPING, SMALL, BOOMER, SPIDER
    }
    
    private static final Map<ParasiteTrait, List<String>> DEATH_MESSAGES = new HashMap<>();
    
    static {
        DEATH_MESSAGES.put(ParasiteTrait.LARGE, Arrays.asList(
                "{{victim}} was stepped on by {{parasite}}",
                "{{victim}} got squished like a bug by {{parasite}}",
                "{{victim}} was flattened by {{parasite}}"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.FLYING, Arrays.asList(
                "{{victim}} experienced aerial assault thanks to {{parasite}}",
                "{{victim}} took a flight to heaven with {{parasite}}",
                "{{parasite}} dive-bombed {{victim}} into oblivion",
                "{{victim}} got caught in a mid-air tango with {{parasite}}"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.THIGHS, Arrays.asList(
                "{{victim}} suffocated in {{parasite}} thighs",
                "{{victim}} got too comfortable in {{parasite}} thighs",
                "{{parasite}} weaponized leg day against {{victim}}",
                "{{victim}} underestimated the gravitational pull of {{parasite}} thighs"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.GENERIC, Arrays.asList(
                "{{victim}} got clapped by {{parasite}}",
                "{{parasite}} took a bit too much liking to {{victim}}",
                "{{victim}} was punched into assimilation by {{parasite}}",
                "{{victim}} submitted to {{parasite}}",
                "{{parasite}} whispered sweet nothings before ending {{victim}}",
                "{{victim}} was casually erased by {{parasite}}"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.HUGGER, Arrays.asList(
                "{{victim}} was hugged too hard by {{parasite}}",
                "{{victim}} ended up in {{parasite}} embrace",
                "{{parasite}} offered {{victim}} a friendly hug",
                "{{parasite}} gently hugged {{victim}} out of existence",
                "{{parasite}} snuggled {{victim}} into the afterlife"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.DOGGO, Arrays.asList(
                "{{victim}} was licked to death by {{parasite}}",
                "{{parasite}} got too excited while playing with {{victim}}",
                "{{parasite}} fetched a bone. The bone from {{victim}} body",
                "{{parasite}} mistook {{victim}} for a chew toy"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.GAPING, Arrays.asList(
                "{{victim}} was eaten by {{parasite}}",
                "{{victim}} learned the definition of vore with {{parasite}}",
                "{{victim}} became {{parasite}} snack",
                "{{victim}} took a one-way trip into {{parasite}} mouth"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.SMALL, Arrays.asList(
                "{{victim}} was tickled to death by {{parasite}}",
                "{{victim}} tripped on {{parasite}} and died",
                "{{parasite}} cuteness was too much for {{victim}} to handle",
                "{{victim}} was nibbled into nonexistence by {{parasite}}"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.BOOMER, Arrays.asList(
                "{{victim}} was blown (up) by {{parasite}}",
                "{{victim}} went boom because of {{parasite}}",
                "{{parasite}} exploded with love for {{victim}}",
                "{{victim}} mistook {{parasite}} for a firecracker"
        ));
        
        DEATH_MESSAGES.put(ParasiteTrait.SPIDER, Arrays.asList(
                "{{victim}} and {{parasite}} had a beautiful webbing",
                "{{victim}} ended in {{parasite}} web",
                "{{parasite}} spun {{victim}} into a bedtime cocoon",
                "{{victim}} got tangled in {{parasite}} silk"
        ));
    }
    
    private static final Map<Class<? extends EntityParasiteBase>, Set<ParasiteTrait>> TRAIT_REGISTRY = new HashMap<>();
    
    static {
        TRAIT_REGISTRY.put(EntityDorpa.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.SPIDER));
        TRAIT_REGISTRY.put(EntityInfSquid.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfBear.class, EnumSet.of(ParasiteTrait.GENERIC, ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityFerBear.class, EnumSet.of(ParasiteTrait.GENERIC, ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityInfHuman.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityFerHuman.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfHumanHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfEnderman.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityFerEnderman.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityInfEndermanHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfCow.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityFerCow.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfCowHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfSheep.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityFerSheep.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfSheepHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfWolf.class, EnumSet.of(ParasiteTrait.SMALL, ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityInfWolfHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfPig.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityFerPig.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfPigHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfVillager.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityFerVillager.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfVillagerHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfHorse.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityFerHorse.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfHorseHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfPlayer.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityInfPlayerHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInfDragonE.class, EnumSet.of(ParasiteTrait.LARGE, ParasiteTrait.FLYING));
        TRAIT_REGISTRY.put(EntityInfDragonEHead.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInhooS.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityInhooM.class, EnumSet.of(ParasiteTrait.GENERIC, ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityHiGolem.class, EnumSet.of(ParasiteTrait.GENERIC, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityHost.class, EnumSet.of(ParasiteTrait.GENERIC, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityHostII.class, EnumSet.of(ParasiteTrait.GENERIC, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityHeed.class, EnumSet.of(ParasiteTrait.DOGGO, ParasiteTrait.GAPING));
        TRAIT_REGISTRY.put(EntityCrux.class, EnumSet.of(ParasiteTrait.LARGE, ParasiteTrait.HUGGER, ParasiteTrait.GAPING));
        TRAIT_REGISTRY.put(EntityMes.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityLesh.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityShyco.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.GAPING, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityShycoAdapted.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.GAPING, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityCanra.class, EnumSet.of(ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityCanraAdapted.class, EnumSet.of(ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityNogla.class, EnumSet.of(ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityNoglaAdapted.class, EnumSet.of(ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityHull.class, EnumSet.of(ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityHullAdapted.class, EnumSet.of(ParasiteTrait.DOGGO));
        TRAIT_REGISTRY.put(EntityEmana.class, EnumSet.of(ParasiteTrait.FLYING));
        TRAIT_REGISTRY.put(EntityEmanaAdapted.class, EnumSet.of(ParasiteTrait.FLYING, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityBano.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityBanoAdapted.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.GAPING));
        TRAIT_REGISTRY.put(EntityWymo.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityIki.class, EnumSet.of(ParasiteTrait.FLYING));
        TRAIT_REGISTRY.put(EntityRanrac.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.DOGGO, ParasiteTrait.SPIDER));
        TRAIT_REGISTRY.put(EntityRanracAdapted.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.DOGGO, ParasiteTrait.SPIDER));
        TRAIT_REGISTRY.put(EntityLodo.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityMudo.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityNuuh.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityAta.class, EnumSet.of(ParasiteTrait.SMALL));
        TRAIT_REGISTRY.put(EntityRathol.class, EnumSet.of(ParasiteTrait.BOOMER));
        TRAIT_REGISTRY.put(EntityGothol.class, EnumSet.of(ParasiteTrait.BOOMER));
        TRAIT_REGISTRY.put(EntityButhol.class, EnumSet.of(ParasiteTrait.BOOMER));
        TRAIT_REGISTRY.put(EntityFlam.class, EnumSet.of(ParasiteTrait.BOOMER));
        TRAIT_REGISTRY.put(EntityAnged.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.SPIDER));
        TRAIT_REGISTRY.put(EntityVenkrol.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityVenkrolSII.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityVenkrolSIII.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityVenkrolSIV.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityTonro.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityUnvo.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityNak.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityDod.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityDodSII.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityDodSIII.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityDodSIV.class, EnumSet.of(ParasiteTrait.GENERIC));
        TRAIT_REGISTRY.put(EntityAlafha.class, EnumSet.of(ParasiteTrait.FLYING));
        TRAIT_REGISTRY.put(EntityGanro.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.HUGGER, ParasiteTrait.GAPING));
        TRAIT_REGISTRY.put(EntityOmboo.class, EnumSet.of(ParasiteTrait.FLYING));
        TRAIT_REGISTRY.put(EntityEsor.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityOrch.class, EnumSet.of(ParasiteTrait.HUGGER, ParasiteTrait.SPIDER));
        TRAIT_REGISTRY.put(EntityFlog.class, EnumSet.of(ParasiteTrait.THIGHS, ParasiteTrait.HUGGER));
        TRAIT_REGISTRY.put(EntityJinjo.class, EnumSet.of(ParasiteTrait.FLYING));
        TRAIT_REGISTRY.put(EntityPheon.class, EnumSet.of(ParasiteTrait.LARGE));
        TRAIT_REGISTRY.put(EntityVesta.class, EnumSet.of(ParasiteTrait.LARGE, ParasiteTrait.HUGGER, ParasiteTrait.SPIDER));
        TRAIT_REGISTRY.put(EntityLencia.class, EnumSet.of(ParasiteTrait.FLYING, ParasiteTrait.BOOMER));
        TRAIT_REGISTRY.put(EntityElvia.class, EnumSet.of(ParasiteTrait.FLYING, ParasiteTrait.BOOMER));
        TRAIT_REGISTRY.put(EntityOronco.class, EnumSet.of(ParasiteTrait.FLYING));
        TRAIT_REGISTRY.put(EntityTerla.class, EnumSet.of(ParasiteTrait.LARGE));
        TRAIT_REGISTRY.put(EntityDropPod.class, EnumSet.of(ParasiteTrait.GENERIC));
    }
    
    public static String getParasiteDeathMessage(EntityLivingBase victim, EntityParasiteBase killer) {
        Set<ParasiteTrait> traits = TRAIT_REGISTRY.getOrDefault(killer.getClass(), EnumSet.noneOf(ParasiteTrait.class));
        if (traits.isEmpty()) return victim.getName() + " decided to die";
        
        ParasiteTrait randomTrait = traits.stream()
                .skip(new Random().nextInt(traits.size()))
                .findFirst()
                .orElse(ParasiteTrait.GENERIC);
        
        List<String> messages = DEATH_MESSAGES.getOrDefault(randomTrait, Collections.singletonList("{{victim}} died mysteriously"));
        String rawMessage = messages.get(new Random().nextInt(messages.size()));
        
        String parasiteName = killer.getName();
        String victimName = victim.getName();
        
        return rawMessage
                .replace("{{parasite}}", parasiteName)
                .replace("{{victim}}", victimName);
    }
}