package de.cas_ual_ty.ydm.duel.block;

import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeader;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeaders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class DuelTileEntity extends TileEntity implements INamedContainerProvider
{
    public DuelManager duelManager;
    
    public DuelTileEntity(TileEntityType<?> tileEntityType)
    {
        super(tileEntityType);
        duelManager = null;
    }
    
    @Override
    public void setLevelAndPosition(World world, BlockPos pos)
    {
        super.setLevelAndPosition(world, pos);
        
        // world is still null at constructor, so we gotta do this here
        duelManager = createDuelManager();
    }
    
    public DuelManager createDuelManager()
    {
        return new DuelManager(level.isClientSide, this::createHeader);
    }
    
    public DuelMessageHeader createHeader()
    {
        return new DuelMessageHeader.TileEntityHeader(DuelMessageHeaders.TILE_ENTITY, getBlockPos());
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player)
    {
        return new DuelBlockContainer(YdmContainerTypes.DUEL_BLOCK_CONTAINER, id, playerInv, worldPosition);
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container.ydm.duel");
    }
}
