package rlshenanigans.action;

public enum ParasiteCommand {
    FOLLOW("Makes your §dPookie §ffollow you"),
    ROAM("Your §dPookie §fwill roam around the area"),
    GIVEITEM("Give your §dPookie an item to wear"),
    RIDE("Ride your §dPookie"),
    RIDEREVERSE("Have your §dPookie §fride YOU"),
    RESIZE("Resize your §dPookie"),
    SMOOCH("Kissy kissy"),
    ASKFORDROP("Ask your §dPookie §ffor items"),
    SELF_DESTRUCT("Trigger obliteration of everything and anything"),
    DESPAWN("Despawn your §dPookie");
    
    private final String description;
    
    ParasiteCommand(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}