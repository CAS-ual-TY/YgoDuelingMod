package de.cas_ual_ty.ydm.set;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        
        for(CardHolder c : set.cards)
        {
            list.add(YdmItems.CARD.get().createItemForCardHolder(c));
        }
        
        return list;
    }
    
    @Override
    public void addInformation(List<Component> tooltip)
    {
        if(set.cards.size() <= 10)
        {
            for(CardHolder ch : set.cards)
            {
                tooltip.add(Component.literal("\"" + ch.getCard().getName() + "\""));
            }
        }
        else if(set.name != null)
        {
            tooltip.add(Component.literal("1x \"" + set.name + "\""));
        }
    }
    
    @Override
    public boolean addInformationInComposition()
    {
        return set.cards.size() <= 10 || set.name != null;
    }
}
