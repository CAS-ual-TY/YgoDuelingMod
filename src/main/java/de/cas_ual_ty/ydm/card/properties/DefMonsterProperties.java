package de.cas_ual_ty.ydm.card.properties;

import java.util.List;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DefMonsterProperties extends MonsterProperties
{
    public int def;
    
    public DefMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        this.readDefMonsterProperties(j);
    }
    
    public DefMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof DefMonsterProperties)
        {
            DefMonsterProperties p1 = (DefMonsterProperties)p0;
            this.def = p1.def;
        }
    }
    
    public DefMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        this.readDefMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        this.writeDefProperties(j);
    }
    
    public void readDefMonsterProperties(JsonObject j)
    {
        this.def = j.get(JsonKeys.DEF).getAsInt();
    }
    
    public void writeDefProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.DEF, this.def);
    }
    
    @Override
    public void addMonsterHeader2(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.getAtk() + " ATK / " + this.getDef() + " DEF"));
    }
    
    // --- Getters ---
    
    public int getDef()
    {
        return this.def;
    }
}
