package de.cas_ual_ty.ydm.duelmanager.playfield;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ZoneType extends ForgeRegistryEntry<ZoneType>
{
    // for description of other fields, see builder methods below
    
    private boolean straightOnly;
    private boolean isStrict;
    private boolean isSecret;
    private boolean renderCardsSpread;
    
    @Nullable
    public CardPosition defaultCardPosition;
    
    public boolean noOwner;
    
    public ZoneType()
    {
        this.straightOnly = true;
        this.isStrict = false;
        this.isSecret = false;
        this.renderCardsSpread = false;
        
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
