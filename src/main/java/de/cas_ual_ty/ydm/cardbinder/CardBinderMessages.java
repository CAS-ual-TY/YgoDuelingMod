package de.cas_ual_ty.ydm.cardbinder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CardBinderMessages
{
    public static void doForBinderContainer(Player player, Consumer<CardBinderContainer> consumer)
    {
        if(player != null && player.containerMenu instanceof CardBinderContainer)
        {
            consumer.accept((CardBinderContainer) player.containerMenu);
        }
    }
    
    // client changes page, tells server
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
                CardBinderMessages.doForBinderContainer(context.getSender(), (container) ->
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
    
    // client changes search, tells server
    public static class ChangeSearch
    {
        public String search;
        
        public ChangeSearch(String search)
        {
            this.search = search;
        }
        
        public static void encode(ChangeSearch msg, FriendlyByteBuf buf)
        {
            buf.writeUtf(msg.search, Short.MAX_VALUE);
        }
        
        public static ChangeSearch decode(FriendlyByteBuf buf)
        {
            return new ChangeSearch(buf.readUtf(Short.MAX_VALUE));
        }
        
        public static void handle(ChangeSearch msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() ->
            {
                CardBinderMessages.doForBinderContainer(context.getSender(), (container) ->
                {
                    container.updateSearch(msg.search);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    // update pages to client
    public static class UpdatePage
    {
        public int page;
        public int maxPage;
        
        public UpdatePage(int page, int maxPage)
        {
            this.page = page;
            this.maxPage = maxPage;
        }
        
        public static void encode(UpdatePage msg, FriendlyByteBuf buf)
        {
            buf.writeInt(msg.page);
            buf.writeInt(msg.maxPage);
        }
        
        public static UpdatePage decode(FriendlyByteBuf buf)
        {
            return new UpdatePage(buf.readInt(), buf.readInt());
        }
        
        public static void handle(UpdatePage msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() ->
            {
                CardBinderMessages.doForBinderContainer(YDM.proxy.getClientPlayer(), (container) ->
                {
                    container.setClientPage(msg.page);
                    container.setClientMaxPage(msg.maxPage);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    // update cards list to client
    public static class UpdateList
    {
        public int page;
        public List<CardHolder> list;
        
        public UpdateList(int page, List<CardHolder> list)
        {
            this.page = page;
            this.list = list;
        }
        
        public static void encode(UpdateList msg, FriendlyByteBuf buf)
        {
            buf.writeInt(msg.page);
            DuelMessageUtility.encodeList(msg.list, buf, DuelMessageUtility::encodeCardHolder);
        }
        
        public static UpdateList decode(FriendlyByteBuf buf)
        {
            return new UpdateList(buf.readInt(), DuelMessageUtility.decodeList(buf, DuelMessageUtility::decodeCardHolder));
        }
        
        public static void handle(UpdateList msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() ->
            {
                CardBinderMessages.doForBinderContainer(YDM.proxy.getClientPlayer(), (container) ->
                {
                    container.setClientList(msg.page, msg.list);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    // client clicks index, tells server
    public static class IndexClicked
    {
        public int index;
        
        public IndexClicked(int index)
        {
            this.index = index;
        }
        
        public static void encode(IndexClicked msg, FriendlyByteBuf buf)
        {
            buf.writeInt(msg.index);
        }
        
        public static IndexClicked decode(FriendlyByteBuf buf)
        {
            return new IndexClicked(buf.readInt());
        }
        
        public static void handle(IndexClicked msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() ->
            {
                CardBinderMessages.doForBinderContainer(context.getSender(), (container) ->
                {
                    container.indexClicked(msg.index);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
    
    public static class IndexDropped
    {
        public int index;
        
        public IndexDropped(int index)
        {
            this.index = index;
        }
        
        public static void encode(IndexDropped msg, FriendlyByteBuf buf)
        {
            buf.writeInt(msg.index);
        }
        
        public static IndexDropped decode(FriendlyByteBuf buf)
        {
            return new IndexDropped(buf.readInt());
        }
        
        public static void handle(IndexDropped msg, Supplier<NetworkEvent.Context> ctx)
        {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() ->
            {
                CardBinderMessages.doForBinderContainer(context.getSender(), (container) ->
                {
                    container.indexDropped(msg.index);
                });
            });
            
            context.setPacketHandled(true);
        }
    }
}
