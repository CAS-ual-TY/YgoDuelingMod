package de.cas_ual_ty.ydm.duel;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.deckbox.DeckProvider;
import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelManager
{
    public final boolean isRemote;
    public final IDuelTicker ticker;
    
    public DuelState duelState;
    
    public UUID player1Id;
    public UUID player2Id;
    
    public PlayerEntity player1;
    public PlayerEntity player2;
    public List<PlayerEntity> spectators;
    
    public boolean player1Ready;
    public boolean player2Ready;
    
    public PlayField playField;
    
    public List<Action> actions;
    public List<String> messages;
    
    public DeckHolder player1Deck;
    public DeckHolder player2Deck;
    
    public DuelManager(boolean isRemote, @Nullable IDuelTicker ticker)
    {
        this.isRemote = isRemote;
        this.ticker = ticker;
        this.spectators = new LinkedList<>();
        this.actions = new LinkedList<>();
        this.messages = new LinkedList<>();
        this.reset();
    }
    
    public void reset()
    {
        this.kickAllPlayers();
        this.duelState = DuelState.IDLE;
        this.resetPlayer1();
        this.resetPlayer2();
        this.resetSpectators();
        this.actions.clear();
        this.messages.clear();
        this.playField = new PlayField(this);
        this.player1Deck = null;
        this.player2Deck = null;
    }
    
    public void setDuelStateAndUpdate(DuelState duelState)
    {
        this.duelState = duelState;
        
        if(!this.isRemote)
        {
            this.updateDuelStateToAll();
        }
    }
    
    public void kickAllPlayers()
    {
        //TODO
        // maybe just sending the new idle state should kick everyone?
        
        // technically, if this is only called when the duel ends
        // reset() is called, so current spectators will not receive any more updates
        // from next duels on the same duelmanager
        // so they will stay in the end screen
        // so kicking shouldnt be necessary in that case
        
        // any other cases that require kicking all?
    }
    
    public void setPlayer1(PlayerEntity player)
    {
        this.player1 = player;
        this.player1Id = player.getUniqueID();
    }
    
    public void setPlayer2(PlayerEntity player)
    {
        this.player2 = player;
        this.player2Id = player.getUniqueID();
    }
    
    public void setSpectator(PlayerEntity player)
    {
        this.spectators.add(player);
    }
    
    public void removePlayer1()
    {
        if(this.hasStarted())
        {
            // set it to null, but keep the id, in case of a disconnect
            this.player1 = null;
        }
        else
        {
            this.resetPlayer1();
        }
        
        this.onPlayerRemoved();
    }
    
    public void resetPlayer1()
    {
        this.player1 = null;
        this.player1Id = null;
        this.player1Ready = false;
    }
    
    public void removePlayer2()
    {
        if(this.hasStarted())
        {
            this.player2 = null;
        }
        else
        {
            this.resetPlayer2();
        }
        
        this.onPlayerRemoved();
    }
    
    public void resetPlayer2()
    {
        this.player2 = null;
        this.player2Id = null;
        this.player2Ready = false;
    }
    
    public void removeSpectator(PlayerEntity player)
    {
        this.spectators.remove(player);
    }
    
    public void resetSpectators()
    {
        this.spectators.clear();
    }
    
    public boolean hasStarted()
    {
        return this.duelState == DuelState.DUELING || this.duelState == DuelState.SIDING;
    }
    
    // either closed container or deselected role
    public void onPlayerRemoved()
    {
        if(!this.hasStarted())
        {
            if(this.duelState == DuelState.PREPARING)
            {
                this.setDuelStateAndUpdate(DuelState.IDLE);
            }
            
            this.checkReadyState();
        }
    }
    
    public void bothReady()
    {
        // both players are ready
        this.setDuelStateAndUpdate(DuelState.PREPARING);
        this.sendDeckProvidersToPlayers();
    }
    
    public void sendDeckProvidersToPlayers()
    {
        this.sendDeckProvidersTo(this.player1);
        this.sendDeckProvidersTo(this.player2);
    }
    
    public void requestDeck(ResourceLocation deckProviderRL, PlayerEntity player)
    {
        PlayerRole role = this.getRoleFor(player);
        
        if(role != PlayerRole.PLAYER1 && role != PlayerRole.PLAYER2)
        {
            return;
        }
        
        DeckProvider d = YDM.DECK_PROVIDERS_REGISTRY.getValue(deckProviderRL);
        
        if(d != null)
        {
            DeckHolder deck = d.provideDeck(player);
            
            if(deck != null)
            {
                this.sendDeckTo(player, deckProviderRL, deck);
            }
        }
    }
    
    public void requestReady(PlayerEntity player, boolean ready)
    {
        if(this.hasStarted())
        {
            return;
        }
        
        PlayerRole role = this.getRoleFor(player);
        
        if(role == PlayerRole.PLAYER1 || role == PlayerRole.PLAYER2)
        {
            this.updateReady(role, ready);
        }
        
        if(this.player1Ready && this.player2Ready)
        {
            this.bothReady();
        }
    }
    
    public void updateReady(PlayerRole role, boolean ready)
    {
        if(role == PlayerRole.PLAYER1)
        {
            this.player1Ready = ready;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            this.player2Ready = ready;
        }
        
        this.checkReadyState();
        
        if(!this.isRemote)
        {
            this.updateReadyToAll(role, ready);
        }
    }
    
    // if only 1 player there, unready all
    public void checkReadyState()
    {
        if(this.player1 == null || this.player2 == null)
        {
            this.player1Ready = false;
            this.player2Ready = false;
        }
    }
    
    public void onPlayerOpenContainer(PlayerEntity player)
    {
        PlayerRole role;
        
        // if it has started, give player back his role
        if(this.hasStarted())
        {
            role = this.getRoleFor(player);
            
            if(role == PlayerRole.PLAYER1)
            {
                this.setPlayer1(player);
            }
            else if(role == PlayerRole.PLAYER2)
            {
                this.setPlayer2(player);
            }
            else
            {
                role = PlayerRole.SPECTATOR;
                this.setSpectator(player);
            }
        }
        else
        {
            role = PlayerRole.SPECTATOR;
            
            // set spectator by default
            this.setSpectator(player);
        }
        
        // tell all the other players that this player has joined
        // apparently this is not called on client
        if(!this.isRemote)
        {
            this.updateRoleToAll(role, player);
        }
        
        /*// this is instead now done on request by client. At this point the client constructor has not been constructed yet, so packets dont work yet
        if(!this.isRemote)
        {
            this.sendAllTo(player);
        }
        */
    }
    
    public void onPlayerCloseContainer(PlayerEntity player)
    {
        // just call removal methods
        // they will differentiate between the game states
        
        PlayerRole role = this.getRoleFor(player);
        
        if(role == PlayerRole.PLAYER1)
        {
            this.removePlayer1();
            this.handlePlayerLeave(player, role);
        }
        else if(role == PlayerRole.PLAYER2)
        {
            this.removePlayer2();
            this.handlePlayerLeave(player, role);
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            this.removeSpectator(player);
        }
        
        if(!this.isRemote)
        {
            this.updateRoleToAll(null, player);
        }
        
    }
    
    @Nullable
    public PlayerRole getRoleFor(PlayerEntity player)
    {
        if(player.getUniqueID().equals(this.player1Id))
        {
            return PlayerRole.PLAYER1;
        }
        if(player.getUniqueID().equals(this.player2Id))
        {
            return PlayerRole.PLAYER2;
        }
        else if(this.spectators.contains(player))
        {
            return PlayerRole.SPECTATOR;
        }
        
        //TODO judge
        return null;
    }
    
    public List<PlayerRole> getAvailablePlayerRoles(PlayerEntity player)
    {
        // only the player roles
        
        List<PlayerRole> list = new LinkedList<>();
        
        if(this.player1Id == null)
        {
            list.add(PlayerRole.PLAYER1);
        }
        
        if(this.player2Id == null)
        {
            list.add(PlayerRole.PLAYER2);
        }
        
        return list;
    }
    
    public boolean canPlayerSelectRole(PlayerEntity player, PlayerRole role)
    {
        if(role == null) // means that he can leave
        {
            return true;
        }
        else if(role == PlayerRole.PLAYER1)
        {
            return this.player1Id == null;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return this.player2Id == null;
        }
        else
        {
            return role == PlayerRole.SPECTATOR; //TODO judge
        }
    }
    
    // player selects a role, if successful send update to everyone
    public void playerSelectRole(PlayerEntity player, PlayerRole role)
    {
        if(this.canPlayerSelectRole(player, role))
        {
            // remove previous role
            
            PlayerRole previous = this.getRoleFor(player);
            
            if(previous != null)
            {
                if(previous == PlayerRole.PLAYER1)
                {
                    this.removePlayer1();
                }
                else if(previous == PlayerRole.PLAYER2)
                {
                    this.removePlayer2();
                }
                else if(previous == PlayerRole.SPECTATOR)
                {
                    this.removeSpectator(player);
                }
            }
            
            // ---
            
            if(role == PlayerRole.PLAYER1)
            {
                this.setPlayer1(player);
            }
            else if(role == PlayerRole.PLAYER2)
            {
                this.setPlayer2(player);
            }
            else// if(role == PlayerRole.SPECTATOR) // player must have a default role //TODO judge
            {
                this.setSpectator(player);
            }
            
            if(!this.isRemote)
            {
                this.updateRoleToAll(role, player);
            }
        }
    }
    
    public List<ResourceLocation> getAvailableDeckProviders(PlayerEntity player)
    {
        List<ResourceLocation> list = new LinkedList<>();
        
        for(DeckProvider d : YDM.DECK_PROVIDERS_REGISTRY)
        {
            if(d.provideDeck(player) != null)
            {
                list.add(d.getRegistryName());
            }
        }
        
        return list;
    }
    
    // on client side the player can be null
    public void handlePlayerLeave(@Nullable PlayerEntity player, PlayerRole role)
    {
        if(this.hasStarted())
        {
            // TODO notify players in case actual duelist left
            // spectators can always be ignored
        }
    }
    
    public void duelResigned(Player winner)
    {
        this.setDuelStateAndUpdate(DuelState.END);
    }
    
    public void endDuelAndReset()
    {
        this.reset();
    }
    
    public void receiveActionFrom(PlayerRole role, Action action)
    {
        action.init(this.getPlayField());
        if(!this.isRemote)
        {
            this.sendActionToAll(action);
        }
        this.actions.add(action);
        action.doAction();
    }
    
    public void doForAllPlayers(Consumer<PlayerEntity> consumer)
    {
        if(this.player1 != null)
        {
            consumer.accept(this.player1);
        }
        
        if(this.player2 != null)
        {
            consumer.accept(this.player2);
        }
        
        for(PlayerEntity player : this.spectators)
        {
            consumer.accept(player);
        }
    }
    
    public void doForAllPlayersExcept(Consumer<PlayerEntity> consumer, PlayerEntity exception)
    {
        this.doForAllPlayers((player) ->
        {
            if(player != exception)
            {
                consumer.accept(player);
            }
        });
    }
    
    public void sendActionToAll(Action action)
    {
        this.doForAllPlayers((player) -> this.sendActionTo(player, action));
    }
    
    public void updateActionsToAll()
    {
        this.doForAllPlayers((player) -> this.sendActionsTo(player));
    }
    
    public void updateDuelStateToAll()
    {
        this.doForAllPlayers((player) -> this.sendDuelStateTo(player));
    }
    
    public void updateRoleToAll(@Nullable PlayerRole role, PlayerEntity rolePlayer)
    {
        this.doForAllPlayers((player) ->
        {
            this.updateRoleTo(player, role, rolePlayer);
        });
    }
    
    public void updateReadyToAll(PlayerRole role, boolean ready)
    {
        this.doForAllPlayers((player) ->
        {
            this.updateReadyTo(player, role, ready);
        });
    }
    
    // synchronize everything
    public void sendAllTo(PlayerEntity player)
    {
        this.sendDuelStateTo(player);
        
        if(this.player1 != null)
        {
            this.updateRoleTo(player, PlayerRole.PLAYER1, this.player1);
        }
        
        if(this.player2 != null)
        {
            this.updateRoleTo(player, PlayerRole.PLAYER2, this.player2);
        }
        
        for(PlayerEntity spectator : this.spectators)
        {
            this.updateRoleTo(player, PlayerRole.SPECTATOR, spectator);
        }
        
        this.updateReadyTo(player, PlayerRole.PLAYER1, this.player1Ready);
        this.updateReadyTo(player, PlayerRole.PLAYER2, this.player2Ready);
        
        // playfield is updated via actions
        
        if(this.duelState == DuelState.DUELING)
        {
            this.sendActionsTo(player);
        }
        
        // TODO synchronize messages (via actions?)
    }
    
    protected void sendActionTo(PlayerEntity player, Action action)
    {
    }
    
    protected void sendActionsTo(PlayerEntity player)
    {
    }
    
    protected void sendDuelStateTo(PlayerEntity player)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.UpdateDuelState(this.duelState));
    }
    
    protected void updateRoleTo(PlayerEntity player, PlayerRole role, PlayerEntity rolePlayer)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.UpdateRole(role, rolePlayer));
    }
    
    protected void updateReadyTo(PlayerEntity player, PlayerRole role, boolean ready)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.UpdateReady(role, ready));
    }
    
    protected void sendMessageTo(PlayerEntity player, String message)
    {
        // TODO
    }
    
    protected void sendDeckProvidersTo(PlayerEntity player)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendDeckProviders(this.getAvailableDeckProviders(player)));
    }
    
    protected void sendDeckTo(PlayerEntity player, ResourceLocation deckProviderRL, DeckHolder deck)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendDeck(deckProviderRL, deck));
    }
    
    protected <MSG> void sendGeneralPacketTo(ServerPlayerEntity player, MSG msg)
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
    
    // --- Getters ---
    
    public IDuelTicker getTicker()
    {
        return this.ticker;
    }
    
    public DuelState getDuelState()
    {
        return this.duelState;
    }
    
    public PlayField getPlayField()
    {
        return this.playField;
    }
    
    public List<Action> getActions()
    {
        return this.actions;
    }
    
    public List<String> getMessages()
    {
        return this.messages;
    }
}
