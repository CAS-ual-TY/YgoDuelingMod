package de.cas_ual_ty.ydm.duelmanager.network;

import de.cas_ual_ty.ydm.duel.block.DuelTileEntity;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.DistExecutor;

public abstract class DuelMessageHeader
{
    // Use DuelMessageUtility encodeHeader / decodeHeader
    
    public final DuelMessageHeaderType type;
    
    public DuelMessageHeader(DuelMessageHeaderType type)
    {
        this.type = type;
    }
    
    public void writeToBuf(PacketBuffer buf)
    {
    }
    
    public void readFromBuf(PacketBuffer buf)
    {
    }
    
    public abstract IDuelManagerProvider getDuelManager(PlayerEntity player);
    
    public static class ContainerHeader extends DuelMessageHeader
    {
        public ContainerHeader(DuelMessageHeaderType type)
        {
            super(type);
        }
        
        @Override
        public IDuelManagerProvider getDuelManager(PlayerEntity player)
        {
            return (IDuelManagerProvider)player.openContainer;
        }
    }
    
    public static class TileEntityHeader extends DuelMessageHeader
    {
        public BlockPos pos;
        
        public TileEntityHeader(DuelMessageHeaderType type, BlockPos pos)
        {
            super(type);
            this.pos = pos;
        }
        
        public TileEntityHeader(DuelMessageHeaderType type)
        {
            super(type);
        }
        
        @Override
        public void writeToBuf(PacketBuffer buf)
        {
            buf.writeBlockPos(this.pos);
        }
        
        @Override
        public void readFromBuf(PacketBuffer buf)
        {
            this.pos = buf.readBlockPos();
        }
        
        @Override
        public IDuelManagerProvider getDuelManager(PlayerEntity player)
        {
            DuelTileEntity te0 = (DuelTileEntity)player.world.getTileEntity(this.pos);
            DuelManager dm = te0.duelManager;
            
            return DistExecutor.<IDuelManagerProvider>unsafeRunForDist(
                () -> () -> new de.cas_ual_ty.ydm.duelmanager.network.ClientDuelManagerProvider(dm),
                () -> () -> new de.cas_ual_ty.ydm.duelmanager.network.ServerDuelManagerProvider(dm));
        }
    }
}
