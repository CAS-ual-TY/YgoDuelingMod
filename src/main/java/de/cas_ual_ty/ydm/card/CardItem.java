package de.cas_ual_ty.ydm.card;

import java.util.List;

import de.cas_ual_ty.ydm.YdmDatabase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class CardItem extends Item
{
    public CardItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void addInformation(ItemStack itemStack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        CardHolder holder = this.getCardHolder(itemStack);
        
        if(holder.getCard() == null)
        {
            super.addInformation(itemStack, worldIn, tooltip, flagIn);
        }
        else
        {
            tooltip.clear();
            holder.addInformation(tooltip);
        }
    }
    
    @Override
    public ITextComponent getDisplayName(ItemStack itemStack)
    {
        CardHolder holder = this.getCardHolder(itemStack);
        
        if(holder.getCard() == null)
        {
            return super.getDisplayName(itemStack);
        }
        else
        {
            return new StringTextComponent(holder.getProperties().getName());
        }
    }
    
    public CardHolder getCardHolder(ItemStack itemStack)
    {
        return new ItemStackCardHolder(itemStack);
    }
    
    public ItemStack createItemForCard(Card card)
    {
        ItemStack itemStack = new ItemStack(this);
        this.getCardHolder(itemStack).setCard(card);
        return itemStack;
    }
    
    public ItemStack createItemForCardHolder(CardHolder card)
    {
        ItemStack itemStack = new ItemStack(this);
        this.getCardHolder(itemStack).override(card);
        return itemStack;
    }
    
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if(!this.isInGroup(group))
        {
            return;
        }
        
        //        YDM.log("Creating card item variants (" + YdmDatabase.CARDS_LIST.size() + " different variants)");
        
        ItemStack itemStack;
        CardHolder holder;
        
        for(Card card : YdmDatabase.CARDS_LIST)
        {
            if(card == CustomCards.DUMMY_CARD)
            {
                continue;
            }
            
            // not using ::createItemForCard to conserve the memory of the method calls
            itemStack = new ItemStack(this);
            holder = this.getCardHolder(itemStack);
            holder.setCard(card);
            items.add(itemStack);
        }
    }
    
    @Override
    public boolean shouldSyncTag()
    {
        return true;
    }
}
