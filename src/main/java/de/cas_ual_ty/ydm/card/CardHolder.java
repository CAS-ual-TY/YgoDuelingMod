package de.cas_ual_ty.ydm.card;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.nbt.CompoundNBT;

public class CardHolder
{
    public Card card;
    public byte overriddenImageIndex;
    public Rarity overriddenRarity;
    
    public CardHolder(Card card, byte overriddenImageIndex, Rarity overriddenRarity)
    {
        this.card = card;
        this.overriddenImageIndex = overriddenImageIndex;
        this.overriddenRarity = overriddenRarity;
    }
    
    public CardHolder(Card card)
    {
        this(card, (byte)-1, (Rarity)null);
    }
    
    public CardHolder(CompoundNBT nbt)
    {
        this.readCardHolderFromNBT(nbt);
    }
    
    public CardHolder(JsonObject json)
    {
        this.readFromJson(json);
    }
    
    protected CardHolder()
    {
    }
    
    public void override(CardHolder cardHolder)
    {
        this.card = cardHolder.card;
        this.overriddenImageIndex = cardHolder.overriddenImageIndex;
        this.overriddenRarity = cardHolder.overriddenRarity;
    }
    
    public Card getCard()
    {
        return this.card;
    }
    
    public void setCard(Card card)
    {
        this.card = card;
    }
    
    public void overrideImageIndex(byte imageIndex)
    {
        this.overriddenImageIndex = imageIndex;
    }
    
    public byte getOverriddenImageIndex()
    {
        return this.overriddenImageIndex;
    }
    
    public void overrideRarity(Rarity rarity)
    {
        this.overriddenRarity = rarity;
    }
    
    public Rarity getOverriddenRarity()
    {
        return this.overriddenRarity;
    }
    
    public boolean isImageIndexOverridden()
    {
        return this.getOverriddenImageIndex() != -1;
    }
    
    public byte getActiveImageIndex()
    {
        return this.isImageIndexOverridden() ? this.getOverriddenImageIndex() : this.getCard().getImageIndex();
    }
    
    public boolean isRarityOverridden()
    {
        return this.getOverriddenRarity() != null;
    }
    
    public Rarity getActiveRarity()
    {
        return this.isRarityOverridden() ? this.getOverriddenRarity() : this.getCard().getRarity();
    }
    
    public Properties getProperties()
    {
        return this.getCard().getProperties();
    }
    
    public void readCardHolderFromNBT(CompoundNBT nbt)
    {
        this.setCard(Database.CARDS_LIST.get(nbt.getString(JsonKeys.SET_ID)));
        this.overrideImageIndex(nbt.getByte(JsonKeys.IMAGE_INDEX));
        this.overrideRarity(Rarity.fromString(nbt.getString(JsonKeys.RARITY)));
    }
    
    public void writeCardHolderToNBT(CompoundNBT nbt)
    {
        if(this.getCard() != null)
        {
            nbt.putString(JsonKeys.SET_ID, this.getCard().getSetId());
        }
        
        nbt.putByte(JsonKeys.IMAGE_INDEX, this.getOverriddenImageIndex());
        
        if(this.isRarityOverridden())
        {
            nbt.putString(JsonKeys.RARITY, this.getOverriddenRarity().name);
        }
        else
        {
            nbt.putString(JsonKeys.RARITY, "");
        }
    }
    
    public void readFromJson(JsonObject json)
    {
        this.setCard(Database.CARDS_LIST.get(json.get(JsonKeys.SET_ID).getAsString()));
        this.overrideImageIndex(json.get(JsonKeys.IMAGE_INDEX).getAsByte());
        this.overrideRarity(Rarity.fromString(json.get(JsonKeys.RARITY).getAsString()));
    }
    
    public void writeToJson(JsonObject json)
    {
        if(this.getCard() != null)
        {
            json.addProperty(JsonKeys.SET_ID, this.getCard().getSetId());
        }
        
        json.addProperty(JsonKeys.IMAGE_INDEX, this.getOverriddenImageIndex());
        
        if(this.isRarityOverridden())
        {
            json.addProperty(JsonKeys.RARITY, this.getOverriddenRarity().name);
        }
        else
        {
            json.addProperty(JsonKeys.RARITY, "");
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof CardHolder))
        {
            return false;
        }
        
        CardHolder holder = (CardHolder)obj;
        
        return this.card == holder.card && this.overriddenImageIndex == holder.overriddenImageIndex && this.overriddenRarity == holder.overriddenRarity;
    }
}
