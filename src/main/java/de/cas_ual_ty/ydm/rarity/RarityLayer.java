package de.cas_ual_ty.ydm.rarity;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.resources.ResourceLocation;

public class RarityLayer
{
    public String texture;
    public RarityLayerType type;
    
    public RarityLayer(String texture, RarityLayerType type)
    {
        this.texture = texture;
        this.type = type;
    }
    
    public RarityLayer(JsonObject json)
    {
        this(json.get(JsonKeys.IMAGE).getAsString(), RarityLayerType.fromString(json.get(JsonKeys.TYPE).getAsString()));
    }
    
    public ResourceLocation getMainImageResourceLocation()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getRarityMainImage(this) + ".png");
    }
    
    public ResourceLocation getInfoImageResourceLocation()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getRarityInfoImage(this) + ".png");
    }
    
    @Override
    public String toString()
    {
        return "RarityLayer{" +
                "texture='" + texture + '\'' +
                ", type=" + type +
                '}';
    }
}
