package de.cas_ual_ty.ydm.cardsupply;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.Rarity;
import de.cas_ual_ty.ydm.card.properties.Properties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class CardSupplyContainer extends Container
{
    public BlockPos pos;
    public PlayerEntity player;
    
    public CardSupplyContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        this(type, id, playerInventory, extraData.readBlockPos());
    }
    
    public CardSupplyContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, BlockPos blockPos)
    {
        super(type, id);
        this.pos = blockPos;
        this.player = playerInventory.player;
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }
        
        // player hot bar
        for(int x = 0; x < 9; ++x)
        {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
        }
    }
    
    public void giveCard(Properties card, byte imageIndex)
    {
        this.player.addItemStackToInventory(YdmItems.CARD.createItemForCardHolder(new CardHolder(card, imageIndex, Rarity.SUPPLY.name)));
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) // from LockableLootTileEntity::isUsableByPlayer
    {
        return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }
}
