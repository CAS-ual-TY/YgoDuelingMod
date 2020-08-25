package de.cas_ual_ty.ydm.playmat;

import java.util.List;
import java.util.function.Consumer;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ClientProxy;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DeckSource;
import de.cas_ual_ty.ydm.duel.DuelMessages;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.PacketDistributor;

public class PlaymatClientContainer extends PlaymatContainer
{
    public PlaymatClientContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(type, id, playerInventory, extraData);
    }
    
    @Override
    public void updateDuelState(DuelState duelState)
    {
        super.updateDuelState(duelState);
        this.doForScreen((screen) -> screen.reInit());
    }
    
    @Override
    public void handleAction(PlayerRole source, Action action)
    {
        // TODO animation
        super.handleAction(source, action);
    }
    
    @Override
    public void receiveDeckSources(List<DeckSource> deckSources)
    {
        super.receiveDeckSources(deckSources);
        this.doForScreen((screen) -> screen.populateDeckSources(deckSources));
    }
    
    @Override
    public void receiveDeck(int index, DeckHolder deck)
    {
        super.receiveDeck(index, deck);
        this.doForScreen((screen) -> screen.receiveDeck(index, deck));
    }
    
    @Override
    public void deckAccepted(PlayerRole role)
    {
        super.deckAccepted(role);
        
        if(role == PlayerRole.PLAYER1)
        {
            this.getDuelManager().player1Deck = DeckHolder.DUMMY;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            this.getDuelManager().player2Deck = DeckHolder.DUMMY;
        }
        
        this.doForScreen((screen) -> screen.reInit());
    }
    
    @Override
    public void onContainerOpened(PlayerEntity player)
    {
        super.onContainerOpened(player);
        
        // request full update
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestFullUpdate());
    }
    
    // only called on own client
    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);
        
        // no need to keep any data when closing on client
        this.getDuelManager().reset();
    }
    
    @SuppressWarnings("resource")
    public void doForScreen(Consumer<PlaymatScreen> consumer)
    {
        Screen screen = ClientProxy.getMinecraft().currentScreen;
        
        if(screen instanceof PlaymatScreen)
        {
            consumer.accept((PlaymatScreen)screen);
        }
    }
}
