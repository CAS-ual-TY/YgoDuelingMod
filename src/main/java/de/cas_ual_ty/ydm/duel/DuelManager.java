package de.cas_ual_ty.ydm.duel;

import com.google.common.collect.ImmutableList;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.action.*;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.*;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    
    public boolean player1OfferedDraw;
    public boolean player2OfferedDraw;
    
    public boolean player1AdmittingDefeat;
    public boolean player2AdmittingDefeat;
    
    public DuelManager(boolean isRemote, Supplier<DuelMessageHeader> header)
    {
        this.isRemote = isRemote;
        headerFactory = header;
        spectators = new LinkedList<>();
        actions = new LinkedList<>();
        messages = new LinkedList<>();
        random = new Random(YDM.random.nextLong());
        reset();
    }
    
    public void playerOpenContainer(PlayerEntity player)
    {
        PlayerRole role;
        
        // if it has started, give player back his role
        if(hasStarted())
        {
            role = getRoleFor(player);
            
            if(role == PlayerRole.PLAYER1)
            {
                setPlayer1(player);
            }
            else if(role == PlayerRole.PLAYER2)
            {
                setPlayer2(player);
            }
            else
            {
                role = PlayerRole.SPECTATOR;
                setSpectator(player);
            }
        }
        else
        {
            role = PlayerRole.SPECTATOR;
            
            // set spectator by default
            setSpectator(player);
        }
        
        // tell all the other players that this player has joined
        // apparently this is not called on client
        if(!isRemote)
        {
            updateRoleToAllExceptRolePlayer(role, player);
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
        
        PlayerRole role = getRoleFor(player);
        
        if(role == PlayerRole.PLAYER1)
        {
            removePlayer1();
            handlePlayerLeave(player, role);
        }
        else if(role == PlayerRole.PLAYER2)
        {
            removePlayer2();
            handlePlayerLeave(player, role);
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            removeSpectator(player);
        }
        
        if(!isRemote)
        {
            updateRoleToAll(null, player);
        }
        
        if(player1 == null && player2 == null && spectators.isEmpty())
        {
            onEveryoneLeft();
        }
    }
    
    // client equivalent
    // need a seperate method because if the player leaves the server
    // you can not fetch it using world.get player by uuid (or some method name like that)
    public void playerCloseContainerClient(UUID playerUUID)
    {
        PlayerRole role = getRoleFor(playerUUID);
        
        if(role == PlayerRole.PLAYER1)
        {
            removePlayer1();
        }
        else if(role == PlayerRole.PLAYER2)
        {
            removePlayer2();
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            removeSpectator(playerUUID);
        }
        
        if(player1 == null && player2 == null && spectators.isEmpty())
        {
            onEveryoneLeft();
        }
    }
    
    protected void onEveryoneLeft()
    {
        reset();
    }
    
    // either closed container or deselected role
    protected void onPlayerRemoved()
    {
        if(!hasStarted())
        {
            if(duelState == DuelState.PREPARING)
            {
                setDuelStateAndUpdate(DuelState.IDLE);
            }
            
            checkReadyState();
        }
    }
    
    protected void handlePlayerLeave(PlayerEntity player, PlayerRole role)
    {
        if(hasStarted())
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
        duelState = DuelState.IDLE;
        resetPlayer1();
        resetPlayer2();
        resetSpectators();
        actions.clear();
        messages.clear();
        playField = new PlayField(this, getPlayFieldType());
        player1Deck = null;
        player2Deck = null;
        player1OfferedDraw = false;
        player2OfferedDraw = false;
        player1AdmittingDefeat = false;
        player2AdmittingDefeat = false;
    }
    
    protected void resetPlayer1()
    {
        player1 = null;
        player1Id = null;
        player1Ready = false;
        player1Decks = null;
    }
    
    protected void resetPlayer2()
    {
        player2 = null;
        player2Id = null;
        player2Ready = false;
        player2Decks = null;
    }
    
    protected void resetSpectators()
    {
        spectators.clear();
    }
    
    protected void setPlayer1(PlayerEntity player)
    {
        player1 = player;
        player1Id = player.getUUID();
    }
    
    protected void setPlayer2(PlayerEntity player)
    {
        player2 = player;
        player2Id = player.getUUID();
    }
    
    protected void setSpectator(PlayerEntity player)
    {
        spectators.add(player);
    }
    
    protected void removePlayer1()
    {
        if(hasStarted())
        {
            // set it to null, but keep the id, in case of a disconnect
            player1 = null;
            player1Decks = null;
        }
        else
        {
            resetPlayer1();
        }
        
        onPlayerRemoved();
    }
    
    protected void removePlayer2()
    {
        if(hasStarted())
        {
            player2 = null;
            player2Decks = null;
        }
        else
        {
            resetPlayer2();
        }
        
        onPlayerRemoved();
    }
    
    protected void removeSpectator(PlayerEntity player)
    {
        spectators.remove(player);
    }
    
    protected void removeSpectator(UUID playerUUID)
    {
        for(int i = 0; i < spectators.size(); ++i)
        {
            if(spectators.get(i).getUUID().equals(playerUUID))
            {
                spectators.remove(i);
                break;
            }
        }
    }
    
    public void kickPlayer(PlayerEntity player)
    {
        player.closeContainer();
    }
    
    public void kickAllPlayers()
    {
        if(player1 != null)
        {
            kickPlayer(player1);
        }
        
        if(player2 != null)
        {
            kickPlayer(player2);
        }
        
        spectators.forEach(this::kickPlayer);
    }
    
    @Nullable
    public PlayerEntity getPlayer(ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return player1;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return player2;
        }
        else
        {
            return null;
        }
    }
    
    @Nullable
    public PlayerRole getRoleFor(PlayerEntity player)
    {
        return getRoleFor(player.getUUID());
    }
    
    @Nullable
    public PlayerRole getRoleFor(UUID playerUUID)
    {
        if(playerUUID.equals(player1Id))
        {
            return PlayerRole.PLAYER1;
        }
        if(playerUUID.equals(player2Id))
        {
            return PlayerRole.PLAYER2;
        }
        else if(spectators.stream().anyMatch((player) -> player.getUUID().equals(playerUUID)))
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
        
        if(getAvailableDecksFor(player).isEmpty())
        {
            return list;
        }
        
        if(player1Id == null)
        {
            list.add(PlayerRole.PLAYER1);
        }
        
        if(player2Id == null)
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
            return player1Id == null || player1Id.equals(player.getUUID());
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return player2Id == null || player2Id.equals(player.getUUID());
        }
        else
        {
            return role == PlayerRole.SPECTATOR; //TODO judge
        }
    }
    
    // player selects a role, if successful send update to everyone
    public void playerSelectRole(PlayerEntity player, PlayerRole role)
    {
        if(canPlayerSelectRole(player, role))
        {
            // remove previous role
            
            PlayerRole previous = getRoleFor(player);
            
            if(previous != null)
            {
                if(previous == PlayerRole.PLAYER1)
                {
                    removePlayer1();
                }
                else if(previous == PlayerRole.PLAYER2)
                {
                    removePlayer2();
                }
                else if(previous == PlayerRole.SPECTATOR)
                {
                    removeSpectator(player);
                }
            }
            
            // ---
            
            if(role == PlayerRole.PLAYER1)
            {
                setPlayer1(player);
            }
            else if(role == PlayerRole.PLAYER2)
            {
                setPlayer2(player);
            }
            else// if(role == PlayerRole.SPECTATOR) // player must have a default role //TODO judge
            {
                setSpectator(player);
            }
            
            if(!isRemote)
            {
                updateRoleToAll(role, player);
            }
        }
    }
    
    public void setDuelStateAndUpdate(DuelState duelState)
    {
        if(duelState == DuelState.END)
        {
            doForAllPlayers(this::kickPlayer);
            return;
        }
        
        this.duelState = duelState;
        
        if(!isRemote)
        {
            updateDuelStateToAll();
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
        IFormattableTextComponent name = (IFormattableTextComponent) player.getName();
        
        PlayerRole role;
        
        //        if(this.getDuelState() == DuelState.IDLE)
        //        {
        //            role = PlayerRole.SPECTATOR;
        //        }
        //        else
        //        {
        role = getRoleFor(player);
        //        }
        
        DuelChatMessage chatMessage = new DuelChatMessage(message, name, role);
        logAndSendMessage(chatMessage);
    }
    
    protected void logAndSendMessage(DuelChatMessage chatMessage)
    {
        messages.add(chatMessage);
        sendMessageToAll(chatMessage);
    }
    
    public void requestReady(PlayerEntity player, boolean ready)
    {
        if(hasStarted())
        {
            return;
        }
        
        PlayerRole role = getRoleFor(player);
        
        if(role == PlayerRole.PLAYER1 || role == PlayerRole.PLAYER2)
        {
            updateReady(role, ready);
        }
        
        if(player1Ready && player2Ready)
        {
            onBothReady();
        }
    }
    
    public void updateReady(PlayerRole role, boolean ready)
    {
        if(role == PlayerRole.PLAYER1)
        {
            player1Ready = ready;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            player2Ready = ready;
        }
        
        checkReadyState();
        
        if(!isRemote)
        {
            updateReadyToAll(role, ready);
        }
    }
    
    // if only 1 player there, unready all
    protected void checkReadyState()
    {
        if(player1 == null || player2 == null)
        {
            player1Ready = false;
            player2Ready = false;
        }
    }
    
    protected void onBothReady()
    {
        // both players are ready
        setDuelStateAndUpdate(DuelState.PREPARING);
        findDecksForPlayers();
        sendDecksToPlayers();
    }
    
    public boolean hasStarted()
    {
        return duelState == DuelState.DUELING || duelState == DuelState.SIDING;
    }
    
    protected void findDecksForPlayers()
    {
        player1Decks = getAvailableDecksFor(player1);
        player2Decks = getAvailableDecksFor(player2);
    }
    
    public List<DeckSource> getAvailableDecksFor(PlayerEntity player)
    {
        if(hasStarted())
        {
            return ImmutableList.of();
        }
        
        FindDecksEvent e = new FindDecksEvent(player, this);
        MinecraftForge.EVENT_BUS.post(e);
        e.decksList.add(DeckSource.EMPTY_DECK.get()); // empty deck
        return e.decksList;
    }
    
    public void sendDecksToPlayers()
    {
        if(player1Decks.isEmpty())
        {
            kickPlayer(player1);
        }
        
        if(player2Decks.isEmpty())
        {
            kickPlayer(player2);
        }
        
        if(player1Decks.isEmpty() || player2Decks.isEmpty())
        {
            return;
        }
        
        sendDecksTo(player1, player1Decks);
        sendDecksTo(player2, player2Decks);
    }
    
    public void requestDeck(int index, PlayerEntity player)
    {
        if(hasStarted())
        {
            return;
        }
        
        PlayerRole role = getRoleFor(player);
        List<DeckSource> list = null;
        
        if(role == PlayerRole.PLAYER1)
        {
            list = player1Decks;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            list = player2Decks;
        }
        
        if(list == null || index < 0 || index >= list.size())
        {
            return;
        }
        
        DeckHolder deck = list.get(index).deck;
        
        sendDeckTo(player, index, deck);
    }
    
    public void chooseDeck(int index, PlayerEntity player)
    {
        PlayerRole role = getRoleFor(player);
        List<DeckSource> list = null;
        
        if(role == PlayerRole.PLAYER1)
        {
            list = player1Decks;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            list = player2Decks;
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
                player1Deck = deck;
                updateDeckAcceptedToAll(PlayerRole.PLAYER1);
            }
            else if(role == PlayerRole.PLAYER2)
            {
                player2Deck = deck;
                updateDeckAcceptedToAll(PlayerRole.PLAYER2);
            }
            
            if(player1Deck != null && player2Deck != null)
            {
                startDuel();
            }
        }
    }
    
    protected void startDuel()
    {
        sendInfoMessageToAll(new TranslationTextComponent("container.ydm.duel.info_start"));
        populatePlayField();
        setDuelStateAndUpdate(DuelState.DUELING);
    }
    
    protected void populatePlayField()
    {
        doAction(new InitSleevesAction(ActionTypes.INIT_SLEEVES, player1Deck.getSleeves(), player2Deck.getSleeves()));
        
        // send main decks
        doAction(new PopulateAction(ActionTypes.POPULATE, playField.player1Deck.index,
                player1Deck.getMainDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER1)).collect(Collectors.toList())));
        doAction(new PopulateAction(ActionTypes.POPULATE, playField.player2Deck.index,
                player2Deck.getMainDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER2)).collect(Collectors.toList())));
        
        // send extra decks
        doAction(new PopulateAction(ActionTypes.POPULATE, playField.player1ExtraDeck.index,
                player1Deck.getExtraDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER1)).collect(Collectors.toList())));
        doAction(new PopulateAction(ActionTypes.POPULATE, playField.player2ExtraDeck.index,
                player2Deck.getExtraDeckNonNull().map((card) -> new DuelCard(card, false, CardPosition.FD, ZoneOwner.PLAYER2)).collect(Collectors.toList())));
    }
    
    public List<ZoneInteraction> getActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        return getPlayField().getActionsFor(player, interactor, interactorCard, interactee);
    }
    
    public List<ZoneInteraction> getAdvancedActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        return getPlayField().getAdvancedActionsFor(player, interactor, interactorCard, interactee);
    }
    
    public void receiveActionFrom(PlayerEntity player, Action action)
    {
        receiveActionFrom(player, getRoleFor(player), action);
    }
    
    public void receiveActionFrom(PlayerEntity player, PlayerRole role, Action action)
    {
        if(role != PlayerRole.PLAYER1 && role != PlayerRole.PLAYER2)
        {
            return;
        }
        
        try
        {
            doAction(action);
            
            if(!player.level.isClientSide)
            {
                setPlayerOffersDraw(player, role, false);
                setPlayerAdmitsDefeat(player, role, false);
            }
            
            // if action throws, it is no announced
            
            if(action instanceof IAnnouncedAction)
            {
                IAnnouncedAction a1 = (IAnnouncedAction) action;
                ITextComponent playerName = player.getName();
                logAndSendMessage(new DuelChatMessage(a1.getAnnouncement(playerName), playerName, role, true));
            }
        }
        catch(Exception e)
        {
            // action failed
        }
    }
    
    public void setPlayerOffersDraw(PlayerEntity player, PlayerRole role, boolean offersDraw)
    {
        boolean previous;
        
        if(role == PlayerRole.PLAYER1 && player1OfferedDraw != offersDraw)
        {
            previous = player1OfferedDraw;
            player1OfferedDraw = offersDraw;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            previous = player2OfferedDraw;
            player2OfferedDraw = offersDraw;
        }
        else
        {
            return;
        }
        
        if(previous == offersDraw)
        {
            return; // dont send message
        }
        
        if(offersDraw)
        {
            logAndSendMessage(new DuelChatMessage(new StringTextComponent("Offering Draw"), player.getName(), role, true));
        }
        else
        {
            logAndSendMessage(new DuelChatMessage(new StringTextComponent("Cancel Draw Offer"), player.getName(), role, true));
        }
    }
    
    public boolean setPlayerAdmitsDefeat(PlayerEntity player, PlayerRole role, boolean admitsDefeat)
    {
        boolean previous = false;
        
        if(role == PlayerRole.PLAYER1)
        {
            if(admitsDefeat)
            {
                if(player1AdmittingDefeat)
                {
                    logAndSendMessage(new DuelChatMessage(new StringTextComponent("Admit Defeat"), player.getName(), role, true));
                    return true;
                }
            }
            
            previous = player1AdmittingDefeat;
            player1AdmittingDefeat = admitsDefeat;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            if(admitsDefeat)
            {
                if(player2AdmittingDefeat)
                {
                    logAndSendMessage(new DuelChatMessage(new StringTextComponent("Admit Defeat"), player.getName(), role, true));
                    return true;
                }
            }
            
            previous = player2AdmittingDefeat;
            player2AdmittingDefeat = admitsDefeat;
        }
        else
        {
            return false;
        }
        
        if(previous == admitsDefeat)
        {
            return false;
        }
        
        if(admitsDefeat)
        {
            sendMessageTo(player, new DuelChatMessage(new StringTextComponent("Click 'Admit Defeat' again to confirm"), player.getName(), role, true));
        }
        else
        {
            sendMessageTo(player, new DuelChatMessage(new StringTextComponent("Cancel 'Admit Defeat'"), player.getName(), role, true));
        }
        
        return false;
    }
    
    public void playerOffersDraw(PlayerEntity player)
    {
        PlayerRole role = getRoleFor(player);
        setPlayerOffersDraw(player, role, role == PlayerRole.PLAYER1 ? !player1OfferedDraw : (role == PlayerRole.PLAYER2 ? !player2OfferedDraw : false));
        
        if(player1OfferedDraw && player2OfferedDraw)
        {
            YdmUtil.executeDrawCommands(player1, player2);
            setDuelStateAndUpdate(DuelState.END);
        }
    }
    
    public void playerAdmitsDefeat(PlayerEntity player)
    {
        PlayerRole role = getRoleFor(player);
        
        boolean end = false;
        
        if(role == PlayerRole.PLAYER1 && setPlayerAdmitsDefeat(player, role, true))
        {
            YdmUtil.executeAdmitDefeatCommands(player2, player1);
            end = true;
        }
        else if(role == PlayerRole.PLAYER2 && setPlayerAdmitsDefeat(player, role, true))
        {
            YdmUtil.executeAdmitDefeatCommands(player1, player2);
            end = true;
        }
        
        if(end)
        {
            setDuelStateAndUpdate(DuelState.END);
        }
    }
    
    // this is server side only
    // first init happens here
    protected void doAction(Action action)
    {
        action.initServer(getPlayField());
        action.doAction();
        
        // if init or doAction failed, the next lines are not executed
        
        if(!isRemote)
        {
            sendActionToAll(action);
        }
        
        logAction(action);
    }
    
    public void logAction(Action action)
    {
        actions.add(action);
    }
    
    public IFormattableTextComponent getInfoNameBold()
    {
        return new TranslationTextComponent("container.ydm.duel.info_name")
                .withStyle((s) -> s.applyFormat(TextFormatting.BOLD));
    }
    
    public void sendInfoMessageToAll(ITextComponent text)
    {
        DuelChatMessage msg = new DuelChatMessage(
                text,
                getInfoNameBold(),
                PlayerRole.JUDGE, true);
        messages.add(msg);
        sendMessageToAll(msg);
    }
    
    protected void doForAllPlayers(Consumer<PlayerEntity> consumer)
    {
        if(player1 != null)
        {
            consumer.accept(player1);
        }
        
        if(player2 != null)
        {
            consumer.accept(player2);
        }
        
        for(PlayerEntity player : spectators)
        {
            consumer.accept(player);
        }
    }
    
    protected void doForAllPlayersExcept(Consumer<PlayerEntity> consumer, PlayerEntity exception)
    {
        doForAllPlayers((player) ->
        {
            if(player != exception)
            {
                consumer.accept(player);
            }
        });
    }
    
    protected void sendMessageToAll(DuelChatMessage message)
    {
        doForAllPlayers((player) -> sendMessageTo(player, message));
    }
    
    protected void sendActionToAll(Action action)
    {
        doForAllPlayers((player) -> sendActionTo(player, null, action));
    }
    
    protected void updateDuelStateToAll()
    {
        doForAllPlayers((player) -> sendDuelStateTo(player));
    }
    
    protected void updateRoleToAll(@Nullable PlayerRole role, PlayerEntity rolePlayer)
    {
        doForAllPlayers((player) ->
        {
            updateRoleTo(player, role, rolePlayer);
        });
    }
    
    protected void updateRoleToAllExceptRolePlayer(@Nullable PlayerRole role, PlayerEntity rolePlayer)
    {
        doForAllPlayersExcept((player) ->
        {
            updateRoleTo(player, role, rolePlayer);
        }, rolePlayer);
    }
    
    protected void updateReadyToAll(PlayerRole role, boolean ready)
    {
        doForAllPlayers((player) ->
        {
            updateReadyTo(player, role, ready);
        });
    }
    
    protected void updateDeckAcceptedToAll(PlayerRole role)
    {
        doForAllPlayers((player) -> sendDeckAcceptedTo(player, role));
    }
    
    // synchronize everything
    public void sendAllTo(PlayerEntity player)
    {
        if(player1 != null)
        {
            updateRoleTo(player, PlayerRole.PLAYER1, player1);
        }
        
        if(player2 != null)
        {
            updateRoleTo(player, PlayerRole.PLAYER2, player2);
        }
        
        for(PlayerEntity spectator : spectators)
        {
            updateRoleTo(player, PlayerRole.SPECTATOR, spectator);
        }
        
        updateReadyTo(player, PlayerRole.PLAYER1, player1Ready);
        updateReadyTo(player, PlayerRole.PLAYER2, player2Ready);
        
        // playfield is updated via actions
        if(duelState == DuelState.DUELING)
        {
            sendActionsTo(player);
        }
        
        sendMessagesTo(player);
        
        //send duel state last as that will trigger a GUI re-init
        sendDuelStateTo(player);
    }
    
    protected void sendMessageTo(PlayerEntity player, DuelChatMessage message)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.SendMessageToClient(getHeader(), message));
    }
    
    protected void sendMessagesTo(PlayerEntity player)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.SendAllMessagesToClient(getHeader(), messages));
    }
    
    protected void sendActionTo(PlayerEntity player, PlayerRole source, Action action)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.DuelAction(getHeader(), /* source,*/ action));
    }
    
    protected void sendActionsTo(PlayerEntity player)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.AllDuelActions(getHeader(), actions));
    }
    
    protected void sendDuelStateTo(PlayerEntity player)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.UpdateDuelState(getHeader(), duelState));
    }
    
    protected void updateRoleTo(PlayerEntity player, PlayerRole role, PlayerEntity rolePlayer)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.UpdateRole(getHeader(), role, rolePlayer));
    }
    
    protected void updateReadyTo(PlayerEntity player, PlayerRole role, boolean ready)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.UpdateReady(getHeader(), role, ready));
    }
    
    protected void sendDecksTo(PlayerEntity player, List<DeckSource> list)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.SendAvailableDecks(getHeader(), list));
    }
    
    protected void sendDeckTo(PlayerEntity player, int index, DeckHolder deck)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.SendDeck(getHeader(), index, deck));
    }
    
    protected void sendDeckAcceptedTo(PlayerEntity player, PlayerRole acceptedOf)
    {
        sendGeneralPacketTo((ServerPlayerEntity) player, new DuelMessages.DeckAccepted(getHeader(), acceptedOf));
    }
    
    protected <MSG> void sendGeneralPacketTo(ServerPlayerEntity player, MSG msg)
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
    
    // --- Getters ---
    
    public DuelMessageHeader getHeader()
    {
        return headerFactory.get();
    }
    
    public DuelState getDuelState()
    {
        return duelState;
    }
    
    public PlayField getPlayField()
    {
        return playField;
    }
    
    public List<Action> getActions()
    {
        return actions;
    }
    
    public List<DuelChatMessage> getMessages()
    {
        return messages;
    }
    
    public Random getRandom()
    {
        return random;
    }
}
