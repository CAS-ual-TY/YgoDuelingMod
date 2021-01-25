package de.cas_ual_ty.ydm.set;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CardSet
{
    public static final CardSet DUMMY = new CardSet("Dummy", "DUM-MY", "DUMMY", new Date(0), new FullCardPuller(new JsonObject(), null), ImmutableList.of())
    {
        @Override
        public String getImageName()
        {
            return "blanc_set";
        }
        
        @Override
        public boolean getIsHardcoded()
        {
            return true;
        }
    };
    
    public String name;
    public String code;
    public String type;
    public Date date;
    public String image;
    public CardPuller pull;
    
    // must be same size and order as cards JsonArray
    public List<CardHolder> cards;
    
    // list of all contained rarities
    public List<String> rarityPool;
    
    public boolean isSubSet;
    public String shownCode;
    
    public CardSet(String name, String code, String type, Date date, CardPuller pull, List<CardHolder> cards)
    {
        this.name = name;
        this.code = code;
        this.type = type;
        this.date = date;
        this.pull = pull;
        this.cards = cards;
        
        this.init();
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
            this.rarityPool = ImmutableList.of();
        }
        else
        {
            JsonArray cards = j.get(JsonKeys.CARDS).getAsJsonArray();
            this.cards = new ArrayList<>(cards.size());
            this.rarityPool = new LinkedList<>();
            
            JsonObject c;
            long id;
            Properties card;
            byte imageIndex;
            String rarity;
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
                
                rarity = c.get(JsonKeys.RARITY).getAsString();
                
                if(!this.rarityPool.contains(rarity))
                {
                    this.rarityPool.add(rarity);
                }
                
                this.cards.add(new CardHolder(card, imageIndex, rarity, c.get(JsonKeys.CODE).getAsString()));
            }
        }
        
        this.init();
    }
    
    protected void init()
    {
        this.isSubSet = this.type.equals("Sub-Set");
        this.shownCode = this.code.split("_")[0];
    }
    
    public void postDBInit()
    {
        this.pull.postDBInit();
    }
    
    public List<ItemStack> open(Random random)
    {
        return this.pull.open(random);
    }
    
    public SortedArraySet<CardHolder> getAllCardEntries()
    {
        SortedArraySet<CardHolder> sortedSet = SortedArraySet.newSet(0);
        this.addAllCardEntries(sortedSet);
        return sortedSet;
    }
    
    public void addAllCardEntries(SortedArraySet<CardHolder> sortedSet)
    {
        this.pull.addAllCardEntries(sortedSet);
    }
    
    public void addItemInformation(List<ITextComponent> tooltip)
    {
        tooltip.add(new StringTextComponent(this.name));
        tooltip.add(new StringTextComponent(this.type));
    }
    
    public void addInformation(List<ITextComponent> tooltip)
    {
        tooltip.add(new StringTextComponent(this.name));
        tooltip.add(new StringTextComponent(this.type));
        tooltip.add(new StringTextComponent(YdmDatabase.SET_DATE_PARSER.format(this.date)));
        tooltip.add(new StringTextComponent(this.shownCode));
        tooltip.add(StringTextComponent.EMPTY);
        this.pull.addInformation(tooltip);
    }
    
    public boolean isIndependentAndItem()
    {
        return this != CardSet.DUMMY && !this.isSubSet && this.name != null && this.date != null;
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
