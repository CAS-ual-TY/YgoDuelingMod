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
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionType;
import de.cas_ual_ty.ydm.playmat.PlaymatContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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
    
    public static void encodeCardHolder(CardHolder card, PacketBuffer buf)
    {
        CompoundNBT nbt = new CompoundNBT();
        card.writeCardHolderToNBT(nbt);
        buf.writeCompoundTag(nbt);
    }
    
    public static CardHolder decodeCardHolder(PacketBuffer buf)
    {
        return new CardHolder(buf.readCompoundTag());
    }
    
    public static void encodeDeckHolder(DeckHolder deck, PacketBuffer buf)
    {
        DuelMessages.encodeList(deck.getMainDeck(), buf, (card, buf1) -> DuelMessages.encodeCardHolder(card, buf1));
        DuelMessages.encodeList(deck.getExtraDeck(), buf, (card, buf1) -> DuelMessages.encodeCardHolder(card, buf1));
        DuelMessages.encodeList(deck.getSideDeck(), buf, (card, buf1) -> DuelMessages.encodeCardHolder(card, buf1));
    }
    
    public static DeckHolder decodeDeckHolder(PacketBuffer buf)
    {
        List<CardHolder> mainDeck = DuelMessages.decodeList(buf, (buf1) -> DuelMessages.decodeCardHolder(buf1));
        List<CardHolder> extraDeck = DuelMessages.decodeList(buf, (buf1) -> DuelMessages.decodeCardHolder(buf1));
        List<CardHolder> sideDeck = DuelMessages.decodeList(buf, (buf1) -> DuelMessages.decodeCardHolder(buf1));
        
        return new DeckHolder(mainDeck, extraDeck, sideDeck);
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
                    container.updateDuelState(msg.duelState);
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
    
    public static class SendDeckProviders
    {
        public List<ResourceLocation> deckProviderRLs;
        
        public SendDeckProviders(List<ResourceLocation> deckProviderRLs)
        {
            this.deckProviderRLs = deckProviderRLs;
        }
        
        public static void encode(SendDeckProviders msg, PacketBuffer buf)
        {
            DuelMessages.encodeList(msg.deckProviderRLs, buf, (deckProviderRL, buf1) -> buf1.writeResourceLocation(deckProviderRL));
        }
        
        public static SendDeckProviders decode(PacketBuffer buf)
        {
            return new SendDeckProviders(DuelMessages.decodeList(buf, (buf1) -> buf1.readResourceLocation()));
        }
        
        public static void handle(SendDeckProviders msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(YDM.proxy.getClientPlayer(), (container, player) ->
                {
                    container.receiveDeckProviders(msg.deckProviderRLs);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class RequestDeck
    {
        public ResourceLocation deckProviderRL;
        
        public RequestDeck(ResourceLocation deckProviderRL)
        {
            this.deckProviderRL = deckProviderRL;
        }
        
        public static void encode(RequestDeck msg, PacketBuffer buf)
        {
            buf.writeResourceLocation(msg.deckProviderRL);
        }
        
        public static RequestDeck decode(PacketBuffer buf)
        {
            return new RequestDeck(buf.readResourceLocation());
        }
        
        public static void handle(RequestDeck msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(context.getSender(), (container, player) ->
                {
                    container.getDuelManager().requestDeck(msg.deckProviderRL, player);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class SendDeck
    {
        public ResourceLocation deckProviderRL;
        public DeckHolder deck;
        
        public SendDeck(ResourceLocation deckProviderRL, DeckHolder deck)
        {
            this.deckProviderRL = deckProviderRL;
            this.deck = deck;
        }
        
        public static void encode(SendDeck msg, PacketBuffer buf)
        {
            buf.writeResourceLocation(msg.deckProviderRL);
            DuelMessages.encodeDeckHolder(msg.deck, buf);
        }
        
        public static SendDeck decode(PacketBuffer buf)
        {
            return new SendDeck(buf.readResourceLocation(), DuelMessages.decodeDeckHolder(buf));
        }
        
        public static void handle(SendDeck msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                DuelMessages.doForContainer(YDM.proxy.getClientPlayer(), (container, player) ->
                {
                    container.receiveSingleDeckProvider(msg.deckProviderRL, msg.deck);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
}
