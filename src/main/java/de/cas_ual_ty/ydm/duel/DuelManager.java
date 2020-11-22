package de.cas_ual_ty.ydm.duel;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CustomCards;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionTypes;
import de.cas_ual_ty.ydm.duel.action.IAnnouncedAction;
import de.cas_ual_ty.ydm.duel.action.PopulateAction;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.PlayFieldType;
import de.cas_ual_ty.ydm.duel.playfield.PlayFieldTypes;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneInteraction;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

public class DuelManager
{
    public final boolean isRemote;
    public final Supplier<DuelMessageHeader> headerFactory;
    
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
    public List<DuelChatMessage> messages;
    
    public List<DeckSource> player1Decks;
    public List<DeckSource> player2Decks;
    
    public DeckHolder player1Deck;
    public DeckHolder player2Deck;
    
    public Random random;
    
    public DuelManager(boolean isRemote, Supplier<DuelMessageHeader> header)
    {
        this.isRemote = isRemote;
        this.headerFactory = header;
        this.spectators = new LinkedList<>();
        this.actions = new LinkedList<>();
        this.messages = new LinkedList<>();
        this.random = new Random(YDM.random.nextLong());
        this.reset();
    }
    
    public void playerOpenContainer(PlayerEntity player)
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
            this.updateRoleToAllExceptRolePlayer(role, player);
        }
        
        /*// this is instead now done on request by client. At this point the client constructor has not been constructed yet, so packets dont work yet
        if(!this.isRemote)
        {
            this.sendAllTo(player);
        }
        */
    }
    
    public void playerCloseContainer(PlayerEntity player)
    {
        // just call removal methods
        // they will differentiate between the game states
        
        if(player == null)
        {
            return;
        }
        
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
        
        if(this.player1 == null && this.player2 == null && this.spectators.isEmpty())
        {
            this.onEveryoneLeft();
        }
    }
    
    // client equivalent
    // need a seperate method because if the player leaves the server
    // you can not fetch it using world.get player by uuid (or some method name like that)
    public void playerCloseContainerClient(UUID playerUUID)
    {
        PlayerRole role = this.getRoleFor(playerUUID);
        
        if(role == PlayerRole.PLAYER1)
        {
            this.removePlayer1();
        }
        else if(role == PlayerRole.PLAYER2)
        {
            this.removePlayer2();
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            this.removeSpectator(playerUUID);
        }
        
        if(this.player1 == null && this.player2 == null && this.spectators.isEmpty())
        {
            this.onEveryoneLeft();
        }
    }
    
    protected void onEveryoneLeft()
    {
        this.reset();
    }
    
    // either closed container or deselected role
    protected void onPlayerRemoved()
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
    
    protected void handlePlayerLeave(PlayerEntity player, PlayerRole role)
    {
        if(this.hasStarted())
        {
            // TODO notify players in case actual duelist left
            // spectators can always be ignored
            
            // do not reset here
            // this is done in onEveryoneLeft
        }
    }
    
    public PlayFieldType getPlayFieldType()
    {
        return PlayFieldTypes.DEFAULT;
    }
    
    public void reset()
    {
        this.duelState = DuelState.IDLE;
        this.resetPlayer1();
        this.resetPlayer2();
        this.resetSpectators();
        this.actions.clear();
        this.messages.clear();
        this.playField = new PlayField(this, this.getPlayFieldType());
        this.player1Deck = null;
        this.player2Deck = null;
    }
    
    protected void resetPlayer1()
    {
        this.player1 = null;
        this.player1Id = null;
        this.player1Ready = false;
        this.player1Decks = null;
    }
    
    protected void resetPlayer2()
    {
        this.player2 = null;
        this.player2Id = null;
        this.player2Ready = false;
        this.player2Decks = null;
    }
    
    protected void resetSpectators()
    {
        this.spectators.clear();
    }
    
    protected void setPlayer1(PlayerEntity player)
    {
        this.player1 = player;
        this.player1Id = player.getUniqueID();
    }
    
    protected void setPlayer2(PlayerEntity player)
    {
        this.player2 = player;
        this.player2Id = player.getUniqueID();
    }
    
    protected void setSpectator(PlayerEntity player)
    {
        this.spectators.add(player);
    }
    
    protected void removePlayer1()
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
    
    protected void removePlayer2()
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
    
    protected void removeSpectator(PlayerEntity player)
    {
        this.spectators.remove(player);
    }
    
    protected void removeSpectator(UUID playerUUID)
    {
        for(int i = 0; i < this.spectators.size(); ++i)
        {
            if(this.spectators.get(i).getUniqueID().equals(playerUUID))
            {
                this.spectators.remove(i);
                break;
            }
        }
    }
    
    protected void kickPlayer(PlayerEntity player)
    {
        player.closeScreen();
    }
    
    @Nullable
    public PlayerEntity getPlayer(ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return this.player1;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return this.player2;
        }
        else
        {
            return null;
        }
    }
    
    @Nullable
    public PlayerRole getRoleFor(PlayerEntity player)
    {
        return this.getRoleFor(player.getUniqueID());
    }
    
    @Nullable
    public PlayerRole getRoleFor(UUID playerUUID)
    {
        if(playerUUID.equals(this.player1Id))
        {
            return PlayerRole.PLAYER1;
        }
        if(playerUUID.equals(this.player2Id))
        {
            return PlayerRole.PLAYER2;
        }
        else if(this.spectators.stream().anyMatch((player) -> player.getUniqueID().equals(playerUUID)))
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
            return this.player1Id == null || this.player1Id.equals(player.getUniqueID());
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return this.player2Id == null || this.player2Id.equals(player.getUniqueID());
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
    
    public void setDuelStateAndUpdate(DuelState duelState)
    {
        this.duelState = duelState;
        
        if(!this.isRemote)
        {
            this.updateDuelStateToAll();
        }
    }
    
    public void receiveMessageFromClient(PlayerEntity player, ITextComponent message)
    {
        /*//FIXME
        if(message.getString().trim().isEmpty())
        {
            return;
        }
        */
        IFormattableTextComponent name = (IFormattableTextComponent)player.getName();
        
        PlayerRole role;
        
        //        if(this.getDuelState() == DuelState.IDLE)
        //        {
        //            role = PlayerRole.SPECTATOR;
        //        }
        //        else
        //        {
        role = this.getRoleFor(player);
        //        }
        
        DuelChatMessage chatMessage = new DuelChatMessage(message, name, role);
        this.logAndSendMessage(chatMessage);
    }
    
    protected void logAndSendMessage(DuelChatMessage chatMessage)
    {
        this.messages.add(chatMessage);
        this.sendMessageToAll(chatMessage);
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
            this.onBothReady();
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
    protected void checkReadyState()
    {
        if(this.player1 == null || this.player2 == null)
        {
            this.player1Ready = false;
            this.player2Ready = false;
        }
    }
    
    protected void onBothReady()
    {
        // both players are ready
        this.setDuelStateAndUpdate(DuelState.PREPARING);
        this.findDecksForPlayers();
        this.sendDecksToPlayers();
    }
    
    public boolean hasStarted()
    {
        return this.duelState == DuelState.DUELING || this.duelState == DuelState.SIDING;
    }
    
    protected void findDecksForPlayers()
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
        e.decksList.add(new DeckSource(DeckHolder.DUMMY, YdmItems.CARD.createItemForCard(CustomCards.DUMMY_CARD))); // empty deck
        return e.decksList;
    }
    
    protected void sendDecksToPlayers()
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
    
    protected void startDuel()
    {
        this.sendInfoMessageToAll(new TranslationTextComponent("container.ydm.duel.info_start"));
        this.populatePlayField();
        this.setDuelStateAndUpdate(DuelState.DUELING);
    }
    
    protected void populatePlayField()
    {
        // send main decks
        this.doAction(new PopulateAction(ActionTypes.POPULATE, this.playField.player1Deck.index,
            this.player1Deck.getMainDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER1)).collect(Collectors.toList())));
        this.doAction(new PopulateAction(ActionTypes.POPULATE, this.playField.player2Deck.index,
            this.player2Deck.getMainDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER2)).collect(Collectors.toList())));
        
        // send extra decks
        this.doAction(new PopulateAction(ActionTypes.POPULATE, this.playField.player1ExtraDeck.index,
            this.player1Deck.getExtraDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER1)).collect(Collectors.toList())));
        this.doAction(new PopulateAction(ActionTypes.POPULATE, this.playField.player2ExtraDeck.index,
            this.player2Deck.getExtraDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER2)).collect(Collectors.toList())));
    }
    
    public List<ZoneInteraction> getActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        return this.getPlayField().getActionsFor(player, interactor, interactorCard, interactee);
    }
    
    public void receiveActionFrom(PlayerEntity player, Action action)
    {
        this.receiveActionFrom(player, this.getRoleFor(player), action);
    }
    
    public void receiveActionFrom(PlayerEntity player, PlayerRole role, Action action)
    {
        if(role != PlayerRole.PLAYER1 && role != PlayerRole.PLAYER2)
        {
            return;
        }
        
        this.doAction(action);
        
        if(action instanceof IAnnouncedAction)
        {
            IAnnouncedAction a1 = (IAnnouncedAction)action;
            ITextComponent playerName = player.getName();
            this.logAndSendMessage(new DuelChatMessage(a1.getAnnouncement(playerName), playerName, role, true));
        }
    }
    
    // this is server side only
    // first init happens here
    protected void doAction(Action action)
    {
        action.init(this.getPlayField());
        if(!this.isRemote)
        {
            this.sendActionToAll(action);
        }
        this.actions.add(action);
        action.doAction();
    }
    
    public IFormattableTextComponent getInfoNameBold()
    {
        return new TranslationTextComponent("container.ydm.duel.info_name")
            .modifyStyle((s) -> s.applyFormatting(TextFormatting.BOLD));
    }
    
    public void sendInfoMessageToAll(ITextComponent text)
    {
        this.sendMessageToAll(new DuelChatMessage(
            text,
            this.getInfoNameBold(),
            PlayerRole.JUDGE, true));
    }
    
    protected void doForAllPlayers(Consumer<PlayerEntity> consumer)
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
    
    protected void doForAllPlayersExcept(Consumer<PlayerEntity> consumer, PlayerEntity exception)
    {
        this.doForAllPlayers((player) ->
        {
            if(player != exception)
            {
                consumer.accept(player);
            }
        });
    }
    
    protected void sendMessageToAll(DuelChatMessage message)
    {
        this.doForAllPlayers((player) -> this.sendMessageTo(player, message));
    }
    
    protected void sendActionToAll(Action action)
    {
        this.doForAllPlayers((player) -> this.sendActionTo(player, null, action));
    }
    
    protected void updateDuelStateToAll()
    {
        this.doForAllPlayers((player) -> this.sendDuelStateTo(player));
    }
    
    protected void updateRoleToAll(@Nullable PlayerRole role, PlayerEntity rolePlayer)
    {
        this.doForAllPlayers((player) ->
        {
            this.updateRoleTo(player, role, rolePlayer);
        });
    }
    
    protected void updateRoleToAllExceptRolePlayer(@Nullable PlayerRole role, PlayerEntity rolePlayer)
    {
        this.doForAllPlayersExcept((player) ->
        {
            this.updateRoleTo(player, role, rolePlayer);
        }, rolePlayer);
    }
    
    protected void updateReadyToAll(PlayerRole role, boolean ready)
    {
        this.doForAllPlayers((player) ->
        {
            this.updateReadyTo(player, role, ready);
        });
    }
    
    protected void updateDeckAcceptedToAll(PlayerRole role)
    {
        this.doForAllPlayers((player) -> this.sendDeckAcceptedTo(player, role));
    }
    
    // synchronize everything
    public void sendAllTo(PlayerEntity player)
    {
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
        
        this.sendMessagesTo(player);
        
        //send duel state last as that will trigger a GUI re-init
        this.sendDuelStateTo(player);
    }
    
    protected void sendMessageTo(PlayerEntity player, DuelChatMessage message)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendMessageToClient(this.getHeader(), message));
    }
    
    protected void sendMessagesTo(PlayerEntity player)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendAllMessagesToClient(this.getHeader(), this.messages));
    }
    
    protected void sendActionTo(PlayerEntity player, PlayerRole source, Action action)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.DuelAction(this.getHeader(), /* source,*/ action));
    }
    
    protected void sendActionsTo(PlayerEntity player)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.AllDuelActions(this.getHeader(), this.actions));
    }
    
    protected void sendDuelStateTo(PlayerEntity player)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.UpdateDuelState(this.getHeader(), this.duelState));
    }
    
    protected void updateRoleTo(PlayerEntity player, PlayerRole role, PlayerEntity rolePlayer)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.UpdateRole(this.getHeader(), role, rolePlayer));
    }
    
    protected void updateReadyTo(PlayerEntity player, PlayerRole role, boolean ready)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.UpdateReady(this.getHeader(), role, ready));
    }
    
    protected void sendDecksTo(PlayerEntity player, List<DeckSource> list)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendAvailableDecks(this.getHeader(), list));
    }
    
    protected void sendDeckTo(PlayerEntity player, int index, DeckHolder deck)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.SendDeck(this.getHeader(), index, deck));
    }
    
    protected void sendDeckAcceptedTo(PlayerEntity player, PlayerRole acceptedOf)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.DeckAccepted(this.getHeader(), acceptedOf));
    }
    
    protected <MSG> void sendGeneralPacketTo(ServerPlayerEntity player, MSG msg)
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
    
    // --- Getters ---
    
    public DuelMessageHeader getHeader()
    {
        return this.headerFactory.get();
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
    
    public List<DuelChatMessage> getMessages()
    {
        return this.messages;
    }
    
    public Random getRandom()
    {
        return this.random;
    }
}
