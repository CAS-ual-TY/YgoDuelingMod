package de.cas_ual_ty.ydm.set;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.JsonKeys;

public class CompositionCardPuller extends CardPuller
{
    public final List<String> subSetCodes;
    protected List<CardSet> subSets;
    
    @SuppressWarnings("unchecked")
    public CompositionCardPuller(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        super(setJson, set);
        
        JsonArray subSetsJson = setJson.get(JsonKeys.SUB_SETS).getAsJsonArray();
        
        this.subSetCodes = new ArrayList<>(subSetsJson.size());
        this.subSets = null;
        
        for(int i = 0; i < subSetsJson.size(); ++i)
        {
            this.subSetCodes.add(subSetsJson.get(i).getAsString());
        }
    }
    
    @Override
    public List<CardHolder> open(Random random)
    {
        if(this.subSets == null)
        {
            this.subSets = new LinkedList<>();
            
            CardSet subSet;
            for(String code : this.subSetCodes)
            {
                subSet = YdmDatabase.SETS_LIST.get(code);
                
                if(subSet == null)
                {
                    YDM.log("Can not find sub-set: " + code + " in set:" + this.set.code + " (" + this.set.name + ")");
                }
                else
                {
                    this.subSets.add(subSet);
                }
            }
        }
        
        List<CardHolder> list = new ArrayList<>(0);
        
        for(CardSet subSet : this.subSets)
        {
            list.addAll(subSet.open(random));
        }
        
        return list;
    }
    
    @Override
    public List<CardHolder> getAllCardEntries()
    {
        List<CardHolder> list = new ArrayList<>();
        
        for(CardSet subSet : this.subSets)
        {
            list.addAll(subSet.getAllCardEntries());
        }
        
        return list;
    }
}
