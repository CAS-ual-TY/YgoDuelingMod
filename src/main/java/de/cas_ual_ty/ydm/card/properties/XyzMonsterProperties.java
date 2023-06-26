package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.network.chat.Component;

import java.util.List;

public class XyzMonsterProperties extends DefMonsterProperties
{
    public byte rank;
    
    public XyzMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        readXyzProperties(j);
    }
    
    public XyzMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof XyzMonsterProperties)
        {
            XyzMonsterProperties p1 = (XyzMonsterProperties) p0;
            rank = p1.rank;
        }
    }
    
    public XyzMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        readXyzProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
    }
    
    public void readXyzProperties(JsonObject j)
    {
        rank = j.get(JsonKeys.RANK).getAsByte();
    }
    
    public void writeXyzProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.RANK, rank);
    }
    
    @Override
    public void addMonsterHeader1(List<Component> list)
    {
        list.add(Component.literal(getAttribute() + " / Rank " + getRank()));
    }
    
    // --- Getters ---
    
    public byte getRank()
    {
        return rank;
    }
}
