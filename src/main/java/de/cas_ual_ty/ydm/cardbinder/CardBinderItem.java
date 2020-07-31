package de.cas_ual_ty.ydm.cardbinder;

import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class CardBinderItem extends Item implements INamedContainerProvider
{
    public CardBinderItem(Properties properties)
    {
        super(properties);
    }
    
    public CardBinderCardsManager getInventoryManager(ItemStack itemStack)
    {
        return itemStack.getCapability(YDM.BINDER_INVENTORY_CAPABILITY).orElseThrow(YdmUtil.throwNullCapabilityException());
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        LazyOptional<CardBinderCardsManager> l = stack.getCapability(YDM.BINDER_INVENTORY_CAPABILITY);
        
        if(l.isPresent())
        {
            CardBinderCardsManager m = l.orElse(null);
            
            if(YDM.showBinderId)
            {
                tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".uuid"));
                
                if(m.getUUID() != null)
                {
                    tooltip.add(new StringTextComponent(m.getUUID().toString()));
                }
                else
                {
                    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".uuid.empty"));
                }
            }
        }
        else
        {
            tooltip.add(new StringTextComponent("Error! No capability present! Pls report to mod author!"));
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = this.getActiveBinder(player);
        
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
        ItemStack s = this.getActiveBinder(player);
        return new CardBinderContainer(YdmContainerTypes.CARD_BINDER, id, playerInv, this.getInventoryManager(s), s);
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".card_binder");
    }
    
    public ItemStack getActiveBinder(PlayerEntity player)
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
