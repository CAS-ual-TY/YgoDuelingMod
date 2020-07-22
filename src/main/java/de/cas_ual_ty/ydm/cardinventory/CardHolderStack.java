package de.cas_ual_ty.ydm.cardinventory;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.JsonKeys;

public class CardHolderStack
{
    public CardHolder cardHolder;
    public int count;
    
    public CardHolderStack(CardHolder cardHolder)
    {
        this.cardHolder = cardHolder;
        this.count = 1;
    }
    
    public CardHolderStack(JsonObject json)
    {
        this.readFromJson(json);
    }
    
    public CardHolderStack merge(CardHolderStack wrapper)
    {
        this.count += wrapper.count;
        return this;
    }
    
    public int getCount()
    {
        return this.count;
    }
    
    public CardHolder getCardHolder()
    {
        JsonObject json = new JsonObject();
        this.cardHolder.writeToJson(json);
        return new CardHolder(json);
    }
    
    public CardHolder getKey()
    {
        return this.cardHolder;
    }
    
    public void writeToJson(JsonObject json)
    {
        this.cardHolder.writeToJson(json);
        json.addProperty(JsonKeys.COUNT, this.count);
    }
    
    public void readFromJson(JsonObject json)
    {
        this.cardHolder = new CardHolder(json);
        this.count = json.get(JsonKeys.COUNT).getAsInt();
    }
    
    public static int compareCardHolders(CardHolder h1, CardHolder h2)
    {
        // Compare set id
        int comp = h1.getCard().getSetId().compareTo(h2.getCard().getSetId());
        
        if(comp != 0)
        {
            return comp;
        }
        
        // Compare active image index
        comp = Byte.compare(h1.getActiveImageIndex(), h2.getActiveImageIndex());
        
        if(comp != 0)
        {
            return comp;
        }
        
        // Compare active rarity
        comp = h1.getActiveRarity().name.compareTo(h2.getActiveRarity().name);
        
        if(comp != 0)
        {
            return comp;
        }
        
        // If active image index and rarity are the same,
        // prioritize modified ones
        
        // Compare overridden image index
        // -1 is nothing and should come after any other
        // so negate result
        comp = -Byte.compare(h1.getOverriddenImageIndex(), h2.getOverriddenImageIndex());
        
        if(comp != 0)
        {
            return comp;
        }
        
        // Compare active rarity
        // if one is null and two is not null, two is preferred
        if(h1.getOverriddenRarity() == null)
        {
            if(h2.getOverriddenRarity() != null)
            {
                // one is null, two is not null
                // so one comes after two
                return 1;
            }
        }
        else
        {
            if(h2.getOverriddenRarity() == null)
            {
                // one is not null, two is null
                // so one comes before two
                return -1;
            }
        }
        
        return 0;
    }
}
