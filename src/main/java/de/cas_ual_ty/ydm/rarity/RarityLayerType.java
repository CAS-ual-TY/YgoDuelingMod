package de.cas_ual_ty.ydm.rarity;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.ResourceLocation;

public enum RarityLayerType
{
    NORMAL("normal"), INVERTED("inverted");
    
    public static final ResourceLocation EMPTY_MASK = new ResourceLocation(YDM.MOD_ID, "textures/gui/mask_empty.png");
    
    public final ResourceLocation maskRl;
    public final String id;
    
    RarityLayerType(String id)
    {
        maskRl = new ResourceLocation(YDM.MOD_ID, "textures/gui/rarity_mask_" + id + ".png");
        this.id = id;
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
