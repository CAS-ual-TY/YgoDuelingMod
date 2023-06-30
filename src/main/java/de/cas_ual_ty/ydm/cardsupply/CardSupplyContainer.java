package de.cas_ual_ty.ydm.cardsupply;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.rarity.Rarities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CardSupplyContainer extends AbstractContainerMenu
{
    public BlockPos pos;
    public Player player;
    
    public CardSupplyContainer(MenuType<?> type, int id, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(type, id, playerInventory, extraData.readBlockPos());
    }
    
    public CardSupplyContainer(MenuType<?> type, int id, Inventory playerInventory, BlockPos blockPos)
    {
        super(type, id);
        pos = blockPos;
        player = playerInventory.player;
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }
        
        // player hot bar
        for(int x = 0; x < 9; ++x)
        {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
        }
    }
    
    public void giveCard(Properties card, byte imageIndex)
    {
        player.addItem(YdmItems.CARD.get().createItemForCardHolder(new CardHolder(card, imageIndex, Rarities.SUPPLY.name)));
    }
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) // from LockableLootTileEntity::isUsableByPlayer
    {
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }
}
