package de.cas_ual_ty.ydm.card.properties;

public enum Species
{
    AQUA("Aqua"), BEAST("Beast"), BEAST_WARRIOR("Beast-Warrior"), CREATOR_GOD("Creator-God"), CYBERSE("Cyberse"), DESTROYER_GOD("Destroyer God", true), DINOSAUR("Dinosaur"), DIVINE_BEAST("Divine-Beast"), DRAGON("Dragon"), FAIRY("Fairy"), FIEND("Fiend"), FISH("Fish"), INSECT("Insect"), MACHINE("Machine"), PLANT("Plant"), PSYCHIC("Psychic"), PYRO("Pyro"), REPTILE("Reptile"), ROCK("Rock"), SEA_SERPENT("Sea Serpent"), SPELLCASTER("Spellcaster"), THUNDER("Thunder"), WARRIOR("Warrior"), WINGED_BEAST("Winged Beast"), WYRM("Wyrm"), ZOMBIE("Zombie");
    
    public final String name;
    
    private Species(String name)
    {
        this.name = name;
    }
    
    private Species(String name, boolean custom)
    {
        this(name);
        // not sure if I am going to use the custom parameter
        // this is for custom cards
    }
    
    public static final Species[] VALUES = Species.values();
    
    public static Species fromString(String s)
    {
        for(Species species : Species.VALUES)
        {
            if(species.name.equals(s))
            {
                return species;
            }
        }
        
        return null;
    }
}
