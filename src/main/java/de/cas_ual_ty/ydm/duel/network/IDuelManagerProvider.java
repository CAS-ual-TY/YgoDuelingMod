package de.cas_ual_ty.ydm.duel.network;

import java.util.List;

import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DeckSource;
import de.cas_ual_ty.ydm.duel.DuelChatMessage;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerEntity;

public interface IDuelManagerProvider
{
    public DuelManager getDuelManager();
    
    public default DuelMessageHeader getMessageHeader()
    {
        return this.getDuelManager().headerFactory.get();
    }
    
    public default void updateDuelState(DuelState duelState)
    {
        this.getDuelManager().setDuelStateAndUpdate(duelState);
    }
    
    public default void handleAction(Action action)
    {
        action.init(this.getDuelManager().getPlayField());
        action.doAction();
    }
    
    public default void handleAllActions(List<Action> actions)
    {
        // just do all actions without animation
        
        for(Action action : actions)
        {
            action.init(this.getDuelManager().getPlayField());
            action.doAction();
        }
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
    
    public default void receiveMessage(PlayerEntity player, DuelChatMessage message)
    {
    }
}
