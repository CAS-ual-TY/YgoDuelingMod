package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class SimpleDuelBlock extends Block implements INamedContainerProvider
{
    public SimpleDuelBlock(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return YdmTileEntityTypes.SIMPLE_DUEL_TILE_ENTITY.create();
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
    {
        return null;
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".simple_duel_block");
    }
}
