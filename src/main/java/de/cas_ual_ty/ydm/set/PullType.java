package de.cas_ual_ty.ydm.set;

import com.google.gson.JsonObject;

public enum PullType
{
    FULL("full", FullCardPuller::new), DISTRIBUTION("distribution", DistributionCardPuller::new), COMPOSITION("composition", CompositionCardPuller::new);
    
    public static final PullType[] VALUES = PullType.values();
    
    public final String s;
    public final Factory factory;
    
    PullType(String s, Factory factory)
    {
        this.s = s;
        this.factory = factory;
    }
    
    public static CardPuller createPull(String pullType, JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        for(PullType type : PullType.VALUES)
        {
            if(type.s.equals(pullType))
            {
                return type.factory.create(setJson, set);
            }
        }
        
        throw new IllegalArgumentException("No valid pull-type: " + pullType);
    }
    
    public interface Factory
    {
        CardPuller create(JsonObject setJson, CardSet set);
    }
}
