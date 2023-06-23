package de.cas_ual_ty.ydm.duel.playfield;



import de.cas_ual_ty.ydm.YDM;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ZoneType
{
    // for description of other fields, see builder methods below
    
    private boolean straightOnly;
    private boolean isStrict;
    private boolean isSecret;
    private boolean showFaceDownCardsToOwner;
    private boolean keepFocusedAfterInteraction;
    private boolean canHaveCounters;
    
    @Nullable
    public CardPosition defaultCardPosition;
    
    public boolean noOwner;
    
    private ResourceLocation registryName;
    private String localKey;
    
    public ZoneType()
    {
        straightOnly = true;
        isStrict = false;
        isSecret = false;
        showFaceDownCardsToOwner = false;
        keepFocusedAfterInteraction = false;
        canHaveCounters = false;
        
        defaultCardPosition = null;
        
        noOwner = false;
    
        registryName = null;
        localKey = null;
    }
    
    public ResourceLocation getRegistryName()
    {
        if(registryName == null)
        {
            registryName = YDM.zoneTypeRegistry.get().getKey(this);
        }
        
        return registryName;
    }
    
    public String getLocalKey()
    {
        if(localKey == null)
        {
            ResourceLocation rl = getRegistryName();
            localKey = "zone." + rl.getNamespace() + "." + rl.getPath();
        }
        
        return localKey;
    }
    
    public Component getLocal()
    {
        return Component.translatable(getLocalKey());
    }
    
    // allow SET and DEF position
    public ZoneType allowSideways()
    {
        straightOnly = false;
        return this;
    }
    
    // disallow viewing of cards inside without opponent noticing (deck)
    public ZoneType secret()
    {
        isSecret = true;
        return this;
    }
    
    // dont allow enemy cards in zone (GY, banished)
    public ZoneType strict()
    {
        isStrict = true;
        return this;
    }
    
    // cards which are face down are shown to the owner
    public ZoneType showFaceDownCardsToOwner()
    {
        showFaceDownCardsToOwner = true;
        return this;
    }
    
    public ZoneType keepFocusedAfterInteraction()
    {
        keepFocusedAfterInteraction = true;
        return this;
    }
    
    public ZoneType canHaveCounters()
    {
        canHaveCounters = true;
        return this;
    }
    
    // change the default card position when
    public ZoneType defaultCardPosition(CardPosition defaultCardPosition)
    {
        this.defaultCardPosition = defaultCardPosition;
        return this;
    }
    
    // for the link zones; does not create this zone once for each side
    public ZoneType noOwner()
    {
        noOwner = true;
        return this;
    }
    
    public boolean getStraightOnly()
    {
        return straightOnly;
    }
    
    public boolean getIsStrict()
    {
        return isStrict;
    }
    
    public boolean getIsSecret()
    {
        return isSecret;
    }
    
    public boolean getKeepFocusedAfterInteraction()
    {
        return keepFocusedAfterInteraction;
    }
    
    public boolean getShowFaceDownCardsToOwner()
    {
        return showFaceDownCardsToOwner;
    }
    
    public boolean getCanHaveCounters()
    {
        return canHaveCounters;
    }
    
    @Nullable
    public CardPosition getDefaultCardPosition()
    {
        return defaultCardPosition;
    }
    
    public boolean getNoOwner()
    {
        return noOwner;
    }
}
