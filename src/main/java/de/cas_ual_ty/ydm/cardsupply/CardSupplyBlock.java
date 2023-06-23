package de.cas_ual_ty.ydm.cardsupply;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class CardSupplyBlock extends Block
{
    public CardSupplyBlock(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        if(!worldIn.isClientSide && player instanceof ServerPlayer p)
        {
            NetworkHooks.openScreen(p, getMenuProvider(state, worldIn, pos), pos);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos)
    {
        return new SimpleMenuProvider(
                (id, inventory, player) -> new CardSupplyContainer(YdmContainerTypes.CARD_SUPPLY.get(), id, inventory, pos),
                Component.translatable("container." + YDM.MOD_ID + ".card_supply"));
    }
}
