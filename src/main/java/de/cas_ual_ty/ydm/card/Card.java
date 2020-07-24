package de.cas_ual_ty.ydm.card;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.client.ImageHandler;
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
    
    public String getDirectImageName()
    {
        return this.getProperties().getId() + "_" + this.getImageIndex();
    }
    
    public String getInfoImageName()
    {
        return ImageHandler.addInfoSuffix(this.getDirectImageName());
    }
    
    public String getItemImageName()
    {
        return ImageHandler.addItemSuffix(this.getDirectImageName());
    }
    
    public String getImageURL()
    {
        return this.getProperties().getImage(this.getImageIndex());
    }
    
    public ResourceLocation getInfoImageResourceLocation()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ImageHandler.getInfoReplacementImage(this) + ".png");
    }
    
    public ResourceLocation getItemImageResourceLocation()
    {
        return new ResourceLocation(YDM.MOD_ID, "item/" + this.getItemImageName());
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
}