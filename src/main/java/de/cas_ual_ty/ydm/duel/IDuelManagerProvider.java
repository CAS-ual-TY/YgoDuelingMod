package de.cas_ual_ty.ydm.duel;

import java.util.List;

import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageHeader;

public interface IDuelManagerProvider
{
    public DuelManager getDuelManager();
    
    public default DuelMessageHeader getMessageHeader()
    {
        return this.getDuelManager().header;
    }
    
    public default void updateDuelState(DuelState duelState)
    {
        this.getDuelManager().setDuelStateAndUpdate(duelState);
    }
    
    public default void handleAction(PlayerRole source, Action action)
    {
        action.init(this.getDuelManager().getPlayField());
        action.doAction();
    }
    
    public default void receiveDeckSources(List<DeckSource> deckSources)
    {
    }
    
    public default void receiveDeck(int index, DeckHolder deck)
    {
    }
    
    public default void deckAccepted(PlayerRole role)
    {
    }
}
