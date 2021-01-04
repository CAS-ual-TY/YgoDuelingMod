package de.cas_ual_ty.ydm.duel;

import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CustomCards;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class DeckSource
{
    public static final Supplier<DeckSource> EMPTY_DECK = () -> new DeckSource(DeckHolder.DUMMY, YdmItems.CARD.createItemForCard(CustomCards.DUMMY_PROPERTIES));
    
    public DeckHolder deck;
    public ItemStack source;
    public ITextComponent name;
    
    public DeckSource(DeckHolder deck, ItemStack source, ITextComponent name)
    {
        this.deck = deck;
        this.source = source;
        this.name = name;
    }
    
    public DeckSource(DeckHolder deck, ItemStack source)
    {
        this(deck, source, source.getDisplayName());
    }
}
