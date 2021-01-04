package de.cas_ual_ty.ydm.set;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.CardHolder;

public abstract class Pull
{
    public final CardSet set;
    
    public Pull(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        this.set = set;
    }
    
    public abstract List<CardHolder> open(Random random);
}
