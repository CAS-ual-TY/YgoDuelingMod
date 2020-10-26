package de.cas_ual_ty.ydm.duel;

import java.util.List;
import java.util.function.Consumer;

import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import net.minecraft.client.gui.screen.Screen;

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
        return this.duelManager;
    }
    
    @Override
    public void updateDuelState(DuelState duelState)
    {
        IDuelManagerProvider.super.updateDuelState(duelState);
        ClientDuelManagerProvider.doForScreen((screen) -> screen.reInit());
    }
    
    @Override
    public void handleAction(Action action)
    {
        // TODO animation
        
        IDuelManagerProvider.super.handleAction(action);
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
            this.getDuelManager().player1Deck = DeckHolder.DUMMY;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            this.getDuelManager().player2Deck = DeckHolder.DUMMY;
        }
        
        ClientDuelManagerProvider.doForScreen((screen) -> screen.reInit());
    }
    
    @SuppressWarnings("resource")
    public static void doForScreen(Consumer<IDuelScreen> consumer)
    {
        Screen screen = ClientProxy.getMinecraft().currentScreen;
        
        if(screen instanceof IDuelScreen)
        {
            consumer.accept((IDuelScreen)screen);
        }
    }
}
