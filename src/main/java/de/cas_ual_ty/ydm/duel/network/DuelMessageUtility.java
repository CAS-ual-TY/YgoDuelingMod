package de.cas_ual_ty.ydm.duel.network;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.*;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionType;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DuelMessageUtility
{
    public static void encodeHeader(DuelMessageHeader header, FriendlyByteBuf buf)
    {
        buf.writeRegistryIdUnsafe(YDM.duelMessageHeaderRegistry.get(), header.type);
        header.writeToBuf(buf);
    }
    
    public static DuelMessageHeader decodeHeader(FriendlyByteBuf buf)
    {
        DuelMessageHeader header = buf.readRegistryIdUnsafe(YDM.duelMessageHeaderRegistry.get()).createHeader();
        header.readFromBuf(buf);
        return header;
    }
    
    public static <U> void encodeList(List<U> list, FriendlyByteBuf buf, BiConsumer<U, FriendlyByteBuf> encoder)
    {
        buf.writeInt(list.size());
        
        for(U u : list)
        {
            encoder.accept(u, buf);
        }
    }
    
    public static <U> List<U> decodeList(FriendlyByteBuf buf, Function<FriendlyByteBuf, U> decoder, Function<Integer, List<U>> listCreator)
    {
        int size = buf.readInt();
        List<U> list = listCreator.apply(size);
        
        for(int i = 0; i < size; ++i)
        {
            list.add(decoder.apply(buf));
        }
        
        return list;
    }
    
    public static <U> List<U> decodeList(FriendlyByteBuf buf, Function<FriendlyByteBuf, U> decoder)
    {
        return DuelMessageUtility.decodeList(buf, decoder, (size) -> new ArrayList<>(size));
    }
    
    public static void encodeActions(List<Action> actions, FriendlyByteBuf buf)
    {
        DuelMessageUtility.encodeList(actions, buf, DuelMessageUtility::encodeAction);
    }
    
    public static List<Action> decodeActions(FriendlyByteBuf buf)
    {
        return DuelMessageUtility.decodeList(buf, DuelMessageUtility::decodeAction, (size) -> new LinkedList<>());
    }
    
    public static void encodeAction(Action action, FriendlyByteBuf buf)
    {
        DuelMessageUtility.encodeActionType(action.actionType, buf);
        action.writeToBuf(buf);
    }
    
    public static Action decodeAction(FriendlyByteBuf buf)
    {
        ActionType actionType = DuelMessageUtility.decodeActionType(buf);
        return actionType.factory.create(actionType, buf);
    }
    
    public static void encodeActionType(ActionType type, FriendlyByteBuf buf)
    {
        buf.writeRegistryIdUnsafe(YDM.actionTypeRegistry.get(), type);
    }
    
    public static ActionType decodeActionType(FriendlyByteBuf buf)
    {
        return buf.readRegistryIdUnsafe(YDM.actionTypeRegistry.get());
    }
    
    public static void encodeDuelState(DuelState duelState, FriendlyByteBuf buf)
    {
        buf.writeByte(duelState.getIndex());
    }
    
    public static DuelState decodeDuelState(FriendlyByteBuf buf)
    {
        return DuelState.getFromIndex(buf.readByte());
    }
    
    public static void encodePlayerId(Player player, FriendlyByteBuf buf)
    {
        buf.writeInt(player.getId());
    }
    
    public static int decodePlayerId(FriendlyByteBuf buf)
    {
        return buf.readInt();
    }
    
    public static void encodePlayerRole(PlayerRole role, FriendlyByteBuf buf)
    {
        buf.writeByte(role.getIndex());
    }
    
    public static PlayerRole decodePlayerRole(FriendlyByteBuf buf)
    {
        return PlayerRole.getFromIndex(buf.readByte());
    }
    
    public static void encodeZoneOwner(ZoneOwner owner, FriendlyByteBuf buf)
    {
        buf.writeByte(owner.getIndex());
    }
    
    public static ZoneOwner decodeZoneOwner(FriendlyByteBuf buf)
    {
        return ZoneOwner.getFromIndex(buf.readByte());
    }
    
    public static void encodeCardHolder(@Nullable CardHolder card, FriendlyByteBuf buf)
    {
        if(card != null)
        {
            buf.writeBoolean(true);
            CompoundTag nbt = new CompoundTag();
            card.writeCardHolderToNBT(nbt);
            buf.writeNbt(nbt);
        }
        else
        {
            buf.writeBoolean(false);
        }
    }
    
    public static CardHolder decodeCardHolder(FriendlyByteBuf buf)
    {
        return buf.readBoolean() ? new CardHolder(buf.readNbt()) : null;
    }
    
    public static void encodeDeckHolder(DeckHolder deck, FriendlyByteBuf buf)
    {
        DuelMessageUtility.encodeList(deck.getMainDeck(), buf, (card, buf1) -> DuelMessageUtility.encodeCardHolder(card, buf1));
        DuelMessageUtility.encodeList(deck.getExtraDeck(), buf, (card, buf1) -> DuelMessageUtility.encodeCardHolder(card, buf1));
        DuelMessageUtility.encodeList(deck.getSideDeck(), buf, (card, buf1) -> DuelMessageUtility.encodeCardHolder(card, buf1));
    }
    
    public static DeckHolder decodeDeckHolder(FriendlyByteBuf buf)
    {
        List<CardHolder> mainDeck = DuelMessageUtility.decodeList(buf, (buf1) -> DuelMessageUtility.decodeCardHolder(buf1));
        List<CardHolder> extraDeck = DuelMessageUtility.decodeList(buf, (buf1) -> DuelMessageUtility.decodeCardHolder(buf1));
        List<CardHolder> sideDeck = DuelMessageUtility.decodeList(buf, (buf1) -> DuelMessageUtility.decodeCardHolder(buf1));
        
        return new DeckHolder(mainDeck, extraDeck, sideDeck);
    }
    
    public static void encodeCardPosition(CardPosition cardPosition, FriendlyByteBuf buf)
    {
        buf.writeByte(cardPosition.getIndex());
    }
    
    public static CardPosition decodeCardPosition(FriendlyByteBuf buf)
    {
        return CardPosition.getFromIndex(buf.readByte());
    }
    
    public static void encodeDuelCard(DuelCard card, FriendlyByteBuf buf)
    {
        DuelMessageUtility.encodeCardHolder(card.getCardHolder(), buf);
        buf.writeBoolean(card.getIsToken());
        DuelMessageUtility.encodeCardPosition(card.getCardPosition(), buf);
        DuelMessageUtility.encodeZoneOwner(card.getOwner(), buf);
    }
    
    public static DuelCard decodeDuelCard(FriendlyByteBuf buf)
    {
        return new DuelCard(DuelMessageUtility.decodeCardHolder(buf), buf.readBoolean(), DuelMessageUtility.decodeCardPosition(buf), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    public static void encodeDuelChatMessage(DuelChatMessage message, FriendlyByteBuf buf)
    {
        buf.writeComponent(message.message);
        buf.writeComponent(message.playerName);
        DuelMessageUtility.encodePlayerRole(message.sourceRole, buf);
        buf.writeBoolean(message.isAnnouncement);
    }
    
    public static DuelChatMessage decodeDuelChatMessage(FriendlyByteBuf buf)
    {
        return new DuelChatMessage(buf.readComponent(), buf.readComponent(), DuelMessageUtility.decodePlayerRole(buf), buf.readBoolean());
    }
    
    public static void encodeDeckSourceParams(DeckSource deck, FriendlyByteBuf buf)
    {
        buf.writeItem(deck.source);
        buf.writeComponent(deck.name);
    }
    
    public static DeckSource decodeDeckSourceParams(FriendlyByteBuf buf)
    {
        return new DeckSource(null, buf.readItem(), buf.readComponent());
    }
    
    public static void encodePhase(DuelPhase phase, FriendlyByteBuf buf)
    {
        buf.writeByte(phase.getIndex());
    }
    
    public static DuelPhase decodePhase(FriendlyByteBuf buf)
    {
        return DuelPhase.getFromIndex(buf.readByte());
    }
}
