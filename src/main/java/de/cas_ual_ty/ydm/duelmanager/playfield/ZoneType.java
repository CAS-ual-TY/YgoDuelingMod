package de.cas_ual_ty.ydm.duelmanager.playfield;

import javax.annotation.Nullable;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class ZoneType extends ForgeRegistryEntry<ZoneType>
{
    // for description of other fields, see builder methods below
    
    private boolean straightOnly;
    private boolean isStrict;
    private boolean isSecret;
    private boolean renderCardsSpread;
    private boolean renderCardsReversed;
    private boolean showFaceDownCardsToOwner;
    
    @Nullable
    public CardPosition defaultCardPosition;
    
    public boolean noOwner;
    
    public ZoneType()
    {
        this.straightOnly = true;
        this.isStrict = false;
        this.isSecret = false;
        this.renderCardsSpread = false;
        this.renderCardsReversed = false;
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
    // exclusive with renderCardsSpread
    public ZoneType secret()
    {
        if(!this.renderCardsSpread)
        {
            this.isSecret = true;
        }
        return this;
    }
    
    // dont allow enemy cards in zone (GY, banished)
    public ZoneType strict()
    {
        this.isStrict = true;
        return this;
    }
    
    // exclusive with isSecret
    public ZoneType renderCardsSpread()
    {
        if(!this.isSecret)
        {
            this.renderCardsSpread = true;
        }
        return this;
    }
    
    // requires renderCardsSpread
    // render bottom to top instead of top to bottom
    public ZoneType renderCardsReversed()
    {
        if(this.renderCardsSpread)
        {
            this.renderCardsReversed = true;
        }
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
    
    public boolean getRenderCardsSpread()
    {
        return this.renderCardsSpread;
    }
    
    public boolean getRenderCardsReversed()
    {
        return this.renderCardsReversed;
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
