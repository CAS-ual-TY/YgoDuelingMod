package de.cas_ual_ty.ydm.card.properties;

public enum Type
{
    MONSTER("Monster"), SPELL("Spell"), TRAP("Trap");
    
    public final String name;
    
    Type(String name)
    {
        this.name = name;
    }
    
    public static final Type[] VALUES = Type.values();
    
    public static Type fromString(String s)
    {
        for(Type type : Type.VALUES)
        {
            if(type.name.equals(s))
            {
                return type;
            }
        }
        
        return null;
    }
}
