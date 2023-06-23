package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.duel.DeckSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class PatreonDeckBoxItem extends DeckBoxItem
{
    public PatreonDeckBoxItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
    {
        if(allowedIn(group))
        {
            super.fillItemCategory(group, items);
            
            if(YdmDatabase.databaseReady)
            {
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
