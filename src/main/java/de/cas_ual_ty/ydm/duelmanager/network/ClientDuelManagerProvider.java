package de.cas_ual_ty.ydm.duelmanager.network;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DuelContainer;
import de.cas_ual_ty.ydm.duel.screen.DuelContainerScreen;
import de.cas_ual_ty.ydm.duel.screen.DuelingDuelScreen;
import de.cas_ual_ty.ydm.duel.screen.PreparingDuelScreen;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import de.cas_ual_ty.ydm.duelmanager.action.ShowCardAction;
import de.cas_ual_ty.ydm.duelmanager.action.ShowZoneAction;
import de.cas_ual_ty.ydm.duelmanager.action.ViewZoneAction;
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
        
        if(action instanceof ViewZoneAction)
        {
            ViewZoneAction a = (ViewZoneAction)action;
            ClientDuelManagerProvider.doForDuelingScreen((screen) ->
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
            ClientDuelManagerProvider.doForDuelingScreen((screen) ->
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
            ClientDuelManagerProvider.doForDuelingScreen((screen) ->
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
        ClientDuelManagerProvider.doForPreparingScreen((screen) -> screen.populateDeckSources(deckSources));
    }
    
    @Override
    public void receiveDeck(int index, DeckHolder deck)
    {
        ClientDuelManagerProvider.doForPreparingScreen((screen) -> screen.receiveDeck(index, deck));
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
        
        ClientDuelManagerProvider.doForPreparingScreen((screen) -> screen.reInit());
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
    
    public static void doForPreparingScreen(Consumer<PreparingDuelScreen> consumer)
    {
        @SuppressWarnings("resource")
        Screen screen = ClientProxy.getMinecraft().currentScreen;
        
        if(screen instanceof PreparingDuelScreen)
        {
            consumer.accept((PreparingDuelScreen)screen);
        }
    }
    
    public static void doForDuelingScreen(Consumer<DuelingDuelScreen> consumer)
    {
        @SuppressWarnings("resource")
        Screen screen = ClientProxy.getMinecraft().currentScreen;
        
        if(screen instanceof DuelingDuelScreen)
        {
            consumer.accept((DuelingDuelScreen)screen);
        }
    }
}
