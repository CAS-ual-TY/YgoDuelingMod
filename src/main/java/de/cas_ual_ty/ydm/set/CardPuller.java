package de.cas_ual_ty.ydm.set;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.text.ITextComponent;

public abstract class CardPuller
{
    public final CardSet set;
    
    public CardPuller(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        this.set = set;
    }
    
    public void postDBInit()
    {
        
    }
    
    public abstract List<ItemStack> open(Random random);
    
    public void addInformation(List<ITextComponent> tooltip)
    {
        
    }
    
    public abstract boolean addInformationInComposition();
    
    public void addAllCardEntries(SortedArraySet<CardHolder> sortedSet)
    {
        this.set.cards.forEach(sortedSet::add);
    }
}
