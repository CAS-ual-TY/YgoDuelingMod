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
    public static final CardHolder DUMMY = new CardHolder(Properties.DUMMY, (byte)0, Rarity.CREATIVE.name, "DUM-MY");
    public Properties card;
    public byte imageIndex;
    public String rarity;
    public String code;
    
    public CardHolder(Properties card, byte imageIndex, String rarity, String code)
    {
        this.card = card;
        this.imageIndex = imageIndex;
        this.rarity = rarity;
        this.code = code;
    }
    
    public CardHolder(Properties card, byte imageIndex, String rarity)
    {
        this(card, imageIndex, rarity, card.getId() + "_" + imageIndex);
    }
    
    protected CardHolder()
    {
        this(null, (byte)0, "", "");
    }
    
    public CardHolder(CompoundNBT nbt)
    {
        this();
        this.readCardHolderFromNBT(nbt);
    }
    
    public CardHolder(JsonObject json)
    {
        this();
        this.readFromJson(json);
    }
    
    public void addInformation(List<ITextComponent> tooltip)
    {
        tooltip.add(new StringTextComponent(this.getCard().getName()));
        tooltip.add(new StringTextComponent(this.getCode()));
        tooltip.add(new StringTextComponent(this.getRarity()));
        tooltip.add(new StringTextComponent("Image Variant " + (1 + this.getImageIndex())));
    }
    
    public String getImageName()
    {
        return this.getCard().getImageName(this.getImageIndex());
    }
    
    public String getInfoImageName()
    {
        return this.getCard().getInfoImageName(this.getImageIndex());
    }
    
    public String getItemImageName()
    {
        return this.getCard().getItemImageName(this.getImageIndex());
    }
    
    public String getMainImageName()
    {
        return this.getCard().getMainImageName(this.getImageIndex());
    }
    
    public String getImageURL()
    {
        return this.getCard().getImageURL(this.getImageIndex());
    }
    
    public ResourceLocation getInfoImageResourceLocation()
    {
        return this.getCard().getInfoImageResourceLocation(this.getImageIndex());
    }
    
    public ResourceLocation getItemImageResourceLocation()
    {
        return this.getCard().getItemImageResourceLocation(this.getImageIndex());
    }
    
    public ResourceLocation getMainImageResourceLocation()
    {
        return this.getCard().getMainImageResourceLocation(this.getImageIndex());
    }
    
    public void override(CardHolder cardHolder)
    {
        this.card = cardHolder.card;
        this.imageIndex = cardHolder.imageIndex;
        this.rarity = cardHolder.rarity;
        this.code = cardHolder.code;
    }
    
    public Properties getCard()
    {
        return this.card;
    }
    
    public void setCard(Properties card)
    {
        this.card = card;
    }
    
    public void setImageIndex(byte imageIndex)
    {
        this.imageIndex = imageIndex;
    }
    
    public byte getImageIndex()
    {
        return this.imageIndex;
    }
    
    public void setRarity(String rarity)
    {
        this.rarity = rarity;
    }
    
    public String getRarity()
    {
        return this.rarity;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getCode()
    {
        return this.code;
    }
    
    public void readCardHolderFromNBT(CompoundNBT nbt)
    {
        this.card = YdmDatabase.PROPERTIES_LIST.get(nbt.getLong(JsonKeys.ID));
        
        if(this.card == null)
        {
            this.card = Properties.DUMMY;
        }
        
        this.imageIndex = nbt.getByte(JsonKeys.IMAGE_INDEX);
        this.rarity = nbt.getString(JsonKeys.RARITY);
        this.code = nbt.getString(JsonKeys.CODE);
    }
    
    public void writeCardHolderToNBT(CompoundNBT nbt)
    {
        if(this.card != Properties.DUMMY)
        {
            nbt.putLong(JsonKeys.ID, this.card.getId());
        }
        
        nbt.putByte(JsonKeys.IMAGE_INDEX, this.imageIndex);
        nbt.putString(JsonKeys.RARITY, this.rarity);
        nbt.putString(JsonKeys.CODE, this.code);
    }
    
    public void readFromJson(JsonObject json)
    {
        this.card = YdmDatabase.PROPERTIES_LIST.get(json.get(JsonKeys.ID).getAsLong());
        
        if(this.card == null)
        {
            this.card = Properties.DUMMY;
        }
        
        this.imageIndex = json.get(JsonKeys.IMAGE_INDEX).getAsByte();
        this.rarity = json.get(JsonKeys.RARITY).getAsString();
        this.code = json.get(JsonKeys.CODE).getAsString();
    }
    
    public void writeToJson(JsonObject json)
    {
        if(this.card != Properties.DUMMY)
        {
            json.addProperty(JsonKeys.ID, this.card.getId());
        }
        
        json.addProperty(JsonKeys.IMAGE_INDEX, this.imageIndex);
        json.addProperty(JsonKeys.RARITY, this.rarity);
        json.addProperty(JsonKeys.CODE, this.code);
    }
    
    @Override
    public String toString()
    {
        return this.getCard().getName() + " (" + this.getCode() + ")";
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof CardHolder))
        {
            return false;
        }
        
        CardHolder holder = (CardHolder)obj;
        
        return this.card == holder.card && this.imageIndex == holder.imageIndex && this.rarity == holder.rarity;
    }
}
