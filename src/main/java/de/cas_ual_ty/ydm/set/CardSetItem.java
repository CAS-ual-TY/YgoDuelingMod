package de.cas_ual_ty.ydm.set;

import java.util.List;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class CardSetItem extends Item
{
    public CardSetItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void addInformation(ItemStack itemStack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        CardSet set = this.getCardSet(itemStack);
        tooltip.clear();
        set.addInformation(tooltip);
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
    
    public ItemStack createItemForSet(CardSet set)
    {
        ItemStack itemStack = new ItemStack(this);
        this.setCardSet(itemStack, set);
        return itemStack;
    }
    
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        if(!this.isInGroup(group))
        {
            return;
        }
        
        for(CardSet set : YdmDatabase.SETS_LIST)
        {
            if(set.isIndependentAndItem())
            {
                items.add(this.createItemForSet(set));
            }
        }
    }
    
    @Override
    public boolean shouldSyncTag()
    {
        return true;
    }
}