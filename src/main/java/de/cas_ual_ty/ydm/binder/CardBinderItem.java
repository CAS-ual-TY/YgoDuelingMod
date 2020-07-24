package de.cas_ual_ty.ydm.binder;

import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CardBinderItem extends Item
{
    public CardBinderItem(Properties properties)
    {
        super(properties);
    }
    
    public BinderCardInventoryManager getInventoryManager(ItemStack itemStack)
    {
        return itemStack.getCapability(YDM.BINDER_INVENTORY_CAPABILITY).orElseThrow(YdmUtil.throwNullCapabilityException());
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        if(stack.getCapability(YDM.BINDER_INVENTORY_CAPABILITY).isPresent())
        {
            if(YDM.showBinderId)
            {
                tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".uuid"));
                tooltip.add(new StringTextComponent(this.getInventoryManager(stack).getUUID().toString()));
            }
        }
        else
        {
            tooltip.add(new StringTextComponent("Error! No capability present! Pls report to mod author!"));
        }
    }
}
