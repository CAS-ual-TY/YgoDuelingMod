package de.cas_ual_ty.ydm.duel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.deckbox.DeckProvider;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionType;
import de.cas_ual_ty.ydm.playmat.PlaymatContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class DuelMessages
{
    public static void doForContainer(PlayerEntity player, BiConsumer<PlaymatContainer, PlayerEntity> consumer)
    {
        if(player != null && player.openContainer instanceof PlaymatContainer)
        {
            consumer.accept((PlaymatContainer)player.openContainer, player);
        }
    }
    
    public static <U> void encodeList(List<U> list, PacketBuffer buf, BiConsumer<U, PacketBuffer> encoder)
    {
        buf.writeInt(list.size());
        
        for(U u : list)
        {
            encoder.accept(u, buf);
        }
    }
    
    public static <U> List<U> decodeList(PacketBuffer buf, Function<PacketBuffer, U> decoder, Function<Integer, List<U>> listCreator)
    {
        int size = buf.readInt();
        List<U> list = listCreator.apply(size);
        
        for(int i = 0; i < size; ++i)
        {
            list.add(decoder.apply(buf));
        }
        
        return list;
    }
    
    public static <U> List<U> decodeList(PacketBuffer buf, Function<PacketBuffer, U> decoder)
    {
        return DuelMessages.decodeList(buf, decoder, (size) -> new ArrayList<>(size));
    }
    
    public static void encodeActions(List<Action> actions, PacketBuffer buf)
    {
        DuelMessages.encodeList(actions, buf, DuelMessages::encodeAction);
    }
    
    public static List<Action> decodeActions(PacketBuffer buf)
    {
        return DuelMessages.decodeList(buf, DuelMessages::decodeAction, (size) -> new LinkedList<>());
    }
    
    public static void encodeAction(Action action, PacketBuffer buf)
    {
        action.writeToBuf(buf);
    }
    
    public static Action decodeAction(PacketBuffer buf)
    {
        ActionType actionType = ActionType.getFromIndex(buf.readByte());
        return actionType.factory.create(actionType, buf);
    }
    
    public static void encodeDuelState(DuelState duelState, PacketBuffer buf)
    {
        buf.writeByte(duelState.getIndex());
    }
    
    public static DuelState decodeDuelState(PacketBuffer buf)
    {
        return DuelState.getFromIndex(buf.readByte());
    }
    
    public static void encodePlayerEntityId(PlayerEntity player, PacketBuffer buf)
    {
        buf.writeInt(player.getEntityId());
    }
    
    public static int decodePlayerEntityId(PacketBuffer buf)
    {
        return buf.readInt();
    }
    
    public static void encodePlayerRole(PlayerRole role, PacketBuffer buf)
    {
        buf.writeByte(role.getIndex());
    }
    
    public static PlayerRole decodePlayerRole(PacketBuffer buf)
    {
        return PlayerRole.getFromIndex(buf.readByte());
    }
    
    public static void encodeDeckProviders(List<DeckProvider> deckProviders, PacketBuffer buf)
    {
        DuelMessages.encodeList(deckProviders, buf, (deckProvider, buf1) -> buf1.writeString(deckProvider.getRegistryName().toString(), 0x100));
    }
    
    public static List<DeckProvider> decodeDeckProviders(PacketBuffer buf)
    {
        // we only add the non-null ones, so not using the #decodeList method
        
        int size = buf.readInt();
        List<DeckProvider> deckProviders = new ArrayList<>(size);
        
        DeckProvider deckProvider;
        for(int i = 0; i < size; ++i)
        {
            deckProvider = YDM.DECK_PROVIDERS_REGISTRY.getValue(new ResourceLocation(buf.readString(0x100)));
            
            if(deckProvider != null)
            {
                deckProviders.add(deckProvider);
            }
        }
        
        return deckProviders;
    }
    
    public static class AvailableRoles
    {
        public List<PlayerRole> playerRoles;
        
        public AvailableRoles(List<PlayerRole> playerRoles)
        {
            this.playerRoles = playerRoles;
        }
        
        public static void encode(AvailableRoles msg, PacketBuffer buf)
        {
            DuelMessages.encodeList(msg.playerRoles, buf, DuelMessages::encodePlayerRole);
        }
        
        public static AvailableRoles decode(PacketBuffer buf)
        {
            return new AvailableRoles(DuelMessages.decodeList(buf, DuelMessages::decodePlayerRole));
        }
        
        public static void handle(AvailableRoles msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(YDM.proxy.getClientPlayer(), (container, player) ->
                {
                    // TODO
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class SelectRole
    {
        public PlayerRole playerRole;
        
        public SelectRole(PlayerRole playerRole)
        {
            this.playerRole = playerRole;
        }
        
        public static void encode(SelectRole msg, PacketBuffer buf)
        {
            DuelMessages.encodePlayerRole(msg.playerRole, buf);
        }
        
        public static SelectRole decode(PacketBuffer buf)
        {
            return new SelectRole(DuelMessages.decodePlayerRole(buf));
        }
        
        public static void handle(SelectRole msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(context.getSender(), (container, player) ->
                {
                    container.getDuelManager().playerSelectRole(player, msg.playerRole);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class UpdateRole
    {
        @Nullable
        public PlayerRole role;
        
        public UUID rolePlayerId;
        
        public UpdateRole(@Nullable PlayerRole role, UUID rolePlayerId)
        {
            this.role = role;
            this.rolePlayerId = rolePlayerId;
        }
        
        public UpdateRole(@Nullable PlayerRole role, PlayerEntity rolePlayer)
        {
            this(role, rolePlayer.getUniqueID());
        }
        
        public static void encode(UpdateRole msg, PacketBuffer buf)
        {
            if(msg.role != null)
            {
                buf.writeBoolean(true);
                DuelMessages.encodePlayerRole(msg.role, buf);
            }
            else
            {
                buf.writeBoolean(false);
            }
            
            buf.writeUniqueId(msg.rolePlayerId);
        }
        
        public static UpdateRole decode(PacketBuffer buf)
        {
            return new UpdateRole(buf.readBoolean() ? DuelMessages.decodePlayerRole(buf) : null, buf.readUniqueId());
        }
        
        public static void handle(UpdateRole msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(YDM.proxy.getClientPlayer(), (container, player) ->
                {
                    PlayerEntity rolePlayer = player.world.getPlayerByUuid(msg.rolePlayerId);
                    
                    if(msg.role != null)
                    {
                        container.getDuelManager().playerSelectRole(rolePlayer, msg.role);
                    }
                    else
                    {
                        container.getDuelManager().onPlayerCloseContainer(rolePlayer);
                    }
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class AvailableDeckProviders
    {
        public List<DeckProvider> deckProviders;
        
        public AvailableDeckProviders(List<DeckProvider> deckProviders)
        {
            this.deckProviders = deckProviders;
        }
        
        public static void encode(AvailableDeckProviders msg, PacketBuffer buf)
        {
            DuelMessages.encodeDeckProviders(msg.deckProviders, buf);
        }
        
        public static AvailableDeckProviders decode(PacketBuffer buf)
        {
            return new AvailableDeckProviders(DuelMessages.decodeDeckProviders(buf));
        }
        
        public static void handle(AvailableDeckProviders msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(YDM.proxy.getClientPlayer(), (container, player) ->
                {
                    // TODO
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class UpdateDuelState
    {
        public DuelState duelState;
        
        public UpdateDuelState(DuelState duelState)
        {
            this.duelState = duelState;
        }
        
        public static void encode(UpdateDuelState msg, PacketBuffer buf)
        {
            DuelMessages.encodeDuelState(msg.duelState, buf);
        }
        
        public static UpdateDuelState decode(PacketBuffer buf)
        {
            return new UpdateDuelState(DuelMessages.decodeDuelState(buf));
        }
        
        public static void handle(UpdateDuelState msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(YDM.proxy.getClientPlayer(), (container, player) ->
                {
                    container.getDuelManager().setDuelStateAndUpdate(msg.duelState);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class RequestFullUpdate
    {
        public RequestFullUpdate()
        {
        }
        
        public static void encode(RequestFullUpdate msg, PacketBuffer buf)
        {
        }
        
        public static RequestFullUpdate decode(PacketBuffer buf)
        {
            return new RequestFullUpdate();
        }
        
        public static void handle(RequestFullUpdate msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(context.getSender(), (container, player) ->
                {
                    container.getDuelManager().sendAllTo(player);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class RequestReady
    {
        public boolean ready;
        
        public RequestReady(boolean ready)
        {
            this.ready = ready;
        }
        
        public static void encode(RequestReady msg, PacketBuffer buf)
        {
            buf.writeBoolean(msg.ready);
        }
        
        public static RequestReady decode(PacketBuffer buf)
        {
            return new RequestReady(buf.readBoolean());
        }
        
        public static void handle(RequestReady msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(context.getSender(), (container, player) ->
                {
                    container.getDuelManager().requestReady(player, msg.ready);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class UpdateReady
    {
        public PlayerRole role;
        public boolean ready;
        
        public UpdateReady(PlayerRole role, boolean ready)
        {
            this.role = role;
            this.ready = ready;
        }
        
        public static void encode(UpdateReady msg, PacketBuffer buf)
        {
            DuelMessages.encodePlayerRole(msg.role, buf);
            buf.writeBoolean(msg.ready);
        }
        
        public static UpdateReady decode(PacketBuffer buf)
        {
            return new UpdateReady(DuelMessages.decodePlayerRole(buf), buf.readBoolean());
        }
        
        public static void handle(UpdateReady msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(YDM.proxy.getClientPlayer(), (container, player) ->
                {
                    container.getDuelManager().updateReady(msg.role, msg.ready);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
}
