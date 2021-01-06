package de.cas_ual_ty.ydm.set;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CardSet
{
    public static final CardSet DUMMY = new CardSet("Dummy", "DUM-MY", "DUMMY", new Date(0), new FullCardPuller(new JsonObject(), null), ImmutableList.of());
    
    public String name;
    public String code;
    public String type;
    public Date date;
    public String image;
    public CardPuller pull;
    
    // must be same size and order as cards JsonArray
    public List<CardHolder> cards;
    
    public CardSet(String name, String code, String type, Date date, CardPuller pull, List<CardHolder> cards)
    {
        this.name = name;
        this.code = code;
        this.type = type;
        this.date = date;
        this.pull = pull;
        this.cards = cards;
    }
    
    public CardSet(JsonObject j) throws IllegalArgumentException
    {
        if(j.has(JsonKeys.NAME))
        {
            this.name = j.get(JsonKeys.NAME).getAsString();
        }
        else
        {
            this.name = null;
        }
        
        this.code = j.get(JsonKeys.CODE).getAsString();
        this.type = j.get(JsonKeys.TYPE).getAsString();
        
        if(!j.has(JsonKeys.DATE))
        {
            this.date = null;
        }
        else
        {
            String date = j.get(JsonKeys.DATE).getAsString();
            try
            {
                this.date = YdmDatabase.SET_DATE_PARSER.parse(date);
            }
            catch (ParseException e)
            {
                YDM.log("Can not parse date: " + date);
                throw new IllegalArgumentException(e);
            }
        }
        
        if(!j.has(JsonKeys.IMAGE))
        {
            this.image = null;
        }
        else
        {
            this.image = j.get(JsonKeys.IMAGE).getAsString();
        }
        
        this.pull = PullType.createPull(j.get(JsonKeys.PULL_TYPE).getAsString(), j, this);
        
        if(!j.has(JsonKeys.CARDS))
        {
            this.cards = ImmutableList.of();
        }
        else
        {
            JsonArray cards = j.get(JsonKeys.CARDS).getAsJsonArray();
            this.cards = new ArrayList<>(cards.size());
            
            JsonObject c;
            long id;
            Properties card;
            byte imageIndex;
            for(JsonElement e : cards)
            {
                c = e.getAsJsonObject();
                
                id = c.get(JsonKeys.ID).getAsLong();
                card = YdmDatabase.PROPERTIES_LIST.get(id);
                
                if(card == null)
                {
                    YDM.log("Can not parse card in: " + this.name + " card: " + card);
                    continue;
                }
                
                imageIndex = c.get(JsonKeys.IMAGE_INDEX).getAsByte();
                
                if(imageIndex >= card.getImageIndicesAmt())
                {
                    YDM.log("Bad image index for card in: " + this.name + " card: " + card);
                }
                
                this.cards.add(new CardHolder(card, imageIndex, c.get(JsonKeys.RARITY).getAsString(), c.get(JsonKeys.CODE).getAsString()));
            }
        }
    }
    
    public List<CardHolder> open(Random random)
    {
        return this.pull.open(random);
    }
    
    public void addInformation(List<ITextComponent> tooltip)
    {
        tooltip.add(new StringTextComponent(this.name));
        tooltip.add(new StringTextComponent(this.type));
        tooltip.add(new StringTextComponent(this.code));
        tooltip.add(new StringTextComponent(YdmDatabase.SET_DATE_PARSER.format(this.date)));
    }
    
    public boolean isIndependentAndItem()
    {
        return this != CardSet.DUMMY && !this.type.equals("Sub-Set") && this.name != null && this.date != null;
    }
    
    public String getImageName()
    {
        return this.code.toLowerCase();
    }
    
    public String getImageURL()
    {
        return this.image;
    }
    
    public String getInfoImageName()
    {
        return YDM.proxy.addSetInfoTag(this.getImageName());
    }
    
    public String getItemImageName()
    {
        return YDM.proxy.addSetItemTag(this.getImageName());
    }
    
    public ResourceLocation getInfoImageResourceLocation()
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getSetInfoReplacementImage(this) + ".png");
    }
    
    public ResourceLocation getItemImageResourceLocation()
    {
        return new ResourceLocation(YDM.MOD_ID, "item/" + this.getItemImageName());
    }
    
    public boolean getIsHardcoded()
    {
        return false;
    }
}
