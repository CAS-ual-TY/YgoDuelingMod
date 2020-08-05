package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class DeckBoxItem extends Item implements INamedContainerProvider
{
    public DeckBoxItem(Properties properties)
    {
        super(properties);
    }
    
    public IItemHandler getItemHandler(ItemStack itemStack)
    {
        return itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(YdmUtil.throwNullCapabilityException());
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = this.getActiveDeckBox(player);
        
        if(player.getHeldItem(hand) == stack)
        {
            player.openContainer(this);
            return ActionResult.resultSuccess(stack);
        }
        
        return super.onItemRightClick(world, player, hand);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player)
    {
        return new DeckBoxContainer(YdmContainerTypes.DECK_BOX, id, playerInv, this.getActiveDeckBox(player));
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".deck_box");
    }
    
    public IDeckHolder getDeckHolder(ItemStack itemStack)
    {
        return new ItemHandlerDeckHolder(this.getItemHandler(itemStack));
    }
    
    public ItemStack getActiveDeckBox(PlayerEntity player)
    {
        if(player.getHeldItemMainhand().getItem() == this)
        {
            return player.getHeldItemMainhand();
        }
        else if(player.getHeldItemOffhand().getItem() == this)
        {
            return player.getHeldItemOffhand();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }
}
