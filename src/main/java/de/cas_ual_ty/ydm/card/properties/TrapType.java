package de.cas_ual_ty.ydm.card.properties;

public enum TrapType
{
    NORMAL("Normal"), CONTINUOUS("Continuous"), COUNTER("Counter");
    
    public final String name;
    
    TrapType(String name)
    {
        this.name = name;
    }
    
    public static final TrapType[] VALUES = TrapType.values();
    
    public static TrapType fromString(String s)
    {
        for(TrapType trapType : TrapType.VALUES)
        {
            if(trapType.name.equals(s))
            {
                return trapType;
            }
        }
        
        return null;
    }
}
