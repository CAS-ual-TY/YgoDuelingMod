package de.cas_ual_ty.ydm.cardsupply;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.CustomCards;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CardSupplyMessages
{
    public static void doForBinderContainer(PlayerEntity player, Consumer<CardSupplyContainer> consumer)
    {
        if(player != null && player.openContainer instanceof CardSupplyContainer)
        {
            consumer.accept((CardSupplyContainer)player.openContainer);
        }
    }
    
    public static class RequestCard
    {
        public Card card;
        
        public RequestCard(Card card)
        {
            this.card = card;
        }
        
        public static void encode(RequestCard msg, PacketBuffer buf)
        {
            buf.writeString(msg.card.getSetId(), 0x100);
        }
        
        public static RequestCard decode(PacketBuffer buf)
        {
            return new RequestCard(YdmDatabase.CARDS_LIST.get(buf.readString(0x100)));
        }
        
        public static void handle(RequestCard msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            
            if(msg.card != null && msg.card != CustomCards.DUMMY_CARD)
            {
                context.enqueueWork(() ->
                {
                    CardSupplyMessages.doForBinderContainer(context.getSender(), (container) ->
                    {
                        container.giveCard(msg.card);
                    });
                });
            }
            
            context.setPacketHandled(true);
        }
    }
}
