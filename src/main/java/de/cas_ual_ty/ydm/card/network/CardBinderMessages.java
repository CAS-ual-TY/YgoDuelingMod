package de.cas_ual_ty.ydm.card.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.Rarity;
import de.cas_ual_ty.ydm.cardbinder.CardBinderContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CardBinderMessages
{
    public static void doForBinderContainer(PlayerEntity player, Consumer<CardBinderContainer> consumer)
    {
        if(player != null && player.openContainer instanceof CardBinderContainer)
        {
            consumer.accept((CardBinderContainer)player.openContainer);
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
        
        public ChangePage()
        {
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
        
        public UpdatePage()
        {
        }
        
        public static void encode(UpdatePage msg, PacketBuffer buf)
        {
            buf.writeInt(msg.page);
            buf.writeInt(msg.maxPage);
        }
        
        public static UpdatePage decode(PacketBuffer buf)
        {
            return new UpdatePage(buf.readInt(), buf.readInt());
        }
        
        public static void handle(UpdatePage msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
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
        
        public UpdateList()
        {
        }
        
        public static void encode(UpdateList msg, PacketBuffer buf)
        {
            buf.writeInt(msg.page);
            
            buf.writeInt(msg.list.size());
            for(CardHolder cardHolder : msg.list)
            {
                buf.writeString(cardHolder.getCard().getSetId(), 0x100);
                buf.writeByte(cardHolder.getOverriddenImageIndex());
                buf.writeString(cardHolder.getOverriddenRarity() != null ? cardHolder.getOverriddenRarity().name : "", 0x100);
            }
        }
        
        public static UpdateList decode(PacketBuffer buf)
        {
            int page = buf.readInt();
            
            int size = buf.readInt();
            List<CardHolder> list = new ArrayList<>(size);
            
            for(int i = 0; i < size; ++i)
            {
                list.add(new CardHolder(Database.CARDS_LIST.get(buf.readString()), buf.readByte(), Rarity.fromString(buf.readString())));
            }
            
            return new UpdateList(page, list);
        }
        
        public static void handle(UpdateList msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
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
        public boolean shiftDown;
        
        public IndexClicked(int index, boolean shiftDown)
        {
            this.index = index;
            this.shiftDown = shiftDown;
        }
        
        public IndexClicked()
        {
        }
        
        public static void encode(IndexClicked msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
            buf.writeBoolean(msg.shiftDown);
        }
        
        public static IndexClicked decode(PacketBuffer buf)
        {
            return new IndexClicked(buf.readInt(), buf.readBoolean());
        }
        
        public static void handle(IndexClicked msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
            context.enqueueWork(() ->
            {
                CardBinderMessages.doForBinderContainer(context.getSender(), (container) ->
                {
                    container.indexClicked(msg.index, msg.shiftDown);
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
        
        public IndexDropped()
        {
        }
        
        public static void encode(IndexDropped msg, PacketBuffer buf)
        {
            buf.writeInt(msg.index);
        }
        
        public static IndexDropped decode(PacketBuffer buf)
        {
            return new IndexDropped(buf.readInt());
        }
        
        public static void handle(IndexDropped msg, Supplier<NetworkEvent.Context> ctx)
        {
            Context context = ctx.get();
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
