package de.cas_ual_ty.ydm.card;

import java.util.List;

import de.cas_ual_ty.ydm.capability.CardHolderProvider;
import de.cas_ual_ty.ydm.capability.ICardHolder;
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
        super.addInformation(itemStack, worldIn, tooltip, flagIn);
        ICardHolder holder = this.getCardHolder(itemStack);
        tooltip.add(new StringTextComponent(holder.getCard().getSetId()));
        tooltip.add(new StringTextComponent(holder.getActiveRarity().name));
    }
    
    public ICardHolder getCardHolder(ItemStack itemStack)
    {
        return itemStack.getCapability(CardHolderProvider.CAPABILITY_CARD_HOLDER).orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));
    }
    
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        
    }
}
