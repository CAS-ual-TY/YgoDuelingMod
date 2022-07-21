package de.cas_ual_ty.ydm.card;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class CardSleevesItem extends Item
{
    public final CardSleevesType sleeves;
    
    public CardSleevesItem(Properties properties, CardSleevesType sleeves)
    {
        super(properties);
        this.sleeves = sleeves;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        
        if(sleeves.isPatreonReward)
        {
            tooltip.add(new StringTextComponent(sleeves.patronName + "'s Patreon Sleeves"));
        }
    }
    
    @Override
    public Rarity getRarity(ItemStack stack)
    {
        return sleeves.isPatreonReward ? Rarity.RARE : Rarity.COMMON;
    }
}
