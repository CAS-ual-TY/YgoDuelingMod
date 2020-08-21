package de.cas_ual_ty.ydm.card;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.properties.Properties;
import net.minecraft.util.ResourceLocation;

public class Card
{
    public Properties properties;
    public String setId;
    public byte imageIndex;
    public Rarity rarity;
    
    public Card(JsonObject j)
    {
        
    }
    
    public Card(Properties p, byte imageIndex)
    {
        this.properties = p;
        this.setId = String.valueOf(this.properties.getId() + "_" + imageIndex);
        this.imageIndex = imageIndex;
        this.rarity = Rarity.COMMON;
    }
    
    public String getImageName()
    {
        return this.getProperties().getImageName(this.getImageIndex());
    }
    
    public String getItemImageURL()
    {
        return this.getProperties().getImageURL(this.getImageIndex());
    }
    
    public ResourceLocation getItemImageResourceLocation()
    {
        return this.getProperties().getItemImageResourceLocation(this.getImageIndex());
    }
    
    public String getItemImageName()
    {
        return this.getProperties().getItemImageName(this.getImageIndex());
    }
    
    // --- Getters ---
    
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
    
    @Override
    public String toString()
    {
        return this.getSetId();
    }
}