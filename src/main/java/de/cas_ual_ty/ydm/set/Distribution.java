package de.cas_ual_ty.ydm.set;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.set.Distribution.Pull.PullEntry;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

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
    
    public void addInformation(List<ITextComponent> tooltip, CardSet set)
    {
        for(Pull pull : this.pulls)
        {
            tooltip.add(new StringTextComponent("Odds:  " + Distribution.makeOddsString(pull.weight, this.totalWeight)));
            
            for(PullEntry pe : pull.pullEntries)
            {
                StringBuilder s = new StringBuilder();
                s.append("    " + pe.count + "x: ");
                
                boolean rarityFound = false;
                
                for(String rarity : pe.rarities)
                {
                    if(set.rarityPool.contains(rarity))
                    {
                        s.append(rarity + " / ");
                        
                        if(!rarityFound)
                        {
                            rarityFound = true;
                        }
                    }
                }
                
                if(rarityFound)
                {
                    s.delete(s.length() - 3, s.length());
                }
                else
                {
                    s.append(TextFormatting.RED.toString() + "NO MATCH");
                }
                
                tooltip.add(new StringTextComponent(s.toString()));
            }
            
            tooltip.add(StringTextComponent.EMPTY);
        }
        
        if(this.pulls.length > 0)
        {
            tooltip.remove(tooltip.size() - 1);
        }
    }
    
    public static String makeOddsString(int weight, int totalWeight)
    {
        int a = weight;
        int b = totalWeight;
        int gcd = -1;
        
        while((gcd = Distribution.gcd(a, b)) != 1)
        {
            a /= gcd;
            b /= gcd;
        }
        
        String s2 = weight + ":" + totalWeight;
        
        if(gcd == -1)
        {
            return s2;
        }
        else
        {
            return a + ":" + b + " (" + s2 + ")";
        }
    }
    
    private static int gcd(int a, int b)
    {
        if(b == 0)
        {
            return a;
        }
        return Distribution.gcd(b, a % b);
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
