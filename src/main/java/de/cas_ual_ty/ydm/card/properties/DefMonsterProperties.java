package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.network.chat.Component;

import java.util.List;

public class DefMonsterProperties extends MonsterProperties
{
    public int def;
    
    public DefMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        readDefMonsterProperties(j);
    }
    
    public DefMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof DefMonsterProperties)
        {
            DefMonsterProperties p1 = (DefMonsterProperties) p0;
            def = p1.def;
        }
    }
    
    public DefMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        readDefMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        writeDefProperties(j);
    }
    
    public void readDefMonsterProperties(JsonObject j)
    {
        def = j.get(JsonKeys.DEF).getAsInt();
    }
    
    public void writeDefProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.DEF, def);
    }
    
    @Override
    public void addMonsterHeader2(List<Component> list)
    {
        list.add(Component.literal(getAtk() + " ATK / " + getDef() + " DEF"));
    }
    
    // --- Getters ---
    
    public int getDef()
    {
        return def;
    }
}
