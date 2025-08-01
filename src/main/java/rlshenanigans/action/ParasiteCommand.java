package rlshenanigans.action;

public enum ParasiteCommand {
    FOLLOW("Makes your §dPookie §ffollow you"),
    ROAM("Your §dPookie §fwill roam around the area"),
    RIDE("Ride your §dPookie"),
    RIDEREVERSE("Have your §dPookie ride YOU"),
    RESIZE("Resize your §dPookie"),
    SMOOCH("Kissy kissy"),
    ASKFORDROP("Ask your §dPookie §f for items"),
    SELF_DESTRUCT("Trigger obliteration of everything and anything");
    
    private final String description;
    
    ParasiteCommand(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}