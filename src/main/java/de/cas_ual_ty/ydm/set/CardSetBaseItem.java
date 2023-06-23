package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import de.cas_ual_ty.ydm.util.JsonKeys;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.SortedArraySet;


import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public abstract class CardSetBaseItem extends Item
{
    public CardSetBaseItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        CardSet set = getCardSet(itemStack);
        tooltip.clear();
        set.addItemInformation(tooltip);
    }
    
    @Override
    public Component getName(ItemStack itemStack)
    {
        CardSet set = getCardSet(itemStack);
        return Component.literal(set.name);
    }
    
    public CardSet getCardSet(ItemStack itemStack)
    {
        String code = getNBT(itemStack).getString(JsonKeys.CODE);
        
        if(code.isEmpty())
        {
            return CardSet.DUMMY;
        }
        
        CardSet set = YdmDatabase.SETS_LIST.get(code);
        
        if(set == null)
        {
            set = CardSet.DUMMY;
        }
        
        return set;
    }
    
    public void setCardSet(ItemStack itemStack, CardSet set)
    {
        getNBT(itemStack).putString(JsonKeys.CODE, set.code);
    }
    
    public CompoundTag getNBT(ItemStack itemStack)
    {
        return itemStack.getOrCreateTag();
    }
    
    @Override
    public abstract void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items);
    
    @Override
    public boolean shouldOverrideMultiplayerNbt()
    {
        return true;
    }
    
    public void viewSetContents(Level world, Player player, ItemStack itemStack)
    {
        if(!world.isClientSide)
        {
            CardSet set = getCardSet(itemStack);
            SortedArraySet<CardHolder> cardsSet = set.getAllCardEntries();
            CardHolder[] cards = cardsSet.toArray(new CardHolder[0]);
            
            CIIContainer.openGui(player, cards.length, new MenuProvider()
            {
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player p_createMenu_3_)
                {
                    IItemHandler itemHandler = new ItemStackHandler(cards.length);
                    
                    for(int i = 0; i < cards.length; ++i)
                    {
                        itemHandler.insertItem(i, YdmItems.CARD.get().createItemForCardHolder(cards[i]), false);
                    }
                    
                    return new CardSetContentsContainer(YdmContainerTypes.CARD_SET_CONTENTS.get(), id, playerInv, itemHandler);
                }
                
                @Override
                public Component getDisplayName()
                {
                    return Component.translatable("container." + YDM.MOD_ID + ".card_set_contents");
                }
            });
        }
    }
    
    public static InteractionHand getActiveSetItem(Player player)
    {
        return YdmUtil.getActiveItem(player, (i) -> (i.getItem() instanceof CardSetBaseItem));
    }
}
