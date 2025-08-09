package rlshenanigans.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ParasiteSpeech {
    private static final Random rand = new Random();
    
    private static final List<String> baseQuotes = Arrays.asList(
            "Hello Pookie (๑>◡<๑)",
            "UWU",
            "Can we cuddle?",
            "Feeling freaky",
            "I made this goo... just for you",
            "*purrs*",
            "Golem armor bad",
            "Can we go kill something?",
            "Your skin is so squishy. Like, best squishy. Top-tier squishy (っ˘ω˘ς )",
            "Nice armor. Can I break it?",
            "Evasion bad",
            "You’re my favorite host. Don’t tell the others",
            "Can we hold hands while tearing everything apart?",
            "Can you hear it? It's the call of the thighs",
            "That village looked peaceful. Let’s fix that",
            "Iframes? What iframes?",
            "You should try some beckon biomass, top tier yummy",
            "It's assimilatin' time"
    );
    
    public static String getRandomQuote() {
        return baseQuotes.get(rand.nextInt(baseQuotes.size()));
    }
}