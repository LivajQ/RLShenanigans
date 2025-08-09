package rlshenanigans.util;

import java.util.Random;

public class ParasiteNames {
    private static final String[] NAMES = {
            "Wriggler", "Swarmlet", "GooBoy", "Throbbit", "Mushmeat",
            "Pookie", "Glorp", "Scuttlez", "Meatkiss", "Boil Baby",
            "Dribble", "Murmur", "Grin Maw", "Sniblet", "Cysten",
            "Slopjaw", "Nubbin", "Spindle", "Festerling", "Grubnub",
            "Squelch", "Mawble", "Blisterkin", "Twitchlet", "Greeble",
            "Snoggle", "Wretchie", "Mucklet", "Spasmite", "Droolbit",
            "Glimp", "Nuzzlegut", "Cringler", "Slurmkin", "Gibberbug"
    };
    public static String getRandomName(Random rand) {
        return NAMES[rand.nextInt(NAMES.length)];
    }
}