package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class CardSetItem extends CardSetItemBase
{
    public CardSetItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = CardSetItem.getActiveSet(player);
        
        if(!world.isRemote && player.getHeldItem(hand) == stack)
        {
            this.unseal(stack, player, hand);
            return player.getHeldItem(hand).getItem().onItemRightClick(world, player, hand);
        }
        
        return super.onItemRightClick(world, player, hand);
    }
    
    public void unseal(ItemStack itemStack, PlayerEntity player, Hand hand)
    {
        ItemStack newStack = YdmItems.OPENED_SET.createItemForSet(this.getCardSet(itemStack));
        player.setHeldItem(hand, newStack);
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
    
    public static ItemStack getActiveSet(PlayerEntity player)
    {
        if(player.getHeldItemMainhand().getItem() == YdmItems.SET)
        {
            return player.getHeldItemMainhand();
        }
        else if(player.getHeldItemOffhand().getItem() == YdmItems.SET)
        {
            return player.getHeldItemOffhand();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }
}
