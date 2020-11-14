package de.cas_ual_ty.ydm.cardbinder;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.cardinventory.UUIDCardsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;

public class CardBinderContainer extends Container
{
    protected final UUIDCardsManager manager;
    protected PlayerEntity player;
    protected ItemStack itemStack;
    
    protected List<CardHolder> clientList;
    protected int clientMaxPage;
    
    protected CardInventory serverList;
    
    protected boolean loaded;
    protected int page;
    
    protected Slot insertionSlot;
    
    protected IInventory containerInv;
    
    public CardBinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        this(type, id, playerInventory, null, YdmItems.CARD_BINDER.getActiveBinder(playerInventory.player));
        this.clientList = new ArrayList<>(CardInventory.DEFAULT_CARDS_PER_PAGE);
        this.page = 0;
        this.clientMaxPage = 0;
    }
    
    public CardBinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, UUIDCardsManager manager, ItemStack itemStack)
    {
        super(type, id);
        this.manager = manager;
        this.player = playerInventory.player;
        this.itemStack = itemStack;
        this.serverList = null;
        
        this.loaded = false;
        
        this.containerInv = new Inventory(1);
        this.addSlot(this.insertionSlot = new Slot(this.containerInv, 0, 179, 18)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == YdmItems.CARD && YdmItems.CARD.getCardHolder(stack).getCard() != null;
            }
            
            @Override
            public void putStack(ItemStack stack)
            {
                if(CardBinderContainer.this.serverList != null)
                {
                    int maxPage = CardBinderContainer.this.serverList.getPagesAmount();
                    CardBinderContainer.this.serverList.addCard(YdmItems.CARD.getCardHolder(stack));
                    
                    if(CardBinderContainer.this.page == maxPage)
                    {
                        CardBinderContainer.this.updateListToClient();
                    }
                    
                    if(CardBinderContainer.this.serverList.getPagesAmount() != maxPage)
                    {
                        CardBinderContainer.this.updatePagesToClient();
                    }
                }
            }
        });
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }
        
        // player hot bar
        Slot s;
        for(int x = 0; x < 9; ++x)
        {
            s = new Slot(playerInventory, x, 8 + x * 18, 198);
            
            if(s.getStack() == this.itemStack)
            {
                s = new Slot(playerInventory, s.getSlotIndex(), s.xPos, s.yPos)
                {
                    @Override
                    public boolean canTakeStack(PlayerEntity playerIn)
                    {
                        return false;
                    }
                };
            }
            
            this.addSlot(s);
        }
        
        if(this.manager != null && this.manager.isInIdleState())
        {
            this.manager.load(this.genericCallback());
        }
    }
    
    protected void updateListToClient()
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)this.player), new CardBinderMessages.UpdateList(this.page, this.serverList.getCardsForPage(this.page)));
    }
    
    protected void updatePagesToClient()
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)this.player), new CardBinderMessages.UpdatePage(this.page, this.serverList.getPagesAmount()));
    }
    
    public void setClientList(int page, List<CardHolder> list)
    {
        if(!this.loaded)
        {
            this.loaded = true;
        }
        
        this.page = page;
        this.clientList = list;
    }
    
    public void setClientMaxPage(int page)
    {
        this.clientMaxPage = page;
    }
    
    public void setClientPage(int page)
    {
        this.page = page;
    }
    
    protected void updateHoldingItemStack(ItemStack itemStack)
    {
        this.player.inventory.setItemStack(itemStack);
    }
    
    protected CardHolder extractCard(int index)
    {
        int maxPage = CardBinderContainer.this.serverList.getPagesAmount();
        
        CardHolder card = this.serverList.extractCard(this.page, index);
        
        this.updateListToClient();
        
        if(maxPage != this.serverList.getPagesAmount())
        {
            this.updatePagesToClient();
        }
        
        return card;
    }
    
    public void indexClicked(int index, boolean shiftDown)
    {
        if(!this.manager.isLoaded())
        {
            return;
        }
        
        CardHolder card = this.extractCard(index);
        
        if(card != null)
        {
            ItemStack itemStack = YdmItems.CARD.createItemForCardHolder(card);
            
            if(shiftDown)
            {
                this.player.addItemStackToInventory(itemStack);
            }
            else
            {
                this.player.inventory.setItemStack(itemStack);
            }
        }
    }
    
    public void indexDropped(int index)
    {
        if(!this.manager.isLoaded())
        {
            return;
        }
        
        CardHolder card = this.extractCard(index);
        
        if(card != null)
        {
            ItemStack itemStack = YdmItems.CARD.createItemForCardHolder(card);
            this.player.dropItem(itemStack, false);
        }
    }
    
    public void nextPage()
    {
        if(!this.manager.isLoaded())
        {
            return;
        }
        
        /*
        this.page %= this.serverList.getPagesAmount();
        this.page++;
        */
        
        if(this.page >= this.serverList.getPagesAmount())
        {
            this.page = 1;
        }
        else
        {
            this.page++;
        }
        
        this.updateListToClient();
    }
    
    public void prevPage()
    {
        if(!this.manager.isLoaded())
        {
            return;
        }
        
        if(this.page <= 1)
        {
            this.page = this.serverList.getPagesAmount();
        }
        else
        {
            this.page--;
        }
        
        this.updateListToClient();
    }
    
    public void managerFinished()
    {
        if(this.manager.isLoaded())
        {
            this.serverList = new CardInventory(this.manager.getList());
            this.updateCardsList("");
            this.updatePagesToClient();
            this.updateListToClient();
        }
        else if(this.manager.isSafed())
        {
            this.manager.load(this.genericCallback());
        }
    }
    
    public void updateCardsList(String search)
    {
        this.serverList.updateCardsList(search);
        this.page = 1;
        this.updateListToClient();
    }
    
    public Runnable genericCallback()
    {
        return () ->
        {
            if(this.player.openContainer instanceof CardBinderContainer)
            {
                ((CardBinderContainer)this.player.openContainer).managerFinished();
            }
        };
    }
    
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        Slot slot = this.inventorySlots.get(index);
        
        if(slot != this.insertionSlot && slot.canTakeStack(playerIn))
        {
            ItemStack stack = slot.getStack();
            
            if(this.insertionSlot.isItemValid(stack))
            {
                slot.putStack(ItemStack.EMPTY);
                this.insertionSlot.putStack(stack);
            }
        }
        
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
    
    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
        if(this.manager != null && this.manager.isLoaded())
        {
            this.manager.safe(this.genericCallback());
        }
    }
}
