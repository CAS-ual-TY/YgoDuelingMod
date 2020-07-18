package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;

public class Properties
{
    public String name;
    public long id;
    public boolean isIllegal;
    public String text;
    public Type type;
    public String[] images;
    
    public Properties(Properties p0)
    {
        this.name = p0.name;
        this.id = p0.id;
        this.isIllegal = p0.isIllegal;
        this.text = p0.text;
        this.type = p0.type;
        this.images = p0.images;
    }
    
    public Properties(JsonObject j)
    {
        this.readAllProperties(j);
    }
    
    public Properties()
    {
    }
    
    public void readAllProperties(JsonObject j)
    {
        this.readProperties(j);
    }
    
    public void writeAllProperties(JsonObject j)
    {
        this.writeProperties(j);
    }
    
    public void readProperties(JsonObject j)
    {
        this.name = j.get(JsonKeys.NAME).getAsString();
        this.id = j.get(JsonKeys.ID).getAsLong();
        this.isIllegal = j.get(JsonKeys.IS_ILLEGAL).getAsBoolean();
        this.text = j.get(JsonKeys.TEXT).getAsString();
        this.type = Type.fromString(j.get(JsonKeys.TYPE).getAsString());
        
        JsonArray images = j.get(JsonKeys.IMAGES).getAsJsonArray();
        this.images = new String[images.size()];
        for(int i = 0; i < this.images.length; ++i)
        {
            this.images[i] = images.get(i).getAsString();
        }
    }
    
    public void writeProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.NAME, this.name);
        j.addProperty(JsonKeys.ID, this.id);
        j.addProperty(JsonKeys.IS_ILLEGAL, this.isIllegal);
        j.addProperty(JsonKeys.TEXT, this.text);
        j.addProperty(JsonKeys.TYPE, this.type.name);
        
        JsonArray images = new JsonArray();
        for(String image : this.images)
        {
            images.add(image);
        }
        j.add(JsonKeys.IMAGES, images);
    }
    
    public boolean getIsSpell()
    {
        return this.getType() == Type.SPELL;
    }
    
    public boolean getIsTrap()
    {
        return this.getType() == Type.TRAP;
    }
    
    public boolean getIsMonster()
    {
        return this.getType() == Type.MONSTER;
    }
    
    // --- Getters ---
    
    public String getName()
    {
        return this.name;
    }
    
    public long getId()
    {
        return this.id;
    }
    
    public boolean getIsIllegal()
    {
        return this.isIllegal;
    }
    
    public String getText()
    {
        return this.text;
    }
    
    public Type getType()
    {
        return this.type;
    }
}
