package de.cas_ual_ty.ydm.carditeminventory;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CIIMessages
{
    public static void doForContainer(PlayerEntity player, Consumer<CIIContainer> consumer)
    {
        if(player != null && player.openContainer instanceof CIIContainer)
        {
            consumer.accept((CIIContainer)player.openContainer);
        }
    }
    
    public static class SetPage
    {
        public int page;
        
        public SetPage(int page)
        {
            this.page = page;
        }
        
        public static void encode(SetPage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.page);
        }
        
        public static SetPage decode(PacketBuffer buf)
        {
            return new SetPage(buf.readInt());
        }
        
        public static void handle(SetPage msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            
            context.enqueueWork(() ->
            {
                CIIMessages.doForContainer(YDM.proxy.getClientPlayer(), (container) ->
                {
                    container.setPage(msg.page);
                });
            });
            
            context.setPacketHandled(true);
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
                CIIMessages.doForContainer(context.getSender(), (container) ->
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
