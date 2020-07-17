package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;

public class TrapProperties extends Properties
{
    public TrapType trapType;
    
    public TrapProperties(Properties p0, JsonObject j)
    {
        super(p0);
        this.readTrapProperties(j);
    }
    
    public TrapProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof TrapProperties)
        {
            TrapProperties p1 = (TrapProperties)p0;
            this.trapType = p1.trapType;
        }
    }
    
    public TrapProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        this.readTrapProperties(j);
    }
    
    public void readTrapProperties(JsonObject j)
    {
        this.trapType = TrapType.fromString(j.get(JsonKeys.TRAP_TYPE).getAsString());
    }
    
    // --- Getters ---
    
    public TrapType getTrapType()
    {
        return this.trapType;
    }
}
