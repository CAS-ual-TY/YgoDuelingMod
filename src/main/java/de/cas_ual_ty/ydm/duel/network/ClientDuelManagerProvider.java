package de.cas_ual_ty.ydm.duel.network;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DeckSource;
import de.cas_ual_ty.ydm.duel.DuelChatMessage;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ShowCardAction;
import de.cas_ual_ty.ydm.duel.action.ShowZoneAction;
import de.cas_ual_ty.ydm.duel.action.ViewZoneAction;
import de.cas_ual_ty.ydm.duel.screen.DuelContainerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;

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
        ClientDuelManagerProvider.doForScreen((screen) -> screen.duelStateChanged());
    }
    
    @Override
    public void handleAction(Action action)
    {
        // TODO animation
        
        IDuelManagerProvider.super.handleAction(action);
        
        if(action instanceof ViewZoneAction)
        {
            ViewZoneAction a = (ViewZoneAction)action;
            ClientDuelManagerProvider.doForScreen((screen) ->
            {
                if(screen.getZoneOwner() == a.sourceZone.getOwner())
                {
                    screen.viewZone(a.sourceZone);
                }
            });
        }
        else if(action instanceof ShowZoneAction)
        {
            ShowZoneAction a = (ShowZoneAction)action;
            ClientDuelManagerProvider.doForScreen((screen) ->
            {
                if(screen.getZoneOwner() != a.sourceZone.getOwner())
                {
                    screen.viewZone(a.sourceZone);
                }
            });
        }
        else if(action instanceof ShowCardAction)
        {
            ShowCardAction a = (ShowCardAction)action;
            ClientDuelManagerProvider.doForScreen((screen) ->
            {
                if(screen.getZoneOwner() != a.sourceZone.getOwner())
                {
                    screen.viewCards(a.sourceZone, ImmutableList.of(a.card));
                }
            });
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
            this.getDuelManager().player1Deck = DeckHolder.DUMMY;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            this.getDuelManager().player2Deck = DeckHolder.DUMMY;
        }
        
        ClientDuelManagerProvider.doForScreen((screen) -> screen.deckAccepted(role));
    }
    
    @Override
    public void receiveMessage(PlayerEntity player, DuelChatMessage message)
    {
        this.getDuelManager().messages.add(message);
    }
    
    public static void doForScreen(Consumer<DuelContainerScreen<? extends DuelContainer>> consumer)
    {
        @SuppressWarnings("resource")
        Screen screen = ClientProxy.getMinecraft().currentScreen;
        
        if(screen instanceof DuelContainerScreen)
        {
            consumer.accept((DuelContainerScreen<?>)screen);
        }
    }
}
