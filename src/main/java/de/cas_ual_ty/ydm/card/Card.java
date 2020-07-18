package de.cas_ual_ty.ydm.card;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.properties.Properties;

public class Card
{
    public Properties properties;
    public String setId;
    public byte imageIndex;
    public Rarity rarity;
    
    public Card(JsonObject j)
    {
        
    }
    
    public Card(Properties p)
    {
        this.properties = p;
        this.setId = "SET_ID";
        this.imageIndex = 0;
        this.rarity = Rarity.COMMON;
    }
    
    public Properties getProperties()
    {
        return this.properties;
    }
    
    public String getSetId()
    {
        return this.setId;
    }
    
    public byte getImageIndex()
    {
        return this.imageIndex;
    }
    
    public Rarity getRarity()
    {
        return this.rarity;
    }
}