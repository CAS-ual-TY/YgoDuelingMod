package de.cas_ual_ty.ydm.duel.network;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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
        
        public ClientBaseMessage(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public final PlayerEntity getPlayer(Context context)
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
        
        public ServerBaseMessage(PacketBuffer buf)
        {
            super(buf);
        }
        
        @Override
        public final PlayerEntity getPlayer(Context context)
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
    
    public DuelMessage(PacketBuffer buf)
    {
        decodedHeader = DuelMessageUtility.decodeHeader(buf);
        decodeMessage(buf);
    }
    
    public void encode(PacketBuffer buf)
    {
        DuelMessageUtility.encodeHeader(header, buf);
        encodeMessage(buf);
    }
    
    public abstract void encodeMessage(PacketBuffer buf);
    
    public abstract void decodeMessage(PacketBuffer buf);
    
    public abstract void handleMessage(PlayerEntity player, IDuelManagerProvider provider);
    
    public abstract PlayerEntity getPlayer(NetworkEvent.Context context);
    
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();
        PlayerEntity player = getPlayer(context);
        
        context.enqueueWork(() ->
        {
            handleMessage(player, decodedHeader.getDuelManager(player));
        });
        
        context.setPacketHandled(true);
    }
    
    public static <M extends DuelMessage> void register(SimpleChannel channel, int index, Class<M> c, Function<PacketBuffer, M> constructor)
    {
        channel.registerMessage(index, c, (m, buf) -> m.encode(buf), constructor, (m, ctx) -> m.handle(ctx));
    }
}