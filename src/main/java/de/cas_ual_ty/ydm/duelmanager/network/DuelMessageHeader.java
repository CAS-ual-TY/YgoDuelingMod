package de.cas_ual_ty.ydm.duelmanager.network;

import java.util.function.Function;

import de.cas_ual_ty.ydm.duel.IDuelManagerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class DuelMessageHeader extends ForgeRegistryEntry<DuelMessageHeader>
{
    public abstract void writeToBuf(PacketBuffer buf);
    
    public abstract Function<PlayerEntity, IDuelManagerProvider> readFromBuf(PacketBuffer buf);
    
    public static class ContainerHeader extends DuelMessageHeader
    {
        @Override
        public void writeToBuf(PacketBuffer buf)
        {
            
        }
        
        @Override
        public Function<PlayerEntity, IDuelManagerProvider> readFromBuf(PacketBuffer buf)
        {
            return (player) -> (IDuelManagerProvider)player.openContainer;
        }
    }
}
