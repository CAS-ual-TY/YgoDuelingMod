package de.cas_ual_ty.ydm.set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.set.Distribution.Pull.PullEntry;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;

public class Distribution
{
    public final String name;
    public final Pull[] pulls;
    
    public final int totalWeight;
    public final List<String> pullableRarities;
    
    public Distribution(JsonObject j)
    {
        name = j.get(JsonKeys.NAME).getAsString();
        
        int q, r, s;
        
        JsonArray pullsJson = j.get(JsonKeys.PULLS).getAsJsonArray();
        pulls = new Pull[pullsJson.size()];
        
        JsonObject pullJson;
        
        int weight;
        int totalWeight = 0;
        JsonArray entriesJson;
        PullEntry[] entries;
        
        JsonObject entryJson;
        int count;
        
        JsonArray raritiesJson;
        String[] rarities;
        
        for(q = 0; q < pulls.length; ++q)
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
            
            pulls[q] = new Pull(weight, entries);
            totalWeight += weight;
        }
        
        this.totalWeight = totalWeight;
        
        pullableRarities = new LinkedList<>();
        
        for(Pull pull : pulls)
        {
            for(PullEntry pe : pull.pullEntries)
            {
                for(String rarity : pe.rarities)
                {
                    if(!pullableRarities.contains(rarity))
                    {
                        pullableRarities.add(rarity);
                    }
                }
            }
        }
    }
    
    public void postDBInit()
    {
        logErrors();
    }
    
    public void addInformation(List<ITextComponent> tooltip, CardSet set)
    {
        if(pulls.length <= 0)
        {
            return;
        }
        else if(pulls.length == 1)
        {
            Pull pull = pulls[0];
            
            for(PullEntry pe : pull.pullEntries)
            {
                tooltip.add(new StringTextComponent(Distribution.makePullEntryString(set, pe)));
            }
        }
        else
        {
            for(Pull pull : pulls)
            {
                tooltip.add(new StringTextComponent(Distribution.makeOddsString(pull.weight, totalWeight)));
                
                for(PullEntry pe : pull.pullEntries)
                {
                    tooltip.add(new StringTextComponent("  " + Distribution.makePullEntryString(set, pe)));
                }
                
                tooltip.add(StringTextComponent.EMPTY);
            }
            
            tooltip.remove(tooltip.size() - 1);
        }
    }
    
    public void logErrors()
    {
        int gcd = -1;
        
        for(Pull pull : pulls)
        {
            if(gcd == -1)
            {
                gcd = pull.weight;
                continue;
            }
            
            gcd = Distribution.gcd(gcd, pull.weight);
            
            if(gcd == 1)
            {
                return;
            }
        }
        
        if(gcd > 1)
        {
            YDM.log("Distribution " + name + ": Each weight can be reduced by factor: " + gcd + " (Total: " + totalWeight + " -> " + (totalWeight / gcd) + ")");
        }
    }
    
    public static String makePullEntryString(CardSet set, PullEntry pe)
    {
        StringBuilder s = new StringBuilder();
        
        s.append(pe.count + "x: ");
        
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
        
        if(!rarityFound)
        {
            s.append(TextFormatting.RED.toString());
            
            for(String rarity : pe.rarities)
            {
                s.append(rarity + " / ");
            }
        }
        
        if(pe.rarities.length > 0)
        {
            s.delete(s.length() - 3, s.length());
        }
        
        return s.toString();
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
