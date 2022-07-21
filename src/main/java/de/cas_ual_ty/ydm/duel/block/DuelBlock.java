package de.cas_ual_ty.ydm.duel.block;

import de.cas_ual_ty.ydm.YdmTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if(!worldIn.isClientSide && player instanceof ServerPlayerEntity)
        {
            NetworkHooks.openGui((ServerPlayerEntity) player, getTE(worldIn, pos), pos);
        }
        
        return ActionResultType.SUCCESS;
    }
    
    public DuelTileEntity getTE(World world, BlockPos pos)
    {
        TileEntity te = world.getBlockEntity(pos);
        return te instanceof DuelTileEntity ? (DuelTileEntity) te : null;
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
        return defaultBlockState().setValue(HorizontalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HorizontalBlock.FACING);
    }
    
    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return shape;
    }
}
