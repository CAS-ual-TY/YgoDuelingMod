package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class DuelContainer extends Container implements IDuelManagerProvider
{
    public BlockPos pos;
    public DuelTileEntity te;
    
    public DuelContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        this(type, id, playerInventory, extraData.readBlockPos());
    }
    
    public DuelContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, BlockPos blockPos)
    {
        super(type, id);
        this.pos = blockPos;
        this.te = (DuelTileEntity)playerInventory.player.world.getTileEntity(this.pos);
        this.onContainerOpened(playerInventory.player);
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) // from LockableLootTileEntity::isUsableByPlayer
    {
        /*if (player.world.getTileEntity(this.pos) != )
        {
            return false;
        }
        else
        {*/
        return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
        //        }
    }
    
    @Override
    public DuelManager getDuelManager()
    {
        return this.te.duelManager;
    }
    
    public void onContainerOpened(PlayerEntity player)
    {
        this.getDuelManager().playerOpenContainer(player);
    }
    
    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        this.getDuelManager().playerCloseContainer(player);
        super.onContainerClosed(player);
    }
}
