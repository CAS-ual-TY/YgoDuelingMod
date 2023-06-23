package de.cas_ual_ty.ydm.duel.block;

import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeaders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DuelTileEntity extends BlockEntity implements MenuProvider
{
    public DuelManager duelManager;
    
    public DuelTileEntity(BlockEntityType<DuelTileEntity> tileEntityType, BlockPos pos, BlockState state)
    {
        super(tileEntityType, pos, state);
        duelManager = null;
    }
    
    @Override
    public void setLevel(Level world)
    {
        super.setLevel(world);
        
        // world is still null at constructor, so we gotta do this here
        duelManager = createDuelManager();
    }
    
    public DuelManager createDuelManager()
    {
        return new DuelManager(level.isClientSide, this::createHeader);
    }
    
    public DuelMessageHeader createHeader()
    {
        return new DuelMessageHeader.TileEntityHeader(DuelMessageHeaders.TILE_ENTITY.get(), getBlockPos());
    }
    
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player)
    {
        return new DuelBlockContainer(YdmContainerTypes.DUEL_BLOCK_CONTAINER.get(), id, playerInv, worldPosition);
    }
    
    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container.ydm.duel");
    }
}
