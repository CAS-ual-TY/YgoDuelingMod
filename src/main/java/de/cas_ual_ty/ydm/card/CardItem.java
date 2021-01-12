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
        tooltip.clear();
        holder.addInformation(tooltip);
    }
    
    @Override
    public ITextComponent getDisplayName(ItemStack itemStack)
    {
        CardHolder holder = this.getCardHolder(itemStack);
        return new StringTextComponent(holder.getCard().getName());
    }
    
    public CardHolder getCardHolder(ItemStack itemStack)
    {
        return new ItemStackCardHolder(itemStack);
    }
    
    public ItemStack createItemForCard(de.cas_ual_ty.ydm.card.properties.Properties card, byte imageIndex, String rarity, String code)
    {
        ItemStack itemStack = new ItemStack(this);
        this.getCardHolder(itemStack).override(new CardHolder(card, imageIndex, rarity, code));
        return itemStack;
    }
    
    public ItemStack createItemForCard(de.cas_ual_ty.ydm.card.properties.Properties card, byte imageIndex, String rarity)
    {
        ItemStack itemStack = new ItemStack(this);
        this.getCardHolder(itemStack).override(new CardHolder(card, imageIndex, rarity));
        return itemStack;
    }
    
    public ItemStack createItemForCard(de.cas_ual_ty.ydm.card.properties.Properties card)
    {
        return this.createItemForCard(card, (byte)0, Rarity.CREATIVE.name);
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
        
        YdmDatabase.forAllCardVariants((card, imageIndex) ->
        {
            items.add(this.createItemForCard(card, imageIndex, Rarity.CREATIVE.name));
        });
    }
    
    @Override
    public boolean shouldSyncTag()
    {
        return true;
    }
}
