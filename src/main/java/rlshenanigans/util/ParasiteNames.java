package rlshenanigans.util;

import java.util.Random;

public class ParasiteNames {
    private static final String[] NAMES = {
            "Wriggler", "Swarmlet", "GooBoy", "Throbbit", "Mushmeat",
            "Pookie", "Glorp", "Scuttlez", "Meatkiss", "Boil Baby",
            "Dribble", "Murmur", "Grin Maw", "Sniblet", "Cysten"
    };
    
    public static String getRandomName(Random rand) {
        return NAMES[rand.nextInt(NAMES.length)];
    }
}