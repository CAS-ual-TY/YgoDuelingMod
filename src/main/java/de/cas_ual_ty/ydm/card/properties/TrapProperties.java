package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class TrapProperties extends Properties
{
    public TrapType trapType;
    
    public TrapProperties(Properties p0, JsonObject j)
    {
        super(p0);
        readTrapProperties(j);
    }
    
    public TrapProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof TrapProperties)
        {
            TrapProperties p1 = (TrapProperties) p0;
            trapType = p1.trapType;
        }
    }
    
    public TrapProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        readTrapProperties(j);
    }
    
    public void readTrapProperties(JsonObject j)
    {
        trapType = TrapType.fromString(j.get(JsonKeys.TRAP_TYPE).getAsString());
    }
    
    @Override
    public void addCardType(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(getTrapType().name + " " + getType().name));
    }
    
    // --- Getters ---
    
    public TrapType getTrapType()
    {
        return trapType;
    }
}
