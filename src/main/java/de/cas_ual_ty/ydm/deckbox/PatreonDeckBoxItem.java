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
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if(this.isInGroup(group))
        {
            super.fillItemGroup(group, items);
            
            if(YdmDatabase.databaseReady)
            {
                items.add(this.makeItemStackFromDeckSource(DeckSource.getOjamaDeck()));
                
                for(DeckSource s : DeckSource.getAllPatreonDeckSources())
                {
                    items.add(this.makeItemStackFromDeckSource(s));
                }
            }
        }
    }
    
    public ItemStack makeItemStackFromDeckSource(DeckSource s)
    {
        ItemStack itemStack = new ItemStack(YdmItems.PATREON_DECK_BOX);
        this.setDeckHolder(itemStack, s.deck);
        itemStack.setDisplayName(s.name);
        return itemStack;
    }
}
