package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesItem;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class DeckBoxItem extends Item implements MenuProvider
{
    public static final String ITEM_HANDLER_KEY = "cards";
    public static final String CARD_SLEEVES_KEY = "sleeves";
    
    public DeckBoxItem(Properties properties)
    {
        super(properties);
    }
    
    public YDMItemHandler getItemHandler(ItemStack itemStack)
    {
        return itemStack.getCapability(YDM.CARD_ITEM_INVENTORY).orElse(null);
    }
    
    public ItemStack getCardSleeves(ItemStack itemStack)
    {
        if(itemStack.getOrCreateTag().contains(DeckBoxItem.CARD_SLEEVES_KEY))
        {
            return ItemStack.of(itemStack.getOrCreateTag().getCompound(DeckBoxItem.CARD_SLEEVES_KEY));
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }
    
    public void saveItemHandlerToNBT(ItemStack itemStack, YDMItemHandler itemHandler)
    {
        //FIXME probably not needed
        //itemStack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(ih -> ih.deserializeNBT(itemHandler.serializeNBT()));
    }
    
    public void saveCardSleevesToNBT(ItemStack itemStack, ItemStack sleevesStack)
    {
        if(sleevesStack.getItem() instanceof CardSleevesItem && !((CardSleevesItem) sleevesStack.getItem()).sleeves.isCardBack())
        {
            itemStack.getOrCreateTag().put(DeckBoxItem.CARD_SLEEVES_KEY, sleevesStack.save(new CompoundTag()));
        }
        else
        {
            itemStack.getOrCreateTag().put(DeckBoxItem.CARD_SLEEVES_KEY, ItemStack.EMPTY.save(new CompoundTag()));
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        ItemStack stack = DeckBoxItem.getActiveDeckBox(player);
        
        if(player.getItemInHand(hand) == stack)
        {
            player.openMenu(this);
            return InteractionResultHolder.success(stack);
        }
        
        return super.use(world, player, hand);
    }
    
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player)
    {
        return new DeckBoxContainer(YdmContainerTypes.DECK_BOX.get(), id, playerInv, DeckBoxItem.getActiveDeckBox(player));
    }
    
    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container." + YDM.MOD_ID + ".deck_box");
    }
    
    public ItemHandlerDeckHolder getDeckHolder(ItemStack itemStack)
    {
        return new ItemHandlerDeckHolder(getItemHandler(itemStack), getCardSleeves(itemStack));
    }
    
    public void setDeckHolder(ItemStack itemStack, DeckHolder holder)
    {
        YDMItemHandler itemHandler = itemStack.getCapability(YDM.CARD_ITEM_INVENTORY).orElse(null);
        
        CardHolder c;
        
        for(int i = 0; i < holder.getMainDeckSize(); ++i)
        {
            c = holder.getMainDeck().get(i);
            
            if(c != null)
            {
                itemHandler.insertItem(DeckHolder.MAIN_DECK_INDEX_START + i, YdmItems.CARD.get().createItemForCardHolder(c), false);
            }
        }
        
        for(int i = holder.getMainDeckSize(); i < DeckHolder.MAIN_DECK_SIZE; ++i)
        {
            itemHandler.insertItem(DeckHolder.MAIN_DECK_INDEX_START + i, ItemStack.EMPTY, false);
        }
        
        for(int i = 0; i < holder.getExtraDeckSize(); ++i)
        {
            c = holder.getExtraDeck().get(i);
            
            if(c != null)
            {
                itemHandler.insertItem(DeckHolder.EXTRA_DECK_INDEX_START + i, YdmItems.CARD.get().createItemForCardHolder(c), false);
            }
        }
        
        for(int i = holder.getExtraDeckSize(); i < DeckHolder.EXTRA_DECK_SIZE; ++i)
        {
            itemHandler.insertItem(DeckHolder.EXTRA_DECK_INDEX_START + i, ItemStack.EMPTY, false);
        }
        
        for(int i = 0; i < holder.getSideDeckSize(); ++i)
        {
            c = holder.getSideDeck().get(i);
            
            if(c != null)
            {
                itemHandler.insertItem(DeckHolder.SIDE_DECK_INDEX_START + i, YdmItems.CARD.get().createItemForCardHolder(c), false);
            }
        }
        
        for(int i = holder.getSideDeckSize(); i < DeckHolder.SIDE_DECK_SIZE; ++i)
        {
            itemHandler.insertItem(DeckHolder.SIDE_DECK_INDEX_START + i, ItemStack.EMPTY, false);
        }
        
        saveItemHandlerToNBT(itemStack, itemHandler);
        
        if(!holder.getSleeves().isCardBack())
        {
            saveCardSleevesToNBT(itemStack, new ItemStack(holder.getSleeves().getItem()));
        }
    }
    
    public static ItemStack getActiveDeckBox(Player player)
    {
        if(player.getMainHandItem().getItem() instanceof DeckBoxItem)
        {
            return player.getMainHandItem();
        }
        else if(player.getOffhandItem().getItem() instanceof DeckBoxItem)
        {
            return player.getOffhandItem();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }
    
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null && nbt.contains("card_item_inventory", Tag.TAG_COMPOUND))
        {
            stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
            {
                handler.deserializeNBT(nbt.getCompound("card_item_inventory"));
            });
        }
    }
    
    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack)
    {
        CompoundTag nbt = super.getShareTag(stack);
        
        if(nbt == null)
        {
            nbt = new CompoundTag();
        }
        
        CompoundTag finalNBT = nbt;
        
        stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
        {
            finalNBT.put("card_item_inventory", handler.serializeNBT());
        });
        
        return finalNBT;
    }
}
