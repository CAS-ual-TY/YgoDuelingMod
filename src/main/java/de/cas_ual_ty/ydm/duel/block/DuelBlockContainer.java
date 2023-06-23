package de.cas_ual_ty.ydm.duel.block;

import de.cas_ual_ty.ydm.duel.DuelContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class DuelBlockContainer extends DuelContainer
{
    public BlockPos pos;
    
    public DuelBlockContainer(MenuType<?> type, int id, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(type, id, playerInventory, extraData.readBlockPos());
    }
    
    public DuelBlockContainer(MenuType<?> type, int id, Inventory playerInventory, BlockPos blockPos)
    {
        super(type, id, playerInventory.player, ((DuelTileEntity) playerInventory.player.level.getBlockEntity(blockPos)).duelManager);
        pos = blockPos;
    }
    
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) // from LockableLootTileEntity::isUsableByPlayer
    {
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }
    
    
}
