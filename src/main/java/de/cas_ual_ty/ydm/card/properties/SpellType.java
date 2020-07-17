package de.cas_ual_ty.ydm.card.properties;

public enum SpellType
{
    NORMAL("Normal"), FIELD("Field"), EQUIP("Equip"), CONTINUOUS("Continuous"), QUICK_PLAY("Quick-Play"), RITUAL("Ritual");
    
    public final String name;
    
    private SpellType(String name)
    {
        this.name = name;
    }
    
    public static final SpellType[] VALUES = SpellType.values();
    
    public static SpellType fromString(String s)
    {
        for(SpellType spellType : SpellType.VALUES)
        {
            if(spellType.name.equals(s))
            {
                return spellType;
            }
        }
        
        return null;
    }
}
