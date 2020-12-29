package de.cas_ual_ty.ydm.card;

import java.util.List;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CardHolder
{
    public Card card;
    public byte overriddenImageIndex;
    public String overriddenRarity;
    
    public CardHolder(Card card, byte overriddenImageIndex, String overriddenRarity)
    {
        this.card = card;
        this.overriddenImageIndex = overriddenImageIndex;
        this.overriddenRarity = overriddenRarity;
        
        if(this.overriddenRarity.isEmpty())
        {
            this.overriddenRarity = null;
        }
    }
    
    public CardHolder(Card card)
    {
        this(card, (byte)-1, null);
    }
    
    public CardHolder(CompoundNBT nbt)
    {
        this(null, (byte)-1, null);
        this.readCardHolderFromNBT(nbt);
    }
    
    public CardHolder(JsonObject json)
    {
        this(null, (byte)-1, null);
        this.readFromJson(json);
    }
    
    public void addInformation(List<ITextComponent> tooltip)
    {
        tooltip.add(new StringTextComponent(this.getCard().getProperties().getName()));
        tooltip.add(new StringTextComponent(this.getCard().getSetId()));
        tooltip.add(new StringTextComponent(this.getActiveRarity()));
        tooltip.add(new StringTextComponent("Image Variant " + (1 + this.getActiveImageIndex())));
    }
    
    public String getImageName()
    {
        return this.getProperties().getImageName(this.getActiveImageIndex());
    }
    
    public String getInfoImageName()
    {
        return this.getProperties().getInfoImageName(this.getActiveImageIndex());
    }
    
    public String getItemImageName()
    {
        return this.getProperties().getItemImageName(this.getActiveImageIndex());
    }
    
    public String getMainImageName()
    {
        return this.getProperties().getMainImageName(this.getActiveImageIndex());
    }
    
    public String getImageURL()
    {
        return this.getProperties().getImageURL(this.getActiveImageIndex());
    }
    
    public ResourceLocation getInfoImageResourceLocation()
    {
        return this.getProperties().getInfoImageResourceLocation(this.getActiveImageIndex());
    }
    
    public ResourceLocation getItemImageResourceLocation()
    {
        return this.getProperties().getItemImageResourceLocation(this.getActiveImageIndex());
    }
    
    public ResourceLocation getMainImageResourceLocation()
    {
        return this.getProperties().getMainImageResourceLocation(this.getActiveImageIndex());
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
    
    public void overrideRarity(String rarity)
    {
        this.overriddenRarity = rarity;
    }
    
    public String getOverriddenRarity()
    {
        return this.overriddenRarity;
    }
    
    public boolean isImageIndexOverridden()
    {
        return this.getOverriddenImageIndex() != -1 && this.getOverriddenImageIndex() != this.getCard().getImageIndex();
    }
    
    public byte getActiveImageIndex()
    {
        return this.isImageIndexOverridden() ? this.getOverriddenImageIndex() : this.getCard().getImageIndex();
    }
    
    public boolean isRarityOverridden()
    {
        return this.getOverriddenRarity() != null && this.getOverriddenRarity() != this.getCard().getRarity();
    }
    
    public String getActiveRarity()
    {
        return this.isRarityOverridden() ? this.getOverriddenRarity() : this.getCard().getRarity();
    }
    
    public Properties getProperties()
    {
        return this.getCard().getProperties();
    }
    
    public void readCardHolderFromNBT(CompoundNBT nbt)
    {
        this.setCard(YdmDatabase.CARDS_LIST.get(nbt.getString(JsonKeys.SET_ID)));
        
        if(nbt.contains(JsonKeys.IMAGE_INDEX))
        {
            this.overrideImageIndex(nbt.getByte(JsonKeys.IMAGE_INDEX));
        }
        
        if(nbt.contains(JsonKeys.RARITY))
        {
            this.overrideRarity(nbt.getString(JsonKeys.RARITY));
        }
    }
    
    public void writeCardHolderToNBT(CompoundNBT nbt)
    {
        if(this.getCard() != null)
        {
            nbt.putString(JsonKeys.SET_ID, this.getCard().getSetId());
        }
        
        if(this.isImageIndexOverridden())
        {
            nbt.putByte(JsonKeys.IMAGE_INDEX, this.getOverriddenImageIndex());
        }
        
        if(this.isRarityOverridden())
        {
            nbt.putString(JsonKeys.RARITY, this.getOverriddenRarity());
        }
    }
    
    public void readFromJson(JsonObject json)
    {
        this.setCard(YdmDatabase.CARDS_LIST.get(json.get(JsonKeys.SET_ID).getAsString()));
        
        if(json.has(JsonKeys.IMAGE_INDEX))
        {
            this.overrideImageIndex(json.get(JsonKeys.IMAGE_INDEX).getAsByte());
        }
        
        if(json.has(JsonKeys.RARITY))
        {
            this.overrideRarity(json.get(JsonKeys.RARITY).getAsString());
        }
    }
    
    public void writeToJson(JsonObject json)
    {
        if(this.getCard() != null)
        {
            json.addProperty(JsonKeys.SET_ID, this.getCard().getSetId());
        }
        
        if(this.isImageIndexOverridden())
        {
            json.addProperty(JsonKeys.IMAGE_INDEX, this.getOverriddenImageIndex());
        }
        
        if(this.isRarityOverridden())
        {
            json.addProperty(JsonKeys.RARITY, this.getOverriddenRarity());
        }
    }
    
    @Override
    public String toString()
    {
        return this.getProperties().getName() + " (" + this.getCard().getSetId() + ")";
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
