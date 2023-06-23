package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class LevelMonsterProperties extends DefMonsterProperties
{
    public byte level;
    public boolean isTuner;
    
    public LevelMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        readLevelMonsterProperties(j);
    }
    
    public LevelMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof LevelMonsterProperties)
        {
            LevelMonsterProperties p1 = (LevelMonsterProperties) p0;
            level = p1.level;
            isTuner = p1.isTuner;
        }
    }
    
    public LevelMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        readLevelMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        writeLevelProperties(j);
    }
    
    public void readLevelMonsterProperties(JsonObject j)
    {
        level = j.get(JsonKeys.LEVEL).getAsByte();
        isTuner = j.get(JsonKeys.IS_TUNER).getAsBoolean();
    }
    
    public void writeLevelProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.LEVEL, level);
        j.addProperty(JsonKeys.IS_TUNER, isTuner);
    }
    
    @Override
    public void addMonsterHeader1(List<Component> list)
    {
        list.add(Component.literal(getAttribute() + " / Level " + getLevel()));
    }
    
    @Override
    public void addMonsterTextHeader(List<Component> list)
    {
        MutableComponent s = Component.literal(getSpecies() + " / ");
        
        if(getMonsterType() != null)
        {
            s.append(getMonsterType().name + " / ");
        }
        
        if(getIsPendulum())
        {
            s.append("Pendulum" + " / ");
        }
        
        if(!getAbility().isEmpty())
        {
            s.append(getAbility() + " / ");
        }
        
        if(getIsTuner())
        {
            s.append("Tuner / ");
        }
        
        if(getHasEffect())
        {
            s.append("Effect");
        }
        else
        {
            s.append("Normal");
        }
        
        list.add(s);
    }
    
    // --- Getters ---
    
    public byte getLevel()
    {
        return level;
    }
    
    public boolean getIsTuner()
    {
        return isTuner;
    }
}
