package de.cas_ual_ty.ydm.card.properties;

import java.util.List;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class SpellProperties extends Properties
{
    public SpellType spellType;
    
    public SpellProperties(Properties p0, JsonObject j)
    {
        super(p0);
        this.readSpellProperties(j);
    }
    
    public SpellProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof SpellProperties)
        {
            SpellProperties p1 = (SpellProperties)p0;
            this.spellType = p1.spellType;
        }
    }
    
    public SpellProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        this.readSpellProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        this.writeSpellProperties(j);
    }
    
    public void readSpellProperties(JsonObject j)
    {
        this.spellType = SpellType.fromString(j.get(JsonKeys.SPELL_TYPE).getAsString());
    }
    
    public void writeSpellProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.SPELL_TYPE, this.spellType.name);
    }
    
    @Override
    public void addCardType(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.getSpellType().name + " " + this.getType().name));
    }
    
    // --- Getters ---
    
    public SpellType getSpellType()
    {
        return this.spellType;
    }
}
