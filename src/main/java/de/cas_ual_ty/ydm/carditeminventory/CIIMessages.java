package de.cas_ual_ty.ydm.carditeminventory;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CIIMessages
{
    public static void doForContainer(Player player, Consumer<CIIContainer> consumer)
    {
        if(player != null && player.containerMenu instanceof CIIContainer)
        {
            consumer.accept((CIIContainer) player.containerMenu);
        }
    }
    
    public static class SetPage
    {
        public int page;
        
        public SetPage(int page)
        {
            this.page = page;
        }
        
        public static void encode(SetPage msg, FriendlyByteBuf buf)
        {
            buf.writeInt(msg.page);
        }
        
        public static SetPage decode(FriendlyByteBuf buf)
        {
            return new SetPage(buf.readInt());
        }
        
        public static void handle(SetPage msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            
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
        
        public static void encode(ChangePage msg, FriendlyByteBuf buf)
        {
            buf.writeBoolean(msg.nextPage);
        }
        
        public static ChangePage decode(FriendlyByteBuf buf)
        {
            return new ChangePage(buf.readBoolean());
        }
        
        public static void handle(ChangePage msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            
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
