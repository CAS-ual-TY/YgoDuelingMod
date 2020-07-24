package de.cas_ual_ty.ydm.card.properties;

import java.util.List;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;

public class XyzMonsterProperties extends DefMonsterProperties
{
    public byte rank;
    
    public XyzMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        this.readXyzProperties(j);
    }
    
    public XyzMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof XyzMonsterProperties)
        {
            XyzMonsterProperties p1 = (XyzMonsterProperties)p0;
            this.rank = p1.rank;
        }
    }
    
    public XyzMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        this.readXyzProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
    }
    
    public void readXyzProperties(JsonObject j)
    {
        this.rank = j.get(JsonKeys.RANK).getAsByte();
    }
    
    public void writeXyzProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.RANK, this.rank);
    }
    
    @Override
    public void addMonsterHeader1(List<String> list)
    {
        list.add(this.getAttribute().name + " / Rank " + this.getRank());
    }
    
    // --- Getters ---
    
    public byte getRank()
    {
        return this.rank;
    }
}
