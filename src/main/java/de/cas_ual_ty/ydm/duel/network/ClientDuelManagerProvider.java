package de.cas_ual_ty.ydm.duel.network;

import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.*;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.screen.DuelContainerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Consumer;

public class ClientDuelManagerProvider implements IDuelManagerProvider
{
    protected DuelManager duelManager;
    
    public ClientDuelManagerProvider(DuelManager duelManager)
    {
        this.duelManager = duelManager;
    }
    
    @Override
    public DuelManager getDuelManager()
    {
        return duelManager;
    }
    
    @Override
    public void updateDuelState(DuelState duelState)
    {
        IDuelManagerProvider.super.updateDuelState(duelState);
        ClientDuelManagerProvider.doForScreen((screen) -> screen.duelStateChanged());
    }
    
    @Override
    public void handleAction(Action action)
    {
        ClientDuelManagerProvider.doForScreen((screen) -> screen.handleAction(action));
    }
    
    @Override
    public void handleAllActions(List<Action> actions)
    {
        // just do all actions without animation
        
        for(Action action : actions)
        {
            action.initClient(getDuelManager().getPlayField());
            action.doAction();
            getDuelManager().logAction(action);
        }
    }
    
    @Override
    public void receiveDeckSources(List<DeckSource> deckSources)
    {
        ClientDuelManagerProvider.doForScreen((screen) -> screen.populateDeckSources(deckSources));
    }
    
    @Override
    public void receiveDeck(int index, DeckHolder deck)
    {
        ClientDuelManagerProvider.doForScreen((screen) -> screen.receiveDeck(index, deck));
    }
    
    @Override
    public void deckAccepted(PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            getDuelManager().player1Deck = DeckHolder.DUMMY;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            getDuelManager().player2Deck = DeckHolder.DUMMY;
        }
        
        ClientDuelManagerProvider.doForScreen((screen) -> screen.deckAccepted(role));
    }
    
    @Override
    public void receiveMessage(Player player, DuelChatMessage message)
    {
        getDuelManager().messages.add(message);
    }
    
    public static void doForScreen(Consumer<DuelContainerScreen<? extends DuelContainer>> consumer)
    {
        @SuppressWarnings("resource")
        Screen screen = ClientProxy.getMinecraft().screen;
        
        if(screen instanceof DuelContainerScreen)
        {
            consumer.accept((DuelContainerScreen<?>) screen);
        }
    }
}
