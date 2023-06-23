package de.cas_ual_ty.ydm.card;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        
        if(sleeves.isPatreonReward)
        {
            tooltip.add(Component.literal(sleeves.patronName + "'s Patreon Sleeves"));
        }
    }
    
    @Override
    public Rarity getRarity(ItemStack stack)
    {
        return sleeves.isPatreonReward ? Rarity.RARE : Rarity.COMMON;
    }
}
