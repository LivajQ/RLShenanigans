package rlshenanigans.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ParasiteSpeech {
    private static final Random rand = new Random();
    
    public enum QuoteType {
        BASE,
        KILL,
        WOUNDED
    }
    
    private static final List<String> baseQuotes = Arrays.asList(
            "Hello Pookie :3",
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
    
    private static final List<String> killQuotes = Arrays.asList(
            "Headshot, baby",
            "One tapped",
            "What a freekill",
            "Too easy",
            "Noscope",
            "Sent back to the lobby"
    );
    
    private static final List<String> woundedQuotes = Arrays.asList(
            "A little help, please?",
            "Not feeling too well, my pookie...",
            "Got some spare heals?",
            "I need healing"
    );
    
    public static String getRandomQuote(QuoteType type) {
        List<String> selectedList = null;
        
        switch (type) {
            case BASE:
                selectedList = baseQuotes;
                break;
            case KILL:
                selectedList = killQuotes;
                break;
            case WOUNDED:
                selectedList = woundedQuotes;
                break;
        }
        
        if (selectedList == null) return "";
        return selectedList.get(rand.nextInt(selectedList.size()));
    }
}