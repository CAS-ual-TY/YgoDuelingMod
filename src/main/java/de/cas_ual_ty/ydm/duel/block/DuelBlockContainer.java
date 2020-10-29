package de.cas_ual_ty.ydm.duel.block;

import de.cas_ual_ty.ydm.duel.DuelContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class DuelBlockContainer extends DuelContainer
{
    public BlockPos pos;
    
    public DuelBlockContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        this(type, id, playerInventory, extraData.readBlockPos());
    }
    
    public DuelBlockContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, BlockPos blockPos)
    {
        super(type, id, playerInventory.player, ((DuelTileEntity)playerInventory.player.world.getTileEntity(blockPos)).duelManager);
        this.pos = blockPos;
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) // from LockableLootTileEntity::isUsableByPlayer
    {
        return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }
}
