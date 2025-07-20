package rlshenanigans.handlers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rlshenanigans.RLShenanigans;

public class RLSSoundHandler{
    public static SoundEvent DRJR_AMBIENT;
    
    public static void init(){
        DRJR_AMBIENT = register("drjr_ambient");
    }
    
    private static SoundEvent register(String name) {
        ResourceLocation location = new ResourceLocation(RLShenanigans.MODID, name);
        SoundEvent sound = new SoundEvent(location);
        sound.setRegistryName(location);
        ForgeRegistries.SOUND_EVENTS.register(sound);
        return sound;
    }
}