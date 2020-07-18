package de.cas_ual_ty.ydm.card;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.Database;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.nbt.CompoundNBT;

public class Card
{
    public Properties properties;
    public byte pictureIndex;
    public String setId;
    public Rarity rarity;
    
    public Card(JsonObject j)
    {
        
    }
    
    public Card(Properties p)
    {
        this.properties = p;
        this.pictureIndex = 0;
        this.setId = "SET_ID";
        this.rarity = Rarity.COMMON;
    }
}