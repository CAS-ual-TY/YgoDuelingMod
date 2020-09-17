package de.cas_ual_ty.ydm.duelmanager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import de.cas_ual_ty.ydm.duelmanager.action.ActionType;
import de.cas_ual_ty.ydm.duelmanager.action.Populate;
import de.cas_ual_ty.ydm.duelmanager.playfield.PlayField;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneOwner;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneType;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
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
    
    public List<DeckSource> player1Decks;
    public List<DeckSource> player2Decks;
    
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
        this.duelState = DuelState.IDLE;
        this.resetPlayer1();
        this.resetPlayer2();
        this.resetSpectators();
        this.actions.clear();
        this.messages.clear();
        this.playField = new PlayField(this, this.getZoneTypes());
        this.player1Deck = null;
        this.player2Deck = null;
    }
    
    public List<ZoneType> getZoneTypes()
    {
        List<ZoneType> list = new ArrayList<>(11);
        
        list.add(ZoneTypes.HAND);
        list.add(ZoneTypes.DECK);
        list.add(ZoneTypes.SPELL_TRAP);
        list.add(ZoneTypes.EXTRA_DECK);
        list.add(ZoneTypes.GRAVEYARD);
        list.add(ZoneTypes.MONSTER);
        list.add(ZoneTypes.FIELD_SPELL);
        list.add(ZoneTypes.BANISHED);
        list.add(ZoneTypes.EXTRA);
        list.add(ZoneTypes.EXTRA_MONSTER_RIGHT);
        list.add(ZoneTypes.EXTRA_MONSTER_LEFT);
        
        return list;
    }
    
    public void setDuelStateAndUpdate(DuelState duelState)
    {
        this.duelState = duelState;
        
        if(!this.isRemote)
        {
            this.updateDuelStateToAll();
        }
    }
    
    public void kickAllPlayersAndReset()
    {
        this.doForAllPlayers(this::kickPlayer);
        this.reset();
    }
    
    public void kickPlayer(PlayerEntity player)
    {
        player.closeScreen();
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
            this.player1Decks = null;
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
        this.player1Decks = null;
    }
    
    public void removePlayer2()
    {
        if(this.hasStarted())
        {
            this.player2 = null;
            this.player2Decks = null;
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
        this.player2Decks = null;
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
        this.findDecksForPlayers();
        this.sendDecksToPlayers();
    }
    
    public void findDecksForPlayers()
    {
        this.player1Decks = this.getAvailableDecksFor(this.player1);
        this.player2Decks = this.getAvailableDecksFor(this.player2);
    }
    
    public List<DeckSource> getAvailableDecksFor(PlayerEntity player)
    {
        if(this.hasStarted())
        {
            return ImmutableList.of();
        }
        
        FindDecksEvent e = new FindDecksEvent(player, this);
        MinecraftForge.EVENT_BUS.post(e);
        return e.decksList;
    }
    
    public void sendDecksToPlayers()
    {
        if(this.player1Decks.isEmpty())
        {
            this.kickPlayer(this.player1);
        }
        
        if(this.player2Decks.isEmpty())
        {
            this.kickPlayer(this.player2);
        }
        
        if(this.player1Decks.isEmpty() || this.player2Decks.isEmpty())
        {
            return;
        }
        
        this.sendDecksTo(this.player1, this.player1Decks);
        this.sendDecksTo(this.player2, this.player2Decks);
    }
    
    public void requestDeck(int index, PlayerEntity player)
    {
        if(this.hasStarted())
        {
            return;
        }
        
        PlayerRole role = this.getRoleFor(player);
        List<DeckSource> list = null;
        
        if(role == PlayerRole.PLAYER1)
        {
            list = this.player1Decks;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            list = this.player2Decks;
        }
        
        if(list == null || index < 0 || index >= list.size())
        {
            return;
        }
        
        DeckHolder deck = list.get(index).deck;
        
        this.sendDeckTo(player, index, deck);
    }
    
    public void startDuel()
    {
        this.populatePlayField();
        this.setDuelStateAndUpdate(DuelState.DUELING);
    }
    
    public void populatePlayField()
    {
        byte deckOffset = (byte)this.getPlayField().getSingleZone(ZoneTypes.DECK, ZoneOwner.PLAYER1).index;
        byte extraDeckOffset = (byte)this.getPlayField().getSingleZone(ZoneTypes.EXTRA_DECK, ZoneOwner.PLAYER1).index;
        
        // send main decks
        this.sendActionToAll(new Populate(ActionType.POPULATE, (byte)(deckOffset + this.getPlayField().player1Offset),
            this.player1Deck.getMainDeck().stream().map((card) -> new DuelCard(card, false, CardPosition.FACE_DOWN, ZoneOwner.PLAYER1)).collect(Collectors.toList())));
        this.sendActionToAll(new Populate(ActionType.POPULATE, (byte)(deckOffset + this.getPlayField().player2Offset),
            this.player2Deck.getMainDeck().stream().map((card) -> new DuelCard(card, false, CardPosition.FACE_DOWN, ZoneOwner.PLAYER2)).collect(Collectors.toList())));
        
        // send extra decks
        this.sendActionToAll(new Populate(ActionType.POPULATE, (byte)(extraDeckOffset + this.getPlayField().player1Offset),
            this.player1Deck.getExtraDeck().stream().map((card) -> new DuelCard(card, false, CardPosition.FACE_DOWN, ZoneOwner.PLAYER1)).collect(Collectors.toList())));
        this.sendActionToAll(new Populate(ActionType.POPULATE, (byte)(extraDeckOffset + this.getPlayField().player2Offset),
            this.player2Deck.getExtraDeck().stream().map((card) -> new DuelCard(card, false, CardPosition.FACE_DOWN, ZoneOwner.PLAYER2)).collect(Collectors.toList())));
    }
    
    public void chooseDeck(int index, PlayerEntity player)
    {
        PlayerRole role = this.getRoleFor(player);
        List<DeckSource> list = null;
        
        if(role == PlayerRole.PLAYER1)
        {
            list = this.player1Decks;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            list = this.player2Decks;
        }
        
        if(list == null || index < 0 || index >= list.size())
        {
            return;
        }
        
        DeckHolder deck = list.get(index).deck;
        
        if(deck != null)
        {
            if(role == PlayerRole.PLAYER1)
            {
                this.player1Deck = deck;
                this.updateDeckAcceptedToAll(PlayerRole.PLAYER1);
            }
            else if(role == PlayerRole.PLAYER2)
            {
                this.player2Deck = deck;
                this.updateDeckAcceptedToAll(PlayerRole.PLAYER2);
            }
            
            if(this.player1Deck != null && this.player2Deck != null)
            {
                this.startDuel();
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
        
        if(this.getAvailableDecksFor(player).isEmpty())
        {
            return list;
        }
        
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
    
    // on client side the player can be null
    public void handlePlayerLeave(@Nullable PlayerEntity player, PlayerRole role)
    {
        if(this.hasStarted())
        {
            // TODO notify players in case actual duelist left
            // spectators can always be ignored
            
            if(this.player1 == null && this.player2 == null)
            {
                this.reset(); // TODO do ticking instead
            }
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
        this.doForAllPlayers((player) -> this.sendActionTo(player, null, action));
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
    
    public void updateDeckAcceptedToAll(PlayerRole role)
    {
        this.doForAllPlayers((player) -> this.sendDeckAcceptedTo(player, role));
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
    
    protected void sendActionTo(PlayerEntity player, PlayerRole source, Action action)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.DuelAction(source, action));
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
    
    protected void sendDecksTo(PlayerEntity player, List<DeckSource> list)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendAvailableDecks(list));
    }
    
    protected void sendDeckTo(PlayerEntity player, int index, DeckHolder deck)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendDeck(index, deck));
    }
    
    protected void sendDeckAcceptedTo(PlayerEntity player, PlayerRole acceptedOf)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.DeckAccepted(acceptedOf));
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
