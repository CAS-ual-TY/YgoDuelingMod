package de.cas_ual_ty.ydm.duel;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerEntity;

public class DuelManager
{
    public final IDuelSynchronizer synchronizer;
    public final IDuelTicker ticker;
    
    public DuelState duelState;
    
    public int player1Id;
    public int player2Id;
    
    public PlayerEntity player1;
    public PlayerEntity player2;
    public List<PlayerEntity> spectators;
    
    public PlayField playField;
    
    public List<Action> actions;
    public List<String> messages;
    
    public DuelManager(IDuelSynchronizer synchronizer, @Nullable IDuelTicker ticker)
    {
        this.synchronizer = synchronizer;
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
        this.player1Id = -1;
        this.player2Id = -1;
        this.player1 = null;
        this.player2 = null;
        this.spectators.clear();
        this.actions.clear();
        this.messages.clear();
        this.playField = new PlayField(this);
    }
    
    public void setDuelStateAndUpdate(DuelState duelState)
    {
        this.duelState = duelState;
        this.sendDuelStateToAll();
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
    
    public PlayerRole getRoleFor(PlayerEntity player)
    {
        if(this.player1Id == player.getEntityId())
        {
            return PlayerRole.PLAYER1;
        }
        else if(this.player2Id == player.getEntityId())
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
    
    public List<PlayerRole> getAvailableRoles(PlayerEntity player)
    {
        List<PlayerRole> list = new LinkedList<>();
        
        if(this.player1Id == player.getEntityId())
        {
            list.add(PlayerRole.PLAYER1);
        }
        else if(this.player2Id == player.getEntityId())
        {
            list.add(PlayerRole.PLAYER2);
        }
        else
        {
            if(this.player1Id == -1)
            {
                list.add(PlayerRole.PLAYER1);
            }
            
            if(this.player2Id == -1)
            {
                list.add(PlayerRole.PLAYER2);
            }
            
            list.add(PlayerRole.SPECTATOR);
            //TODO judge
        }
        
        return list;
    }
    
    public boolean canJoinAs(PlayerEntity player, PlayerRole role)
    {
        if(role == PlayerRole.PLAYER1)
        {
            return this.player1Id == -1;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            return this.player2Id == -1;
        }
        else
        {
            return role == PlayerRole.SPECTATOR; //TODO judge
        }
    }
    
    public boolean playerJoin(PlayerEntity player, PlayerRole role)
    {
        if(this.canJoinAs(player, role))
        {
            if(role == PlayerRole.PLAYER1)
            {
                this.player1Id = player.getEntityId();
                this.player1 = player;
            }
            else if(role == PlayerRole.PLAYER2)
            {
                this.player2Id = player.getEntityId();
                this.player2 = player;
            }
            else if(role == PlayerRole.SPECTATOR)
            {
                this.spectators.add(player);
            }
            
            this.sendAllTo(player, role);
            
            //TODO judge
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void playerLeave(PlayerEntity player)
    {
        PlayerRole role = this.getRoleFor(player);
        
        if(role == PlayerRole.PLAYER1)
        {
            this.handleDuelistLeave(player, role);
            this.player1 = null;
        }
        else if(role == PlayerRole.PLAYER2)
        {
            this.handleDuelistLeave(player, role);
            this.player2 = null;
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            this.spectators.remove(player);
        }
        
        //TODO judge
    }
    
    public void handleDuelistLeave(PlayerEntity player, PlayerRole role)
    {
        if(this.duelState == DuelState.DUELING || this.duelState == DuelState.SIDING)
        {
            // notify players in case actual duelist left
            // spectators can always be ignored
        }
        else // idle or end state
        {
            if(role == PlayerRole.PLAYER1)
            {
                this.player1Id = -1;
            }
            else if(role == PlayerRole.PLAYER2)
            {
                this.player2Id = -1;
            }
            
            if(this.duelState == DuelState.END)
            {
                if(this.player1Id == -1 && this.player2Id == -1)
                {
                    this.endDuelAndReset();
                }
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
        this.sendActionToAll(action);
        this.logAction(action);
        action.doAction();
    }
    
    public void logAction(Action action)
    {
        this.actions.add(action);
    }
    
    // is this really needed?
    public void sendActionTo(PlayerRole role, Action action)
    {
        if(role == PlayerRole.PLAYER1 && this.player1.isAlive())
        {
            this.sendActionTo(this.player1, action);
        }
        else if(role == PlayerRole.PLAYER2 && this.player2.isAlive())
        {
            this.sendActionTo(this.player2, action);
        }
        else if(role == PlayerRole.SPECTATOR)
        {
            //this.spectators.stream().filter(player -> player.isAlive()).forEach(player -> this.sendAction(player, action));
            
            for(PlayerEntity player : this.spectators)
            {
                if(player.isAlive())
                {
                    this.sendActionTo(player, action);
                }
            }
        }
    }
    
    public void doForAllPlayers(Consumer<PlayerEntity> consumer)
    {
        if(this.player1 != null && this.player1.isAlive())
        {
            consumer.accept(this.player1);
        }
        
        if(this.player2 != null && this.player2.isAlive())
        {
            consumer.accept(this.player2);
        }
        
        for(PlayerEntity player : this.spectators)
        {
            if(player.isAlive())
            {
                consumer.accept(player);
            }
        }
    }
    
    public void sendActionToAll(Action action)
    {
        this.doForAllPlayers((player) -> this.sendActionTo(player, action));
    }
    
    public void sendActionsToAll()
    {
        this.doForAllPlayers((player) -> this.sendActionsTo(player));
    }
    
    public void sendDuelStateToAll()
    {
        this.doForAllPlayers((player) -> this.sendDuelStateTo(player));
    }
    
    public void sendRoleToAll()
    {
        this.doForAllPlayers((player) ->
        {
            PlayerRole role = this.getRoleFor(player);
            
            if(role != null)
            {
                this.sendRoleTo(player, role);
            }
        });
    }
    
    public void sendAllTo(PlayerEntity player)
    {
        PlayerRole role = this.getRoleFor(player);
        
        if(role != null)
        {
            this.sendAllTo(player, role);
        }
    }
    
    public void sendAllTo(PlayerEntity player, PlayerRole role)
    {
        this.sendDuelStateTo(player);
        this.sendActionsTo(player);
        this.sendRoleTo(player, role);
    }
    
    // --- synchronizer passing ---
    
    protected void sendActionTo(PlayerEntity player, Action action)
    {
        this.synchronizer.sendActionTo(player, action);
    }
    
    protected void sendActionsTo(PlayerEntity player)
    {
        this.synchronizer.sendActionsTo(player, this.actions);
    }
    
    protected void sendDuelStateTo(PlayerEntity player)
    {
        this.synchronizer.sendDuelStateTo(player, this.duelState);
    }
    
    protected void sendRoleTo(PlayerEntity player, PlayerRole role)
    {
        this.synchronizer.sendRoleTo(player, role);
    }
    
    protected void sendMessageTo(PlayerEntity player, String message)
    {
        this.synchronizer.sendChatTo(player, message);
    }
    
    // --- Getters ---
    
    public IDuelSynchronizer getSynchronizer()
    {
        return this.synchronizer;
    }
    
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
