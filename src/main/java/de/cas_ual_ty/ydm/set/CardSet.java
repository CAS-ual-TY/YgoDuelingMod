package de.cas_ual_ty.ydm.set;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.JsonKeys;

public class CardSet
{
    public String name;
    public String code;
    public String type;
    public Date date;
    public Pull pull;
    
    // must be same size and order as cards JsonArray
    public List<CardHolder> cards;
    
    public CardSet(String name, String code, String type, Date date, Pull pull, List<CardHolder> cards)
    {
        this.name = name;
        this.code = code;
        this.type = type;
        this.date = date;
        this.pull = pull;
        this.cards = cards;
    }
    
    public CardSet(JsonObject j, List<CardHolder> cards) throws IllegalArgumentException
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
        
        if(j.has(JsonKeys.DATE))
        {
            String date = j.get(JsonKeys.DATE).getAsString();
            try
            {
                this.date = YdmDatabase.SET_DATE_PARSER.parse(date);
            }
            catch (ParseException e)
            {
                YDM.debug("Can not parse date: " + date);
                throw new IllegalArgumentException(e);
            }
        }
        
        this.pull = PullType.createPull(j.get(JsonKeys.PULL_TYPE).getAsString(), j, this);
        this.cards = cards;
    }
    
    public List<CardHolder> open(Random random)
    {
        return this.pull.open(random);
    }
}
