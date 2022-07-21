package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.LinkedList;
import java.util.List;

public class FindDecksEvent extends PlayerEvent
{
    public List<DeckSource> decksList;
    
    public FindDecksEvent(PlayerEntity player, DuelManager duelManager)
    {
        super(player);
        decksList = new LinkedList<>();
    }
    
    public FindDecksEvent addDeck(DeckHolder deck, ItemStack itemStack)
    {
        decksList.add(new DeckSource(deck, itemStack));
        return this;
    }
    
    public FindDecksEvent addDeck(DeckSource deck)
    {
        decksList.add(deck);
        return this;
    }
}
