package de.cas_ual_ty.ydm.card;

import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.capability.CardHolderProvider;
import de.cas_ual_ty.ydm.capability.ICardHolder;
import de.cas_ual_ty.ydm.util.Database;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

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
        
        LazyOptional<ICardHolder> cap = this.getCardHolderOptional(itemStack);
        
        cap.ifPresent((holder) ->
        {
            tooltip.add(new StringTextComponent(holder.getCard().getProperties().getName()));
            tooltip.add(new StringTextComponent(holder.getCard().getSetId()));
            tooltip.add(new StringTextComponent(holder.getActiveRarity().name));
        });
    }
    
    public ICardHolder getCardHolder(ItemStack itemStack)
    {
        return this.getCardHolderOptional(itemStack).orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!"));
    }
    
    public LazyOptional<ICardHolder> getCardHolderOptional(ItemStack itemStack)
    {
        return itemStack.getCapability(CardHolderProvider.CAPABILITY_CARD_HOLDER);
    }
    
    public ItemStack createItemForCard(Card card)
    {
        ItemStack itemStack = new ItemStack(this);
        this.getCardHolder(itemStack).setCard(card);
        return itemStack;
    }
    
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        YDM.log("Creating card item variants");
        
        ItemStack itemStack;
        LazyOptional<ICardHolder> cap;
        ICardHolder holder;
        
        for(Card card : Database.CARDS_LIST)
        {
            // not using ::createItemForCard to conserve the memory of the method calls
            itemStack = new ItemStack(this);
            cap = this.getCardHolderOptional(itemStack);
            
            if(cap.isPresent())
            {
                holder = cap.orElse(null);
                holder.setCard(card);
                items.add(itemStack);
            }
            else
            {
                break;
            }
        }
    }
}
