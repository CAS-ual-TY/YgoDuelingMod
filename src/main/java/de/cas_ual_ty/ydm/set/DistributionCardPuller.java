package de.cas_ual_ty.ydm.set;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.JsonKeys;

public class DistributionCardPuller extends CardPuller
{
    public final Distribution distribution;
    
    public DistributionCardPuller(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        super(setJson, set);
        
        String distributionName = setJson.get(JsonKeys.DISTRIBUTION).getAsString();
        
        this.distribution = YdmDatabase.DISTRIBUTIONS_LIST.get(distributionName);
        
        if(this.distribution == null)
        {
            throw new IllegalArgumentException("Cannot find distribution: " + distributionName);
        }
    }
    
    @Override
    public List<CardHolder> open(Random random)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
