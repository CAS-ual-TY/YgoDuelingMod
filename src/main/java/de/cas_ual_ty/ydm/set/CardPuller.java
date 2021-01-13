package de.cas_ual_ty.ydm.set;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.util.text.ITextComponent;

public abstract class CardPuller
{
    public final CardSet set;
    
    public CardPuller(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        this.set = set;
    }
    
    public abstract List<CardHolder> open(Random random);
    
    public void addInformation(List<ITextComponent> tooltip)
    {
        
    }
}
