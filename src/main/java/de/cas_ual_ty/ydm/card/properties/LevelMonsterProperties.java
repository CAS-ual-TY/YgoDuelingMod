package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;

public class LevelMonsterProperties extends DefMonsterProperties
{
    public byte level;
    
    public LevelMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        this.readLevelMonsterProperties(j);
    }
    
    public LevelMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof LevelMonsterProperties)
        {
            LevelMonsterProperties p1 = (LevelMonsterProperties)p0;
            this.level = p1.level;
        }
    }
    
    public LevelMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        this.readLevelMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        this.writeLevelProperties(j);
    }
    
    public void readLevelMonsterProperties(JsonObject j)
    {
        this.level = j.get(JsonKeys.LEVEL).getAsByte();
    }
    
    public void writeLevelProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.LEVEL, this.level);
    }
}
