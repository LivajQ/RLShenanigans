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
    
    public static void init(){
        DRJR_AMBIENT = register("drjr_ambient");
        DISC_LAVACHICKEN = register("musicdisc_lavachicken");
        PHANTOM_SPAWN = register("phantom_spawn");
        BLEED_BUILDUP = register("bleedbuildup");
    }
    
    private static SoundEvent register(String name) {
        ResourceLocation location = new ResourceLocation(RLShenanigans.MODID, name);
        SoundEvent sound = new SoundEvent(location);
        sound.setRegistryName(location);
        ForgeRegistries.SOUND_EVENTS.register(sound);
        return sound;
    }
}