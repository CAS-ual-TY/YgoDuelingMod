package de.cas_ual_ty.ydm.duel.playfield;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ZoneType extends ForgeRegistryEntry<ZoneType>
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
    }
    
    public String getLocalKey()
    {
        return "zone." + getRegistryName().getNamespace() + "." + getRegistryName().getPath();
    }
    
    public ITextComponent getLocal()
    {
        return new TranslationTextComponent(getLocalKey());
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
