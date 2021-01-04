package de.cas_ual_ty.ydm.set;

import com.google.gson.JsonObject;

public enum PullType
{
    FULL("full", FullPull::new), DISTRIBUTION("distribution", DistributionPull::new), COMPOSITION("composition", CompositionPull::new);
    
    public static final PullType[] VALUES = PullType.values();
    
    public final String s;
    public final Factory factory;
    
    private PullType(String s, Factory factory)
    {
        this.s = s;
        this.factory = factory;
    }
    
    public static Pull createPull(String pullType, JsonObject setJson, CardSet set) throws IllegalArgumentException
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
    
    public static interface Factory
    {
        public Pull create(JsonObject setJson, CardSet set);
    }
}
