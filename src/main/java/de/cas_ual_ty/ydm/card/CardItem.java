package de.cas_ual_ty.ydm.card;

import java.util.List;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.YDM;
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
            return;
        }
        
        tooltip.add(new StringTextComponent(holder.getCard().getProperties().getName()));
        tooltip.add(new StringTextComponent(holder.getCard().getSetId()));
        tooltip.add(new StringTextComponent(holder.getActiveRarity().name));
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
        if(group != YDM.ydmItemGroup)
        {
            super.fillItemGroup(group, items);
            return;
        }
        
        YDM.log("Creating card item variants (" + Database.CARDS_LIST.size() + " different variants)");
        
        ItemStack itemStack;
        CardHolder holder;
        
        for(Card card : Database.CARDS_LIST)
        {
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
