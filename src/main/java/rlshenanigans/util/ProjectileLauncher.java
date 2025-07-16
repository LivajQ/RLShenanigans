package rlshenanigans.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;

public class ProjectileLauncher {
    public final Class<? extends Entity> projectileClass;
    public final SoundEvent sound;
    public final long cooldown;
    public final float volume;
    public final float pitch;
    
    public ProjectileLauncher(Class<? extends Entity> projectileClass, SoundEvent sound, long cooldown, float volume, float pitch) {
        this.projectileClass = projectileClass;
        this.sound = sound;
        this.cooldown = cooldown;
        this.volume = volume;
        this.pitch = pitch;
    }
}