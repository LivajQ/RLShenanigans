package rlshenanigans.handlers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rlshenanigans.RLShenanigans;

public class RLSSoundHandler{
    public static SoundEvent DRJR_AMBIENT;
    public static SoundEvent DISC_LAVACHICKEN;
    public static SoundEvent PHANTOM_SPAWN;
    public static SoundEvent BLEED_BUILDUP;
    public static SoundEvent SPELL_HEAL;
    public static SoundEvent SPELL_INVULNERABILITY;
    public static SoundEvent SPELL_RAY_OF_FROST;
    
    public static void init(){
        DRJR_AMBIENT = register("drjr_ambient");
        DISC_LAVACHICKEN = register("musicdisc_lavachicken");
        PHANTOM_SPAWN = register("phantom_spawn");
        BLEED_BUILDUP = register("bleedbuildup");
        SPELL_HEAL = register("spell_heal");
        SPELL_INVULNERABILITY = register("spell_invulnerability");
        SPELL_RAY_OF_FROST = register("spell_ray_of_frost");
    }
    
    private static SoundEvent register(String name) {
        ResourceLocation location = new ResourceLocation(RLShenanigans.MODID, name);
        SoundEvent sound = new SoundEvent(location);
        sound.setRegistryName(location);
        ForgeRegistries.SOUND_EVENTS.register(sound);
        return sound;
    }
}