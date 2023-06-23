package de.cas_ual_ty.ydm.cardbinder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardinventory.CardInventory;
import de.cas_ual_ty.ydm.cardinventory.UUIDCardsManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CardBinderContainer extends AbstractContainerMenu
{
    protected final UUIDCardsManager manager;
    protected Player player;
    protected ItemStack itemStack;
    
    protected List<CardHolder> clientList;
    protected int clientMaxPage;
    
    protected CardInventory serverList;
    
    protected boolean loaded;
    protected int page;
    
    protected Slot insertionSlot;
    
    protected Container containerInv;
    
    protected String currentSearch;
    
    public CardBinderContainer(MenuType<?> type, int id, Inventory playerInventory)
    {
        this(type, id, playerInventory, null, YdmItems.CARD_BINDER.get().getActiveBinder(playerInventory.player));
        clientList = new ArrayList<>(CardInventory.DEFAULT_CARDS_PER_PAGE);
        page = 0;
        clientMaxPage = 0;
        
        currentSearch = "";
    }
    
    public CardBinderContainer(MenuType<?> type, int id, Inventory playerInventory, UUIDCardsManager manager, ItemStack itemStack)
    {
        super(type, id);
        this.manager = manager;
        player = playerInventory.player;
        this.itemStack = itemStack;
        serverList = null;
        
        currentSearch = "";
        
        loaded = false;
        
        containerInv = new SimpleContainer(1);
        addSlot(insertionSlot = new Slot(containerInv, 0, 179, 18)
        {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return stack.getItem() == YdmItems.CARD.get() && YdmItems.CARD.get().getCardHolder(stack).getCard() != null;
            }
            
            @Override
            public void set(ItemStack stack)
            {
                if(serverList != null)
                {
                    int maxPage = serverList.getPagesAmount();
                    serverList.addCard(YdmItems.CARD.get().getCardHolder(stack), page);
                    
                    if(page == maxPage && currentSearch.isEmpty())
                    {
                        serverList.updateCardsList("");
                        updateListToClient();
                    }
                    
                    if(serverList.getPagesAmount() != maxPage)
                    {
                        updatePagesToClient();
                    }
                }
            }
        });
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }
        
        // player hot bar
        Slot s;
        for(int x = 0; x < 9; ++x)
        {
            s = new Slot(playerInventory, x, 8 + x * 18, 198);
            
            if(s.getItem() == this.itemStack)
            {
                s = new Slot(playerInventory, s.getSlotIndex(), s.x, s.y)
                {
                    @Override
                    public boolean mayPickup(Player playerIn)
                    {
                        return false;
                    }
                };
            }
            
            addSlot(s);
        }
        
        if(this.manager != null && this.manager.isInIdleState())
        {
            this.manager.load(genericCallback());
        }
    }
    
    protected void updateListToClient()
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new CardBinderMessages.UpdateList(page, serverList.getCardsForPage(page)));
    }
    
    protected void updatePagesToClient()
    {
        YDM.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new CardBinderMessages.UpdatePage(page, serverList.getPagesAmount()));
    }
    
    public void setClientList(int page, List<CardHolder> list)
    {
        if(!loaded)
        {
            loaded = true;
        }
        
        this.page = page;
        clientList = list;
    }
    
    public void setClientMaxPage(int page)
    {
        clientMaxPage = page;
    }
    
    public void setClientPage(int page)
    {
        this.page = page;
    }
    
    protected void updateHoldingItemStack(ItemStack itemStack)
    {
        player.getInventory().setPickedItem(itemStack);
    }
    
    protected CardHolder extractCard(int index)
    {
        int maxPage = serverList.getPagesAmount();
        
        CardHolder card = serverList.extractCard(page, index);
        
        updateListToClient();
        
        if(maxPage != serverList.getPagesAmount())
        {
            updatePagesToClient();
        }
        
        return card;
    }
    
    public void indexClicked(int index, boolean shiftDown)
    {
        if(!manager.isLoaded())
        {
            return;
        }
        
        CardHolder card = extractCard(index);
        
        if(card != null)
        {
            ItemStack itemStack = YdmItems.CARD.get().createItemForCardHolder(card);
            
            if(shiftDown)
            {
                player.addItem(itemStack);
            }
            else
            {
                player.getInventory().setPickedItem(itemStack);
            }
        }
    }
    
    public void indexDropped(int index)
    {
        if(!manager.isLoaded())
        {
            return;
        }
        
        CardHolder card = extractCard(index);
        
        if(card != null)
        {
            ItemStack itemStack = YdmItems.CARD.get().createItemForCardHolder(card);
            player.drop(itemStack, false);
        }
    }
    
    public void updateSearch(String search)
    {
        if(!search.equals(currentSearch))
        {
            currentSearch = search;
            updateCardsList(currentSearch);
        }
    }
    
    public void nextPage()
    {
        if(!manager.isLoaded())
        {
            return;
        }
        
        /*
        this.page %= this.serverList.getPagesAmount();
        this.page++;
        */
        
        if(page >= serverList.getPagesAmount())
        {
            page = 1;
        }
        else
        {
            page++;
        }
        
        updateListToClient();
    }
    
    public void prevPage()
    {
        if(!manager.isLoaded())
        {
            return;
        }
        
        if(page <= 1)
        {
            page = serverList.getPagesAmount();
        }
        else
        {
            page--;
        }
        
        updateListToClient();
    }
    
    public void managerFinished()
    {
        if(manager.isLoaded())
        {
            serverList = new CardInventory(manager.getList());
            updateCardsList(currentSearch);
        }
        else if(manager.isSafed())
        {
            manager.load(genericCallback());
        }
    }
    
    public void updateCardsList(String search)
    {
        try
        {
            serverList.updateCardsList(search);
            page = 1;
            updatePagesToClient();
            updateListToClient();
        }
        catch(Exception e)
        {
            player.closeContainer();
        }
    }
    
    public Runnable genericCallback()
    {
        return () ->
        {
            if(player.containerMenu instanceof CardBinderContainer)
            {
                ((CardBinderContainer) player.containerMenu).managerFinished();
            }
        };
    }
    
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        Slot slot = slots.get(index);
        
        if(slot != insertionSlot && slot.mayPickup(playerIn))
        {
            ItemStack stack = slot.getItem();
            
            if(insertionSlot.mayPlace(stack))
            {
                ItemStack split = stack.split(1);
                insertionSlot.set(split);
            }
        }
        
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }
    
    @Override
    public void removed(Player playerIn)
    {
        super.removed(playerIn);
        if(manager != null && manager.isLoaded())
        {
            manager.safe(genericCallback());
        }
    }
}
