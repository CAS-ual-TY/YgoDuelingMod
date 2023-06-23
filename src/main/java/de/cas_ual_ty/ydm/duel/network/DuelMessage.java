package de.cas_ual_ty.ydm.duel.network;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class DuelMessage
{
    // Server -> Client
    public abstract static class ClientBaseMessage extends DuelMessage
    {
        public ClientBaseMessage(DuelMessageHeader header)
        {
            super(header);
        }
        
        public ClientBaseMessage(FriendlyByteBuf buf)
        {
            super(buf);
        }
        
        @Override
        public final Player getPlayer(NetworkEvent.Context context)
        {
            return YDM.proxy.getClientPlayer();
        }
    }
    
    // Client -> Server
    public abstract static class ServerBaseMessage extends DuelMessage
    {
        public ServerBaseMessage(DuelMessageHeader header)
        {
            super(header);
        }
        
        public ServerBaseMessage(FriendlyByteBuf buf)
        {
            super(buf);
        }
        
        @Override
        public final Player getPlayer(NetworkEvent.Context context)
        {
            return context.getSender();
        }
    }
    
    private DuelMessageHeader header;
    private DuelMessageHeader decodedHeader;
    
    public DuelMessage(DuelMessageHeader header)
    {
        this.header = header;
    }
    
    public DuelMessage(FriendlyByteBuf buf)
    {
        decodedHeader = DuelMessageUtility.decodeHeader(buf);
        decodeMessage(buf);
    }
    
    public void encode(FriendlyByteBuf buf)
    {
        DuelMessageUtility.encodeHeader(header, buf);
        encodeMessage(buf);
    }
    
    public abstract void encodeMessage(FriendlyByteBuf buf);
    
    public abstract void decodeMessage(FriendlyByteBuf buf);
    
    public abstract void handleMessage(Player player, IDuelManagerProvider provider);
    
    public abstract Player getPlayer(NetworkEvent.Context context);
    
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();
        Player player = getPlayer(context);
        
        context.enqueueWork(() ->
        {
            handleMessage(player, decodedHeader.getDuelManager(player));
        });
        
        context.setPacketHandled(true);
    }
    
    public static <M extends DuelMessage> void register(SimpleChannel channel, int index, Class<M> c, Function<FriendlyByteBuf, M> constructor)
    {
        channel.registerMessage(index, c, (m, buf) -> m.encode(buf), constructor, (m, ctx) -> m.handle(ctx));
    }
}