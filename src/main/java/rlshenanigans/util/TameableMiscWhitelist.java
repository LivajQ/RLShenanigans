package rlshenanigans.util;

import java.util.ArrayList;
import java.util.List;

/*
    A list builder for non-parasites and non-lycanites that can be tamed. More of
    a "vanilla dog taming" system than anything convoluted, but some people
    really wanted those sea serpents.
*/
public class TameableMiscWhitelist {
    
    private static final List<Entry> entries = new ArrayList<>();
    public static class Entry {
        public final String mobId;
        public final String itemId;
        public final int metadata;
        
        public Entry(String mobId, String itemId, int metadata) {
            this.mobId = mobId;
            this.itemId = itemId;
            this.metadata = metadata;
        }
    }
    
    public static void load(String[] rawEntries) {
        entries.clear();
        
        for (String line : rawEntries) {
            if (line.trim().isEmpty()) continue;
            
            String[] parts = line.split(";");
            if (parts.length != 3) continue;
            
            String mobId = parts[0].trim();
            String itemId = parts[1].trim();
            int meta;
            
            try {
                meta = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException e) {
                meta = 0;
            }
            
            entries.add(new Entry(mobId, itemId, meta));
        }
    }
    
    public static List<Entry> getEntries() {
        return entries;
    }
}
