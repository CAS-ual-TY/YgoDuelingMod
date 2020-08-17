package de.cas_ual_ty.ydm.duel;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.deckbox.IDeckHolder;
import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    
    public PlayField playField;
    
    public List<Action> actions;
    public List<String> messages;
    
    public IDeckHolder player1Deck;
    public IDeckHolder player2Deck;
    
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
        this.updateDuelStateToAll();
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
    }
    
    public void resetPlayer1()
    {
        this.player1 = null;
        this.player1Id = null;
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
    }
    
    public void resetPlayer2()
    {
        this.player2 = null;
        this.player2Id = null;
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
    
    public void onPlayerOpenContainer(PlayerEntity player)
    {
        // if it has started, give player back his role
        if(this.hasStarted())
        {
            PlayerRole role = this.getRoleFor(player);
            
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
                this.setSpectator(player);
            }
        }
        else
        {
            // set spectator by default
            this.setSpectator(player);
        }
        
        if(!this.isRemote)
        {
            this.sendAllTo(player);
        }
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
        if(role == PlayerRole.PLAYER1)
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
    
    // client side, received
    public void setRoleForPlayer(PlayerEntity player, PlayerRole role)
    {
        if(player != null)
        {
            this.playerSelectRole(player, role);
        }
        else
        {
            if(role == PlayerRole.PLAYER1)
            {
                this.removePlayer1();
            }
            else if(role == PlayerRole.PLAYER2)
            {
                this.removePlayer2();
            }
        }
    }
    
    // player selects a role, if successful send update to everyone
    public boolean playerSelectRole(PlayerEntity player, PlayerRole role)
    {
        YDM.debug("player select role 1 " + role + " " + this.spectators + " " + player);
        
        if(this.canPlayerSelectRole(player, role))
        {
            // remove previous role
            
            PlayerRole previous = this.getRoleFor(player);
            
            YDM.debug("player select role 2 " + previous);
            
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
                    YDM.debug("player select role REMOVING");
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
            
            YDM.debug("player select role 3 " + this.spectators);
            
            if(!this.isRemote)
            {
                this.updateRoleToAll(role, player);
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void handlePlayerLeave(PlayerEntity player, PlayerRole role)
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
    
    public void updateRoleToAll(PlayerRole role, PlayerEntity rolePlayer)
    {
        this.doForAllPlayers((player) ->
        {
            if(role != null)
            {
                this.updateRoleTo(player, role, rolePlayer);
            }
        });
    }
    
    // synchronize everything
    public void sendAllTo(PlayerEntity player)
    {
        this.sendDuelStateTo(player);
        
        this.updateRoleTo(player, PlayerRole.PLAYER1, this.player1);
        this.updateRoleTo(player, PlayerRole.PLAYER2, this.player2);
        
        for(PlayerEntity spectator : this.spectators)
        {
            this.updateRoleTo(player, PlayerRole.SPECTATOR, spectator);
        }
        
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
    
    protected void updateRoleTo(PlayerEntity player, PlayerRole role, @Nullable PlayerEntity rolePlayer)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.UpdateRole(role, rolePlayer));
    }
    
    protected void sendMessageTo(PlayerEntity player, String message)
    {
        // TODO
    }
    
    protected void sendAvailableRolesTo(PlayerEntity player)
    {
        this.sendGeneralPacketTo((ServerPlayerEntity)player, new DuelMessages.AvailableRoles(this.getAvailablePlayerRoles(player)));
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
