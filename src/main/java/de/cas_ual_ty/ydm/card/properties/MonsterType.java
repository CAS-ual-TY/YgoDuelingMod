package de.cas_ual_ty.ydm.card.properties;

public enum MonsterType
{
    FUSION("Fusion"), LINK("Link"), RITUAL("Ritual"), SYNCHRO("Synchro"), XYZ("Xyz");
    
    public final String name;
    
    private MonsterType(String name)
    {
        this.name = name;
    }
    
    public static final MonsterType[] VALUES = MonsterType.values();
    
    public static MonsterType fromString(String s)
    {
        for(MonsterType monsterType : MonsterType.VALUES)
        {
            if(monsterType.name.equals(s))
            {
                return monsterType;
            }
        }
        
        return null;
    }
}
