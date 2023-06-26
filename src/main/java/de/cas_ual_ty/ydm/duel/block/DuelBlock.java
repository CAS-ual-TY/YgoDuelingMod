package de.cas_ual_ty.ydm.duel.block;

import de.cas_ual_ty.ydm.YdmTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class DuelBlock extends HorizontalDirectionalBlock implements EntityBlock
{
    protected final VoxelShape shape;
    
    public DuelBlock(Properties properties, VoxelShape shape)
    {
        super(properties);
        this.shape = shape;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if(!worldIn.isClientSide && player instanceof ServerPlayer)
        {
            NetworkHooks.openScreen((ServerPlayer) player, getTE(worldIn, pos), pos);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    public DuelTileEntity getTE(Level world, BlockPos pos)
    {
        BlockEntity te = world.getBlockEntity(pos);
        return te instanceof DuelTileEntity ? (DuelTileEntity) te : null;
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return YdmTileEntityTypes.DUEL.get().create(pPos, pState);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        pBuilder.add(HorizontalDirectionalBlock.FACING);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }
    
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        return shape;
    }
}
