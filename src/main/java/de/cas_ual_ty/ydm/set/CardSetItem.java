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

public class CardSetItem extends CardSetBaseItem
{
    public CardSetItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = CardSetItem.getActiveSet(player);
        
        if(player.getItemInHand(hand) == stack)
        {
            unseal(stack, player, hand);
            
            if(!world.isClientSide)
            {
                return player.getItemInHand(hand).getItem().use(world, player, hand);
            }
        }
        
        return super.use(world, player, hand);
    }
    
    public void unseal(ItemStack itemStack, PlayerEntity player, Hand hand)
    {
        ItemStack newStack = YdmItems.OPENED_SET.createItemForSet(getCardSet(itemStack));
        player.setItemInHand(hand, newStack);
        
        if(itemStack.getCount() > 1)
        {
            itemStack.shrink(1);
            
            if(!player.level.isClientSide)
            {
                player.inventory.placeItemBackInInventory(player.level, itemStack);
            }
        }
    }
    
    public ItemStack createItemForSet(CardSet set)
    {
        ItemStack itemStack = new ItemStack(this);
        setCardSet(itemStack, set);
        return itemStack;
    }
    
    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
    {
        if(!allowdedIn(group))
        {
            return;
        }
        
        for(CardSet set : YdmDatabase.SETS_LIST)
        {
            if(set.isIndependentAndItem())
            {
                items.add(createItemForSet(set));
            }
        }
    }
    
    public static ItemStack getActiveSet(PlayerEntity player)
    {
        if(player.getMainHandItem().getItem() == YdmItems.SET)
        {
            return player.getMainHandItem();
        }
        else if(player.getOffhandItem().getItem() == YdmItems.SET)
        {
            return player.getOffhandItem();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }
}
