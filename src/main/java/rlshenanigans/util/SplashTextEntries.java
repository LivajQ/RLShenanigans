package rlshenanigans.util;

import static rlshenanigans.RLShenanigans.RLSRAND;

public class SplashTextEntries {
    private static final String[] SPLASHES = {
            "§d§oCall of the Thighs!",
            "§d§oToo much freaky!",
            "§d§oFun time awaits!",
            "§d§oRemember to pet the Reeker!",
            "§d§oThe more thighs the merrier!",
            "§d§oJoin the Hive!",
            "§d§oSubscribe to our OnlyHives!"
    };
    
    public static String getRandomSplash() {
        return SPLASHES[RLSRAND.nextInt(SPLASHES.length)];
    }
}