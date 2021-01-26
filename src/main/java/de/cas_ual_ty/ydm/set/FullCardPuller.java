package de.cas_ual_ty.ydm.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

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
    public void addInformation(List<ITextComponent> tooltip)
    {
        for(CardHolder ch : this.set.cards)
        {
            tooltip.add(new StringTextComponent("\"" + ch.getCard().getName() + "\""));
        }
    }
    
    @Override
    public boolean addInformationInComposition()
    {
        return this.set.cards.size() <= 20;
    }
}
