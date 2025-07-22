package rlshenanigans.util;

import java.util.UUID;

public class ParasiteBrief {
    public final UUID mobUUID;
    public final String name;
    public final String strainId;
    public final int skin;
    
    public ParasiteBrief(TamedParasiteInfo info) {
        this.mobUUID = info.mobUUID;
        this.name = info.name;
        this.strainId = info.strainId;
        this.skin = info.skin;
    }
    
    public ParasiteBrief(UUID mobUUID, String name, String className, int skin) {
        this.mobUUID = mobUUID;
        this.name = name;
        this.strainId = className;
        this.skin = skin;
    }
}