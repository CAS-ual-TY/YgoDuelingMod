package de.cas_ual_ty.ydm.card;

import de.cas_ual_ty.ydm.card.properties.Properties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemStackCardHolder extends CardHolder
{
    private ItemStack itemStack;
    
    public ItemStackCardHolder(ItemStack itemStack)
    {
        super();
        this.itemStack = itemStack;
        readCardHolderFromNBT(getNBT());
    }
    
    private void saveToItemStack()
    {
        writeCardHolderToNBT(getNBT());
    }
    
    private CompoundTag getNBT()
    {
        return itemStack.getOrCreateTag();
    }
    
    @Override
    public void setCard(Properties card)
    {
        super.setCard(card);
        saveToItemStack();
    }
    
    @Override
    public void setImageIndex(byte imageIndex)
    {
        super.setImageIndex(imageIndex);
        saveToItemStack();
    }
    
    @Override
    public void setRarity(String rarity)
    {
        super.setRarity(rarity);
        saveToItemStack();
    }
    
    @Override
    public void setCode(String code)
    {
        super.setCode(code);
        saveToItemStack();
    }
    
    @Override
    public void override(CardHolder cardHolder)
    {
        super.override(cardHolder);
        saveToItemStack();
    }
}
