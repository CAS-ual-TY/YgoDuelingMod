package de.cas_ual_ty.ydm.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.item.ItemStack;

public class FullCardPuller extends CardPuller
{
    public FullCardPuller(JsonObject setJson, CardSet set)
    {
        super(setJson, set);
    }
    
    @Override
    public List<ItemStack> open(Random random)
    {
        List<ItemStack> list = new ArrayList<>();
        
        for(CardHolder c : this.set.cards)
        {
            list.add(YdmItems.CARD.createItemForCardHolder(c));
        }
        
        return list;
    }
    
    @Override
    public boolean addInformationInComposition()
    {
        return false;
    }
}
