package de.cas_ual_ty.ydm.duelmanager;

import java.util.LinkedList;
import java.util.List;

import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class FindDecksEvent extends PlayerEvent
{
    public List<DeckSource> decksList;
    
    public FindDecksEvent(PlayerEntity player, DuelManager duelManager)
    {
        super(player);
        this.decksList = new LinkedList<>();
    }
    
    public FindDecksEvent addDeck(DeckHolder deck, ItemStack itemStack)
    {
        this.decksList.add(new DeckSource(deck, itemStack));
        return this;
    }
    
    public FindDecksEvent addDeck(DeckHolder deck)
    {
        this.decksList.add(new DeckSource(deck));
        return this;
    }
    
    public FindDecksEvent addDeck(DeckSource deck)
    {
        this.decksList.add(deck);
        return this;
    }
}
