package de.cas_ual_ty.ydm.binder;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.network.CardBinderMessages;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.cardinventory.ICardInventory;
import de.cas_ual_ty.ydm.cardinventory.JsonCardInventoryManager;
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

public class BinderContainer extends Container
{
    protected final JsonCardInventoryManager manager;
    protected PlayerEntity player;
    
    protected List<CardHolder> clientList;
    protected int clientMaxPage;
    
    protected ICardInventory serverList;
    
    protected boolean loaded;
    protected int page;
    
    protected Slot insertionSlot;
    
    protected IInventory containerInv;
    
    public BinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        this(type, id, playerInventory, null, YdmItems.CARD_BINDER.getActiveBinder(playerInventory.player));
        this.clientList = new ArrayList<>(CardInventory.DEFAULT_CARDS_PER_PAGE);
        this.page = 0;
        this.clientMaxPage = 0;
    }
    
    public BinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, JsonCardInventoryManager manager, ItemStack itemStack)
    {
        super(type, id);
        this.manager = manager;
        this.player = playerInventory.player;
        this.serverList = null;
        
        this.loaded = false;
        
        Slot s;
        
        this.containerInv = new Inventory(1);
        this.addSlot(this.insertionSlot = new Slot(this.containerInv, 0, 179, 18)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == YdmItems.CARD;
            }
            
            @Override
            public void putStack(ItemStack stack)
            {
                if(BinderContainer.this.serverList != null)
                {
                    BinderContainer.this.serverList.addCard(YdmItems.CARD.getCardHolder(stack));
                    
                    int maxPage = BinderContainer.this.serverList.getPagesAmount();
                    BinderContainer.this.updatePagesToClient();
                    
                    if(BinderContainer.this.page == maxPage)
                    {
                        BinderContainer.this.updateListToClient();
                    }
                }
            }
        });
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                s = new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 139 + y * 18);
                
                if(s.getStack() == itemStack)
                {
                    s = new Slot(playerInventory, s.slotNumber, s.xPos, s.yPos)
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
        }
        
        // player hot bar
        for(int x = 0; x < 9; ++x)
        {
            s = new Slot(playerInventory, x, 8 + x * 18, 197);
            
            if(s.getStack() == itemStack)
            {
                s = new Slot(playerInventory, s.slotNumber, s.xPos, s.yPos)
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
    
    public void indexClicked(int index)
    {
        CardHolder card = this.serverList.extractCard(this.page, index);
        
        if(card != null)
        {
            ItemStack itemStack = YdmItems.CARD.createItemForCardHolder(card);
            this.player.inventory.setItemStack(itemStack);
        }
        
        this.updateListToClient();
    }
    
    public void nextPage()
    {
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
            if(this.player.openContainer instanceof BinderContainer)
            {
                ((BinderContainer)this.player.openContainer).managerFinished();
            }
        };
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
