package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.duel.DeckSource;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class PatreonDeckBoxItem extends DeckBoxItem
{
    public PatreonDeckBoxItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
    {
        if(allowdedIn(group))
        {
            super.fillItemCategory(group, items);
            
            if(YdmDatabase.databaseReady)
            {
                items.add(makeItemStackFromDeckSource(CustomDecks.getOjamaDeck()));
                
                for(DeckSource s : CustomDecks.getAllPatreonDeckSources())
                {
                    items.add(makeItemStackFromDeckSource(s));
                }
            }
        }
    }
    
    public ItemStack makeItemStackFromDeckSource(DeckSource s)
    {
        ItemStack itemStack = new ItemStack(YdmItems.PATREON_DECK_BOX);
        setDeckHolder(itemStack, s.deck);
        itemStack.setHoverName(s.name);
        return itemStack;
    }
}
