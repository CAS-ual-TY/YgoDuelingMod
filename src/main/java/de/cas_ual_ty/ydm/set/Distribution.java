package de.cas_ual_ty.ydm.set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.set.Distribution.Pull.PullEntry;
import de.cas_ual_ty.ydm.util.JsonKeys;

public class Distribution
{
    public final String name;
    public final Pull[] pulls;
    
    public final int totalWeight;
    
    public Distribution(JsonObject j)
    {
        this.name = j.get(JsonKeys.NAME).getAsString();
        
        int q, r, s;
        
        JsonArray pullsJson = j.get(JsonKeys.PULLS).getAsJsonArray();
        this.pulls = new Pull[pullsJson.size()];
        
        JsonObject pullJson;
        
        int weight;
        int totalWeight = 0;
        JsonArray entriesJson;
        PullEntry[] entries;
        
        JsonObject entryJson;
        int count;
        
        JsonArray raritiesJson;
        String[] rarities;
        
        for(q = 0; q < this.pulls.length; ++q)
        {
            pullJson = pullsJson.get(q).getAsJsonObject();
            
            weight = pullJson.get(JsonKeys.WEIGHT).getAsInt();
            entriesJson = pullJson.get(JsonKeys.ENTRIES).getAsJsonArray();
            entries = new PullEntry[entriesJson.size()];
            
            for(r = 0; r < entries.length; ++r)
            {
                entryJson = entriesJson.get(r).getAsJsonObject();
                
                count = entryJson.get(JsonKeys.COUNT).getAsInt();
                raritiesJson = entryJson.get(JsonKeys.RARITIES).getAsJsonArray();
                
                rarities = new String[raritiesJson.size()];
                for(s = 0; s < rarities.length; ++s)
                {
                    rarities[s] = raritiesJson.get(s).getAsString();
                }
                
                entries[r] = new PullEntry(count, rarities);
            }
            
            this.pulls[q] = new Pull(weight, entries);
            totalWeight += weight;
        }
        
        this.totalWeight = totalWeight;
    }
    
    public static class Pull
    {
        public final int weight;
        public final PullEntry[] pullEntries;
        
        public Pull(int weight, PullEntry[] pullEntries)
        {
            this.weight = weight;
            this.pullEntries = pullEntries;
        }
        
        public static class PullEntry
        {
            public final int count;
            public final String[] rarities;
            
            public PullEntry(int count, String[] rarities)
            {
                this.count = count;
                this.rarities = rarities;
            }
        }
    }
}
