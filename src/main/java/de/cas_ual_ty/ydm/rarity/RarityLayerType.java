package de.cas_ual_ty.ydm.rarity;

public enum RarityLayerType
{
    NORMAL("normal", false), INVERTED("inverted", true);
    
    public final String id;
    public final boolean invertedRendering;
    
    RarityLayerType(String id, boolean invertedRendering)
    {
        this.id = id;
        this.invertedRendering = invertedRendering;
    }
    
    public static RarityLayerType fromString(String s)
    {
        for(RarityLayerType t : values())
        {
            if(t.id.equals(s))
            {
                return t;
            }
        }
        
        throw new IllegalArgumentException("Not a rarity layer type: " + s);
    }
}
