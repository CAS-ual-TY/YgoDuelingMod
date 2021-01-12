package de.cas_ual_ty.ydm.carditeminventory;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CardItemInventoryMessages
{
    public static void doForBinderContainer(PlayerEntity player, Consumer<CardItemInventoryContainer> consumer)
    {
        if(player != null && player.openContainer instanceof CardItemInventoryContainer)
        {
            consumer.accept((CardItemInventoryContainer)player.openContainer);
        }
    }
    
    public static class ChangePage
    {
        public boolean nextPage;
        
        public ChangePage(boolean nextPage)
        {
            this.nextPage = nextPage;
        }
        
        public static void encode(ChangePage msg, PacketBuffer buf)
        {
            buf.writeBoolean(msg.nextPage);
        }
        
        public static ChangePage decode(PacketBuffer buf)
        {
            return new ChangePage(buf.readBoolean());
        }
        
        public static void handle(ChangePage msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                CardItemInventoryMessages.doForBinderContainer(context.getSender(), (container) ->
                {
                    if(msg.nextPage)
                    {
                        container.nextPage();
                    }
                    else
                    {
                        container.prevPage();
                    }
                });
            });
            
            context.setPacketHandled(true);
        }
    }
}
