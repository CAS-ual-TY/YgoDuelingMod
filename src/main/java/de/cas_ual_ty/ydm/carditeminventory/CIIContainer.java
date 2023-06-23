package de.cas_ual_ty.ydm.carditeminventory;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;

public class CIIContainer extends AbstractContainerMenu
{
    public static final int INV_SIZE = 4 * 9;
    public static final int PAGE_SIZE = 6 * 9;
    
    protected final Player player;
    protected final IItemHandler itemHandler;
    
    protected int page;
    protected final int maxPage;
    protected boolean filling;
    
    public CIIContainer(MenuType<?> type, int id, Inventory playerInventoryIn, IItemHandler itemHandler)
    {
        super(type, id);
        
        player = playerInventoryIn.player;
        this.itemHandler = itemHandler;
        
        attemptLoad();
        
        createBottomSlots(playerInventoryIn);
        createTopSlots();
        
        page = 0;
        maxPage = Mth.ceil(this.itemHandler.getSlots() / (double) PAGE_SIZE);
    }
    
    public CIIContainer(MenuType<?> type, int id, Inventory playerInventoryIn, int itemHandlerSize)
    {
        this(type, id, playerInventoryIn, new ItemStackHandler(itemHandlerSize));
    }
    
    public CIIContainer(MenuType<?> type, int id, Inventory playerInventoryIn, FriendlyByteBuf extraData)
    {
        this(type, id, playerInventoryIn, extraData.readInt());
    }
    
    protected void attemptLoad()
    {
        if(itemHandler instanceof YDMItemHandler)
        {
            ((YDMItemHandler) itemHandler).load();
        }
    }
    
    protected void attemptSave()
    {
        if(itemHandler instanceof YDMItemHandler)
        {
            ((YDMItemHandler) itemHandler).save();
        }
    }
    
    protected void createTopSlots()
    {
        for(int j = 0; j < 6; ++j)
        {
            for(int k = 0; k < 9; ++k)
            {
                int slotIndex = k + j * 9;
                int itemIndex = page * PAGE_SIZE + slotIndex;
                slotIndex += 4 * 9;
                
                if(itemIndex >= itemHandler.getSlots())
                {
                    continue;
                }
                
                addSlot(new SplitItemHandlerSlot(itemHandler, slotIndex, 8 + k * 18, 18 + j * 18, itemIndex)
                {
                    @Override
                    public boolean mayPlace(@Nonnull ItemStack stack)
                    {
                        return canPutStack(stack);
                    }
                    
                    @Override
                    public boolean mayPickup(Player playerIn)
                    {
                        return canTakeStack(playerIn, getItem());
                    }
                });
            }
        }
    }
    
    protected void createBottomSlots(Inventory playerInventoryIn)
    {
        final int i = (6 - 4) * 18;
        
        for(int l = 0; l < 3; ++l)
        {
            for(int j1 = 0; j1 < 9; ++j1)
            {
                addSlot(new Slot(playerInventoryIn, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }
        
        for(int i1 = 0; i1 < 9; ++i1)
        {
            addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i));
        }
    }
    
    public boolean canPutStack(ItemStack itemStack)
    {
        return false;
    }
    
    public boolean canTakeStack(Player player, ItemStack itemStack)
    {
        return true;
    }
    
    // sets the page but does not update anything
    public void setPage(int page)
    {
        this.page = page;
    }
    
    public int getPage()
    {
        return page;
    }
    
    public int getMaxPage()
    {
        return maxPage;
    }
    
    protected void updatePage()
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new CIIMessages.SetPage(page));
    }
    
    public void nextPage()
    {
        ++page;
        
        if(page >= maxPage)
        {
            page = 0;
        }
        
        updatePage();
        updateSlots();
    }
    
    public void prevPage()
    {
        --page;
        
        if(page < 0)
        {
            page = maxPage - 1;
        }
        
        updatePage();
        updateSlots();
    }
    
    public void updateSlots()
    {
        attemptSave();
        slots.clear();
        createBottomSlots(player.getInventory());
        createTopSlots();
    }
    
    @Override
    public void slotsChanged(Container inventoryIn)
    {
    }
    
    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        Slot slot = slots.get(index);
        ItemStack original = slot.getItem().copy();
        
        if(!original.isEmpty())
        {
            ItemStack itemStack = slot.getItem().split(1);
            
            if(index < INV_SIZE)
            {
                // move into container
                if(moveItemStackTo(itemStack, INV_SIZE, slots.size(), false))
                {
                    return slot.getItem();
                }
            }
            // move to inventory
            else if(moveItemStackTo(itemStack, 0, INV_SIZE, false))
            {
                return slot.getItem();
            }
            
            slot.set(original);
        }
        
        return ItemStack.EMPTY;
    }
    
    @Override
    public void removed(Player pPlayer)
    {
        attemptSave();
        super.removed(pPlayer);
    }
    
    public static void openGui(Player player, int itemHandlerSize, MenuProvider p)
    {
        NetworkHooks.openScreen((ServerPlayer) player, p, (extraData) ->
        {
            extraData.writeInt(itemHandlerSize);
        });
    }
}
