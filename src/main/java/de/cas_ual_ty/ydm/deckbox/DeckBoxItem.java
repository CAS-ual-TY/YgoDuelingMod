package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.CardSleevesItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class DeckBoxItem extends Item implements INamedContainerProvider
{
    public static final String ITEM_HANDLER_KEY = "cards";
    public static final String CARD_SLEEVES_KEY = "sleeves";
    
    public DeckBoxItem(Properties properties)
    {
        super(properties);
    }
    
    public IItemHandler getItemHandler(ItemStack itemStack)
    {
        IItemHandler itemHandler = new ItemStackHandler(DeckHolder.TOTAL_DECK_SIZE);
        ListNBT nbt = itemStack.getOrCreateTag().getList(DeckBoxItem.ITEM_HANDLER_KEY, Constants.NBT.TAG_COMPOUND);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemHandler, null, nbt);
        return itemHandler;
        
        //        return itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(YdmUtil.throwNullCapabilityException());
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
    
    public void saveItemHandlerToNBT(ItemStack itemStack, IItemHandler itemHandler)
    {
        itemStack.getOrCreateTag().put(DeckBoxItem.ITEM_HANDLER_KEY,
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemHandler, null));
    }
    
    public void saveCardSleevesToNBT(ItemStack itemStack, ItemStack sleevesStack)
    {
        if(sleevesStack.getItem() instanceof CardSleevesItem && !((CardSleevesItem) sleevesStack.getItem()).sleeves.isCardBack())
        {
            itemStack.getOrCreateTag().put(DeckBoxItem.CARD_SLEEVES_KEY, sleevesStack.save(new CompoundNBT()));
        }
        else
        {
            itemStack.getOrCreateTag().put(DeckBoxItem.CARD_SLEEVES_KEY, ItemStack.EMPTY.save(new CompoundNBT()));
        }
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = DeckBoxItem.getActiveDeckBox(player);
        
        if(player.getItemInHand(hand) == stack)
        {
            player.openMenu(this);
            return ActionResult.success(stack);
        }
        
        return super.use(world, player, hand);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player)
    {
        return new DeckBoxContainer(YdmContainerTypes.DECK_BOX, id, playerInv, DeckBoxItem.getActiveDeckBox(player));
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".deck_box");
    }
    
    public ItemHandlerDeckHolder getDeckHolder(ItemStack itemStack)
    {
        return new ItemHandlerDeckHolder(getItemHandler(itemStack), getCardSleeves(itemStack));
    }
    
    public void setDeckHolder(ItemStack itemStack, DeckHolder holder)
    {
        IItemHandler itemHandler = new ItemStackHandler(DeckHolder.TOTAL_DECK_SIZE);
        
        CardHolder c;
        
        for(int i = 0; i < holder.getMainDeckSize(); ++i)
        {
            c = holder.getMainDeck().get(i);
            
            if(c != null)
            {
                itemHandler.insertItem(DeckHolder.MAIN_DECK_INDEX_START + i, YdmItems.CARD.createItemForCardHolder(c), false);
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
                itemHandler.insertItem(DeckHolder.EXTRA_DECK_INDEX_START + i, YdmItems.CARD.createItemForCardHolder(c), false);
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
                itemHandler.insertItem(DeckHolder.SIDE_DECK_INDEX_START + i, YdmItems.CARD.createItemForCardHolder(c), false);
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
    
    public static ItemStack getActiveDeckBox(PlayerEntity player)
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
}
