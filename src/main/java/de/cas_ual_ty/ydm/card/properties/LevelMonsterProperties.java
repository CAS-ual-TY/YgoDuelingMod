package de.cas_ual_ty.ydm.card.properties;

import java.util.List;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;

public class LevelMonsterProperties extends DefMonsterProperties
{
    public byte level;
    public boolean isTuner;
    
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
            this.isTuner = p1.isTuner;
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
        this.isTuner = j.get(JsonKeys.IS_TUNER).getAsBoolean();
    }
    
    public void writeLevelProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.LEVEL, this.level);
        j.addProperty(JsonKeys.IS_TUNER, this.isTuner);
    }
    
    @Override
    public void addMonsterHeader1(List<String> list)
    {
        list.add(this.getAttribute().name + " / Level " + this.getLevel());
    }
    
    @Override
    public void addMonsterTextHeader(List<String> list)
    {
        String s = this.getSpecies().name + " / ";
        
        if(this.getMonsterType() != null)
        {
            s += this.getMonsterType().name + " / ";
        }
        
        if(this.getIsPendulum())
        {
            s += "Pendulum" + " / ";
        }
        
        if(this.getAbility() != null)
        {
            s += this.getAbility().name + " / ";
        }
        
        if(this.getIsTuner())
        {
            s += "Tuner / ";
        }
        
        if(this.getHasEffect())
        {
            s += "Effect";
        }
        else
        {
            s += "Normal";
        }
        
        list.add(s);
    }
    
    // --- Getters ---
    
    public byte getLevel()
    {
        return this.level;
    }
    
    public boolean getIsTuner()
    {
        return this.isTuner;
    }
}
