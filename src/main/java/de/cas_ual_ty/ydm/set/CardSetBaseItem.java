package de.cas_ual_ty.ydm.set;

import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import de.cas_ual_ty.ydm.util.JsonKeys;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class CardSetBaseItem extends Item
{
    public CardSetBaseItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void addInformation(ItemStack itemStack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        CardSet set = this.getCardSet(itemStack);
        tooltip.clear();
        set.addItemInformation(tooltip);
    }
    
    @Override
    public ITextComponent getDisplayName(ItemStack itemStack)
    {
        CardSet set = this.getCardSet(itemStack);
        return new StringTextComponent(set.name);
    }
    
    public CardSet getCardSet(ItemStack itemStack)
    {
        String code = this.getNBT(itemStack).getString(JsonKeys.CODE);
        
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
        this.getNBT(itemStack).putString(JsonKeys.CODE, set.code);
    }
    
    public CompoundNBT getNBT(ItemStack itemStack)
    {
        return itemStack.getOrCreateTag();
    }
    
    @Override
    public abstract void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items);
    
    @Override
    public boolean shouldSyncTag()
    {
        return true;
    }
    
    public void viewSetContents(World world, PlayerEntity player, ItemStack itemStack)
    {
        if(!world.isRemote)
        {
            CardSet set = this.getCardSet(itemStack);
            List<CardHolder> cards = set.getAllCardEntries();
            CIIContainer.openGui(player, cards.size(), new INamedContainerProvider()
            {
                
                @Override
                public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity p_createMenu_3_)
                {
                    IItemHandler itemHandler = new ItemStackHandler(cards.size());
                    
                    for(int i = 0; i < cards.size(); ++i)
                    {
                        itemHandler.insertItem(i, YdmItems.CARD.createItemForCardHolder(cards.get(i)), false);
                    }
                    
                    return new CardSetContentsContainer(YdmContainerTypes.CARD_SET_CONTENTS, id, playerInv, itemHandler);
                }
                
                @Override
                public ITextComponent getDisplayName()
                {
                    return new TranslationTextComponent("container." + YDM.MOD_ID + ".card_set_contents");
                }
            });
        }
    }
    
    public static Hand getActiveSetItem(PlayerEntity player)
    {
        return YdmUtil.getActiveItem(player, (i) -> (i.getItem() instanceof CardSetBaseItem));
    }
}
