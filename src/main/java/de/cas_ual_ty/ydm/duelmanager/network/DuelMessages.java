package de.cas_ual_ty.ydm.duelmanager.network;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.IDuelManagerProvider;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class DuelMessages
{
    public static class SelectRole extends DuelMessage.ServerBaseMessage
    {
        public PlayerRole playerRole;
        
        public SelectRole(DuelMessageHeader header, PlayerRole playerRole)
        {
            super(header);
            this.playerRole = playerRole;
        }
        
        public SelectRole(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            DuelMessageUtility.encodePlayerRole(this.playerRole, buf);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.playerRole = DuelMessageUtility.decodePlayerRole(buf);
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.getDuelManager().playerSelectRole(player, this.playerRole);
        }
    }
    
    public static class UpdateRole extends DuelMessage.ClientBaseMessage
    {
        @Nullable
        public PlayerRole role;
        
        public UUID rolePlayerId;
        
        public UpdateRole(DuelMessageHeader header, @Nullable PlayerRole role, UUID rolePlayerId)
        {
            super(header);
            this.role = role;
            this.rolePlayerId = rolePlayerId;
        }
        
        public UpdateRole(DuelMessageHeader header, @Nullable PlayerRole role, PlayerEntity rolePlayer)
        {
            this(header, role, rolePlayer.getUniqueID());
        }
        
        public UpdateRole(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            if(this.role != null)
            {
                buf.writeBoolean(true);
                DuelMessageUtility.encodePlayerRole(this.role, buf);
            }
            else
            {
                buf.writeBoolean(false);
            }
            
            buf.writeUniqueId(this.rolePlayerId);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            if(buf.readBoolean())
            {
                this.role = DuelMessageUtility.decodePlayerRole(buf);
            }
            else
            {
                this.role = null;
            }
            
            this.rolePlayerId = buf.readUniqueId();
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            PlayerEntity rolePlayer = player.world.getPlayerByUuid(this.rolePlayerId);
            
            if(this.role != null)
            {
                provider.getDuelManager().playerSelectRole(rolePlayer, this.role);
            }
            else
            {
                provider.getDuelManager().playerCloseContainer(rolePlayer);
            }
        }
    }
    
    public static class UpdateDuelState extends DuelMessage.ClientBaseMessage
    {
        public DuelState duelState;
        
        public UpdateDuelState(DuelMessageHeader header, DuelState duelState)
        {
            super(header);
            this.duelState = duelState;
        }
        
        public UpdateDuelState(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            DuelMessageUtility.encodeDuelState(this.duelState, buf);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.duelState = DuelMessageUtility.decodeDuelState(buf);
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.updateDuelState(this.duelState);
        }
    }
    
    public static class RequestFullUpdate extends DuelMessage.ServerBaseMessage
    {
        public RequestFullUpdate(DuelMessageHeader header)
        {
            super(header);
        }
        
        public RequestFullUpdate(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.getDuelManager().sendAllTo(player);
        }
    }
    
    public static class RequestReady extends DuelMessage.ServerBaseMessage
    {
        public boolean ready;
        
        public RequestReady(DuelMessageHeader header, boolean ready)
        {
            super(header);
            this.ready = ready;
        }
        
        public RequestReady(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            buf.writeBoolean(this.ready);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.ready = buf.readBoolean();
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.getDuelManager().requestReady(player, this.ready);
        }
    }
    
    public static class UpdateReady extends DuelMessage.ClientBaseMessage
    {
        public PlayerRole role;
        public boolean ready;
        
        public UpdateReady(DuelMessageHeader header, PlayerRole role, boolean ready)
        {
            super(header);
            this.role = role;
            this.ready = ready;
        }
        
        public UpdateReady(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            DuelMessageUtility.encodePlayerRole(this.role, buf);
            buf.writeBoolean(this.ready);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.role = DuelMessageUtility.decodePlayerRole(buf);
            this.ready = buf.readBoolean();
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.getDuelManager().updateReady(this.role, this.ready);
        }
    }
    
    public static class SendAvailableDecks extends DuelMessage.ClientBaseMessage
    {
        public List<DeckSource> deckSources;
        
        public SendAvailableDecks(DuelMessageHeader header, List<DeckSource> deckSources)
        {
            super(header);
            this.deckSources = deckSources;
        }
        
        public SendAvailableDecks(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            DuelMessageUtility.encodeList(this.deckSources, buf, (deckSource, buf1) -> buf1.writeItemStack(deckSource.source));
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.deckSources = DuelMessageUtility.decodeList(buf, (buf1) -> new DeckSource(null, buf1.readItemStack()));
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.receiveDeckSources(this.deckSources);
        }
    }
    
    public static class RequestDeck extends DuelMessage.ServerBaseMessage
    {
        public int index;
        
        public RequestDeck(DuelMessageHeader header, int index)
        {
            super(header);
            this.index = index;
        }
        
        public RequestDeck(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            buf.writeInt(this.index);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.index = buf.readInt();
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.getDuelManager().requestDeck(this.index, player);
        }
    }
    
    public static class SendDeck extends DuelMessage.ClientBaseMessage
    {
        public int index;
        public DeckHolder deck;
        
        public SendDeck(DuelMessageHeader header, int index, DeckHolder deck)
        {
            super(header);
            this.index = index;
            this.deck = deck;
        }
        
        public SendDeck(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            buf.writeInt(this.index);
            DuelMessageUtility.encodeDeckHolder(this.deck, buf);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.index = buf.readInt();
            this.deck = DuelMessageUtility.decodeDeckHolder(buf);
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.receiveDeck(this.index, this.deck);
        }
    }
    
    public static class ChooseDeck extends DuelMessage.ServerBaseMessage
    {
        public int index;
        
        public ChooseDeck(DuelMessageHeader header, int index)
        {
            super(header);
            this.index = index;
        }
        
        public ChooseDeck(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            buf.writeInt(this.index);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.index = buf.readInt();
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.getDuelManager().chooseDeck(this.index, player);
        }
    }
    
    public static class DeckAccepted extends DuelMessage.ClientBaseMessage
    {
        public PlayerRole role;
        
        public DeckAccepted(DuelMessageHeader header, PlayerRole role)
        {
            super(header);
            this.role = role;
        }
        
        public DeckAccepted(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            DuelMessageUtility.encodePlayerRole(this.role, buf);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.role = DuelMessageUtility.decodePlayerRole(buf);
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.deckAccepted(this.role);
        }
    }
    
    public static class RequestDuelAction extends DuelMessage.ServerBaseMessage
    {
        public Action action;
        
        public RequestDuelAction(DuelMessageHeader header, Action action)
        {
            super(header);
            this.action = action;
        }
        
        public RequestDuelAction(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            DuelMessageUtility.encodeAction(this.action, buf);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.action = DuelMessageUtility.decodeAction(buf);
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.getDuelManager().receiveActionFrom(player, this.action);
        }
    }
    
    public static class DuelAction extends DuelMessage.ClientBaseMessage
    {
        public PlayerRole source;
        public Action action;
        
        // must be player role because Judges will also be able to do stuff
        public DuelAction(DuelMessageHeader header, PlayerRole source, Action action)
        {
            super(header);
            this.source = source;
            this.action = action;
        }
        
        public DuelAction(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public void encodeMessage(PacketBuffer buf)
        {
            //encodePlayerRole
            DuelMessageUtility.encodeAction(this.action, buf);
        }
        
        @Override
        public void decodeMessage(PacketBuffer buf)
        {
            this.action = DuelMessageUtility.decodeAction(buf);
        }
        
        @Override
        public void handleMessage(PlayerEntity player, IDuelManagerProvider provider)
        {
            provider.handleAction(this.source, this.action);
        }
    }
}
