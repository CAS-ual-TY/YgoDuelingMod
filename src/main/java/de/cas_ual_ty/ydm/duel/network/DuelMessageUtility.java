package de.cas_ual_ty.ydm.duel.network;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duel.DeckSource;
import de.cas_ual_ty.ydm.duel.DuelChatMessage;
import de.cas_ual_ty.ydm.duel.DuelState;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionType;
import de.cas_ual_ty.ydm.duel.playfield.CardPosition;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;

public class DuelMessageUtility
{
    public static void encodeHeader(DuelMessageHeader header, PacketBuffer buf)
    {
        buf.writeResourceLocation(header.type.getRegistryName());
        header.writeToBuf(buf);
    }
    
    public static DuelMessageHeader decodeHeader(PacketBuffer buf)
    {
        DuelMessageHeader header = YDM.duelMessageHeaderRegistry.getValue(buf.readResourceLocation()).createHeader();
        header.readFromBuf(buf);
        return header;
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
        return DuelMessageUtility.decodeList(buf, decoder, (size) -> new ArrayList<>(size));
    }
    
    public static void encodeActions(List<Action> actions, PacketBuffer buf)
    {
        DuelMessageUtility.encodeList(actions, buf, DuelMessageUtility::encodeAction);
    }
    
    public static List<Action> decodeActions(PacketBuffer buf)
    {
        return DuelMessageUtility.decodeList(buf, DuelMessageUtility::decodeAction, (size) -> new LinkedList<>());
    }
    
    public static void encodeAction(Action action, PacketBuffer buf)
    {
        DuelMessageUtility.encodeActionType(action.actionType, buf);
        action.writeToBuf(buf);
    }
    
    public static Action decodeAction(PacketBuffer buf)
    {
        ActionType actionType = DuelMessageUtility.decodeActionType(buf);
        return actionType.factory.create(actionType, buf);
    }
    
    public static void encodeActionType(ActionType type, PacketBuffer buf)
    {
        buf.writeResourceLocation(type.getRegistryName());
    }
    
    public static ActionType decodeActionType(PacketBuffer buf)
    {
        return YDM.actionTypeRegistry.getValue(buf.readResourceLocation());
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
    
    public static void encodeZoneOwner(ZoneOwner owner, PacketBuffer buf)
    {
        buf.writeByte(owner.getIndex());
    }
    
    public static ZoneOwner decodeZoneOwner(PacketBuffer buf)
    {
        return ZoneOwner.getFromIndex(buf.readByte());
    }
    
    public static void encodeCardHolder(@Nullable CardHolder card, PacketBuffer buf)
    {
        if(card != null)
        {
            buf.writeBoolean(true);
            CompoundNBT nbt = new CompoundNBT();
            card.writeCardHolderToNBT(nbt);
            buf.writeCompoundTag(nbt);
        }
        else
        {
            buf.writeBoolean(false);
        }
    }
    
    public static CardHolder decodeCardHolder(PacketBuffer buf)
    {
        return buf.readBoolean() ? new CardHolder(buf.readCompoundTag()) : null;
    }
    
    public static void encodeDeckHolder(DeckHolder deck, PacketBuffer buf)
    {
        DuelMessageUtility.encodeList(deck.getMainDeck(), buf, (card, buf1) -> DuelMessageUtility.encodeCardHolder(card, buf1));
        DuelMessageUtility.encodeList(deck.getExtraDeck(), buf, (card, buf1) -> DuelMessageUtility.encodeCardHolder(card, buf1));
        DuelMessageUtility.encodeList(deck.getSideDeck(), buf, (card, buf1) -> DuelMessageUtility.encodeCardHolder(card, buf1));
    }
    
    public static DeckHolder decodeDeckHolder(PacketBuffer buf)
    {
        List<CardHolder> mainDeck = DuelMessageUtility.decodeList(buf, (buf1) -> DuelMessageUtility.decodeCardHolder(buf1));
        List<CardHolder> extraDeck = DuelMessageUtility.decodeList(buf, (buf1) -> DuelMessageUtility.decodeCardHolder(buf1));
        List<CardHolder> sideDeck = DuelMessageUtility.decodeList(buf, (buf1) -> DuelMessageUtility.decodeCardHolder(buf1));
        
        return new DeckHolder(mainDeck, extraDeck, sideDeck);
    }
    
    public static void encodeCardPosition(CardPosition cardPosition, PacketBuffer buf)
    {
        buf.writeByte(cardPosition.getIndex());
    }
    
    public static CardPosition decodeCardPosition(PacketBuffer buf)
    {
        return CardPosition.getFromIndex(buf.readByte());
    }
    
    public static void encodeDuelCard(DuelCard card, PacketBuffer buf)
    {
        DuelMessageUtility.encodeCardHolder(card.getCardHolder(), buf);
        buf.writeBoolean(card.getIsToken());
        DuelMessageUtility.encodeCardPosition(card.getCardPosition(), buf);
        DuelMessageUtility.encodeZoneOwner(card.getOwner(), buf);
    }
    
    public static DuelCard decodeDuelCard(PacketBuffer buf)
    {
        return new DuelCard(DuelMessageUtility.decodeCardHolder(buf), buf.readBoolean(), DuelMessageUtility.decodeCardPosition(buf), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    public static void encodeDuelChatMessage(DuelChatMessage message, PacketBuffer buf)
    {
        buf.writeTextComponent(message.message);
        buf.writeTextComponent(message.playerName);
        DuelMessageUtility.encodePlayerRole(message.playerRole, buf);
        buf.writeBoolean(message.isAnnouncement);
    }
    
    public static DuelChatMessage decodeDuelChatMessage(PacketBuffer buf)
    {
        return new DuelChatMessage(buf.readTextComponent(), (IFormattableTextComponent)buf.readTextComponent(), DuelMessageUtility.decodePlayerRole(buf), buf.readBoolean());
    }
    
    public static void encodeDeckSourceParams(DeckSource deck, PacketBuffer buf)
    {
        buf.writeItemStack(deck.source);
        buf.writeTextComponent(deck.name);
    }
    
    public static DeckSource decodeDeckSourceParams(PacketBuffer buf)
    {
        return new DeckSource(null, buf.readItemStack(), buf.readTextComponent());
    }
}
