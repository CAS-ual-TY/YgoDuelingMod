package de.cas_ual_ty.ydm.rarity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;

import java.util.LinkedList;
import java.util.List;

public class RarityEntry
{
    public final String rarity;
    public final List<RarityLayer> layers;
    
    public RarityEntry(String rarity, List<RarityLayer> layers)
    {
        this.rarity = rarity;
        this.layers = layers;
    }
    
    public RarityEntry(JsonObject json)
    {
        this(json.get(JsonKeys.RARITY).getAsString(), new LinkedList<>());
        
        JsonArray layers = json.getAsJsonArray(JsonKeys.LAYERS);
        for(int i = 0; i < layers.size(); i++)
        {
            JsonElement e = layers.get(i);
            this.layers.add(new RarityLayer(e.getAsJsonObject()));
        }
    }
    
    @Override
    public String toString()
    {
        return "RarityEntry{" +
                "rarity='" + rarity + '\'' +
                ", layers=" + layers +
                '}';
    }
}
