package rlshenanigans.util;

import java.util.Random;

public class SplashTextEntries {
    private static final Random RAND = new Random();
    private static final String[] SPLASHES = {
            "§d§oCall of the Thighs!",
            "§d§oToo much freaky!",
            "§d§oFun time awaits!",
            "§d§oRemember to pet the Reeker!",
            "§d§oThe more thighs the merrier!",
            "§d§oJoin the Hive!"
    };
    
    public static String getRandomSplash() {
        return SPLASHES[RAND.nextInt(SPLASHES.length)];
    }
}