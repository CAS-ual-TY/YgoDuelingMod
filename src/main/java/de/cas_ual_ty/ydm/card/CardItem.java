package de.cas_ual_ty.ydm.card;

import de.cas_ual_ty.ydm.YdmDatabase;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class CardItem extends Item
{
    public CardItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        CardHolder holder = getCardHolder(itemStack);
        tooltip.clear();
        holder.addInformation(tooltip);
    }
    
    @Override
    public Component getName(ItemStack itemStack)
    {
        CardHolder holder = getCardHolder(itemStack);
        return Component.literal(holder.getCard().getName());
    }
    
    public CardHolder getCardHolder(ItemStack itemStack)
    {
        return new ItemStackCardHolder(itemStack);
    }
    
    public ItemStack createItemForCard(de.cas_ual_ty.ydm.card.properties.Properties card, byte imageIndex, String rarity, String code)
    {
        ItemStack itemStack = new ItemStack(this);
        getCardHolder(itemStack).override(new CardHolder(card, imageIndex, rarity, code));
        return itemStack;
    }
    
    public ItemStack createItemForCard(de.cas_ual_ty.ydm.card.properties.Properties card, byte imageIndex, String rarity)
    {
        ItemStack itemStack = new ItemStack(this);
        getCardHolder(itemStack).override(new CardHolder(card, imageIndex, rarity));
        return itemStack;
    }
    
    public ItemStack createItemForCard(de.cas_ual_ty.ydm.card.properties.Properties card)
    {
        return createItemForCard(card, (byte) 0, Rarity.CREATIVE.name);
    }
    
    public ItemStack createItemForCardHolder(CardHolder card)
    {
        ItemStack itemStack = new ItemStack(this);
        getCardHolder(itemStack).override(card);
        return itemStack;
    }
    
    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
    {
        if(!allowedIn(group))
        {
            return;
        }
        
        YdmDatabase.forAllCardVariants((card, imageIndex) ->
        {
            items.add(createItemForCard(card, imageIndex, Rarity.CREATIVE.name));
        });
    }
    
    @Override
    public boolean shouldOverrideMultiplayerNbt()
    {
        return true;
    }
}
