package de.cas_ual_ty.ydm.card;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CardHolder implements Comparable<CardHolder>
{
    public static final CardHolder DUMMY = new CardHolder(Properties.DUMMY, (byte) 0, Rarity.CREATIVE.name, "DUM-MY");
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
        this(null, (byte) 0, "", "");
    }
    
    public CardHolder(CompoundTag nbt)
    {
        this();
        readCardHolderFromNBT(nbt);
    }
    
    public CardHolder(JsonObject json)
    {
        this();
        readFromJson(json);
    }
    
    public void addInformation(List<Component> tooltip)
    {
        tooltip.add(Component.literal(getCard().getName()));
        tooltip.add(Component.literal(getCode()));
        tooltip.add(Component.literal(getRarity()));
        tooltip.add(Component.literal("Image Variant " + (1 + getImageIndex())));
    }
    
    public String getImageName()
    {
        return getCard().getImageName(getImageIndex());
    }
    
    public String getInfoImageName()
    {
        return getCard().getInfoImageName(getImageIndex());
    }
    
    public String getItemImageName()
    {
        return getCard().getItemImageName(getImageIndex());
    }
    
    public String getMainImageName()
    {
        return getCard().getMainImageName(getImageIndex());
    }
    
    public String getImageURL()
    {
        return getCard().getImageURL(getImageIndex());
    }
    
    public ResourceLocation getInfoImageResourceLocation()
    {
        return getCard().getInfoImageResourceLocation(getImageIndex());
    }
    
    public ResourceLocation getItemImageResourceLocation()
    {
        return getCard().getItemImageResourceLocation(getImageIndex());
    }
    
    public ResourceLocation getMainImageResourceLocation()
    {
        return getCard().getMainImageResourceLocation(getImageIndex());
    }
    
    public void override(CardHolder cardHolder)
    {
        card = cardHolder.card;
        imageIndex = cardHolder.imageIndex;
        rarity = cardHolder.rarity;
        code = cardHolder.code;
    }
    
    public Properties getCard()
    {
        return card;
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
        return imageIndex;
    }
    
    public void setRarity(String rarity)
    {
        this.rarity = rarity;
    }
    
    public String getRarity()
    {
        return rarity;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void readCardHolderFromNBT(CompoundTag nbt)
    {
        card = YdmDatabase.PROPERTIES_LIST.get(nbt.getLong(JsonKeys.ID));
        
        if(card == null)
        {
            card = Properties.DUMMY;
        }
        
        imageIndex = nbt.getByte(JsonKeys.IMAGE_INDEX);
        rarity = nbt.getString(JsonKeys.RARITY);
        code = nbt.getString(JsonKeys.CODE);
    }
    
    public void writeCardHolderToNBT(CompoundTag nbt)
    {
        if(card != Properties.DUMMY)
        {
            nbt.putLong(JsonKeys.ID, card.getId());
        }
        
        nbt.putByte(JsonKeys.IMAGE_INDEX, imageIndex);
        nbt.putString(JsonKeys.RARITY, rarity);
        nbt.putString(JsonKeys.CODE, code);
    }
    
    public void readFromJson(JsonObject json)
    {
        card = YdmDatabase.PROPERTIES_LIST.get(json.get(JsonKeys.ID).getAsLong());
        
        if(card == null)
        {
            card = Properties.DUMMY;
        }
        
        imageIndex = json.get(JsonKeys.IMAGE_INDEX).getAsByte();
        rarity = json.get(JsonKeys.RARITY).getAsString();
        code = json.get(JsonKeys.CODE).getAsString();
    }
    
    public void writeToJson(JsonObject json)
    {
        if(card != Properties.DUMMY)
        {
            json.addProperty(JsonKeys.ID, card.getId());
        }
        
        json.addProperty(JsonKeys.IMAGE_INDEX, imageIndex);
        json.addProperty(JsonKeys.RARITY, rarity);
        json.addProperty(JsonKeys.CODE, code);
    }
    
    @Override
    public String toString()
    {
        return getCard().getName() + " (" + getCode() + ")";
    }
    
    @Override
    public int compareTo(CardHolder o)
    {
        int c = code.compareTo(o.code);
        
        if(c != 0)
        {
            return c;
        }
        
        c = rarity.compareTo(o.rarity);
        
        if(c != 0)
        {
            return c;
        }
        
        return Byte.compare(imageIndex, o.imageIndex);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof CardHolder))
        {
            return false;
        }
        
        CardHolder holder = (CardHolder) obj;
        
        return card == holder.card && imageIndex == holder.imageIndex && rarity == holder.rarity;
    }
}
