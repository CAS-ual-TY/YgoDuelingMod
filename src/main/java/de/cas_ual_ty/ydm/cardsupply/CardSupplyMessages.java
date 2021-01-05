package de.cas_ual_ty.ydm.cardsupply;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.Properties;
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
        public Properties card;
        public byte imageIndex;
        
        public RequestCard(Properties card, byte imageIndex)
        {
            this.card = card;
            this.imageIndex = imageIndex;
        }
        
        public static void encode(RequestCard msg, PacketBuffer buf)
        {
            buf.writeLong(msg.card.getId());
            buf.writeByte(msg.imageIndex);
        }
        
        public static RequestCard decode(PacketBuffer buf)
        {
            return new RequestCard(YdmDatabase.PROPERTIES_LIST.get(buf.readLong()), buf.readByte());
        }
        
        public static void handle(RequestCard msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            
            if(msg.card != null && msg.card != Properties.DUMMY)
            {
                context.enqueueWork(() ->
                {
                    CardSupplyMessages.doForBinderContainer(context.getSender(), (container) ->
                    {
                        container.giveCard(msg.card, msg.imageIndex);
                    });
                });
            }
            
            context.setPacketHandled(true);
        }
    }
}
