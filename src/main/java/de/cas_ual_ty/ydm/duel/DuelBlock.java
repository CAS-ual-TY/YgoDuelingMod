package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.YdmTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DuelBlock extends HorizontalBlock
{
    protected final VoxelShape shape;
    
    public DuelBlock(Properties properties, VoxelShape shape)
    {
        super(properties);
        this.shape = shape;
    }
    
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(worldIn.isRemote)
        {
            return ActionResultType.SUCCESS;
        }
        else
        {
            INamedContainerProvider inamedcontainerprovider = this.getContainerFromTE(worldIn, pos);
            
            if(inamedcontainerprovider != null)
            {
                NetworkHooks.openGui((ServerPlayerEntity)player, inamedcontainerprovider, pos);
            }
            
            return ActionResultType.SUCCESS;
        }
    }
    
    public INamedContainerProvider getContainerFromTE(World world, BlockPos pos)
    {
        return this.getTE(world, pos);
    }
    
    public DuelTileEntity getTE(World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof DuelTileEntity ? (DuelTileEntity)te : null;
    }
    
    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return YdmTileEntityTypes.DUEL.create();
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }
    
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HorizontalBlock.HORIZONTAL_FACING);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.shape;
    }
}
