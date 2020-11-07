package de.cas_ual_ty.ydm.duel.playfield;

import javax.annotation.Nullable;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class ZoneType extends ForgeRegistryEntry<ZoneType>
{
    // for description of other fields, see builder methods below
    
    private boolean straightOnly;
    private boolean isStrict;
    private boolean isSecret;
    private boolean showFaceDownCardsToOwner;
    
    @Nullable
    public CardPosition defaultCardPosition;
    
    public boolean noOwner;
    
    public ZoneType()
    {
        this.straightOnly = true;
        this.isStrict = false;
        this.isSecret = false;
        this.showFaceDownCardsToOwner = false;
        
        this.defaultCardPosition = null;
        
        this.noOwner = false;
    }
    
    // allow SET and DEF position
    public ZoneType allowSideways()
    {
        this.straightOnly = false;
        return this;
    }
    
    // disallow viewing of cards inside without opponent noticing (deck)
    public ZoneType secret()
    {
        this.isSecret = true;
        return this;
    }
    
    // dont allow enemy cards in zone (GY, banished)
    public ZoneType strict()
    {
        this.isStrict = true;
        return this;
    }
    
    // cards which are face down are shown to the owner
    public ZoneType showFaceDownCardsToOwner()
    {
        this.showFaceDownCardsToOwner = true;
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
        this.noOwner = true;
        return this;
    }
    
    public boolean getStraightOnly()
    {
        return this.straightOnly;
    }
    
    public boolean getIsStrict()
    {
        return this.isStrict;
    }
    
    public boolean getIsSecret()
    {
        return this.isSecret;
    }
    
    public boolean getShowFaceDownCardsToOwner()
    {
        return this.showFaceDownCardsToOwner;
    }
    
    @Nullable
    public CardPosition getDefaultCardPosition()
    {
        return this.defaultCardPosition;
    }
    
    public boolean getNoOwner()
    {
        return this.noOwner;
    }
}
