package de.cas_ual_ty.ydm.set;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.CardHolder;

public class FullCardPuller extends CardPuller
{
    public FullCardPuller(JsonObject setJson, CardSet set)
    {
        super(setJson, set);
    }
    
    @Override
    public List<CardHolder> open(Random random)
    {
        return this.set.cards;
    }
}
