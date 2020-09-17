package de.cas_ual_ty.ydm.duelmanager.playfield;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;

public class ZoneType
{
    // position of bottom zones ("your" zones, not "enemy" zones)
    public final int x;
    public final int y;
    
    private int width;
    private int height;
    
    // for description of other fields, see builder methods below
    
    private boolean straightOnly;
    private boolean isStrict;
    private boolean isSecret;
    private boolean renderCardsSpread;
    
    private int childrenAmt;
    private int childOffX;
    
    @Nullable
    public CardPosition defaultCardPosition;
    
    public boolean noOwner;
    
    public ZoneType(int x, int y)
    {
        this.x = x;
        this.y = y;
        
        this.width = -1;
        this.height = -1;
        
        this.straightOnly = true;
        this.isStrict = false;
        this.isSecret = false;
        this.renderCardsSpread = false;
        
        this.childrenAmt = 1;
        this.childOffX = 0;
        
        this.defaultCardPosition = null;
        
        this.noOwner = false;
    }
    
    public ZoneType fullSize()
    {
        this.width = 32;
        this.height = 32;
        return this;
    }
    
    public ZoneType slimSize()
    {
        this.width = 24;
        this.height = 32;
        return this;
    }
    
    public ZoneType size(int width, int height)
    {
        this.width = width;
        this.height = height;
        return this;
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
    
    // amt: amount of children (eg. Monster Zone has 5)
    // childOffX: space between them (width already accounted for)
    public ZoneType withChildren(int childrenAmt, int childOffX)
    {
        this.childrenAmt = childrenAmt;
        this.childOffX = childOffX;
        
        if(this.childOffX < 0)
        {
            this.childOffX -= this.width;
        }
        
        if(this.childOffX > 0)
        {
            this.childOffX += this.width;
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
    
    public int getX()
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    public int getWidth()
    {
        return this.width;
    }
    
    public int getHeight()
    {
        return this.height;
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
    
    public int getChildrenAmount()
    {
        return this.childrenAmt;
    }
    
    public int getXOffsetForChild(byte child)
    {
        return child * this.childOffX;
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
