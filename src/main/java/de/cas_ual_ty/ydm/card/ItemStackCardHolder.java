package de.cas_ual_ty.ydm.card;

import de.cas_ual_ty.ydm.card.properties.Properties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemStackCardHolder extends CardHolder
{
    private ItemStack itemStack;
    
    public ItemStackCardHolder(ItemStack itemStack)
    {
        super();
        this.itemStack = itemStack;
        this.readCardHolderFromNBT(this.getNBT());
    }
    
    private void saveToItemStack()
    {
        this.writeCardHolderToNBT(this.getNBT());
    }
    
    private CompoundNBT getNBT()
    {
        return this.itemStack.getOrCreateTag();
    }
    
    @Override
    public void setCard(Properties card)
    {
        super.setCard(card);
        this.saveToItemStack();
    }
    
    @Override
    public void setImageIndex(byte imageIndex)
    {
        super.setImageIndex(imageIndex);
        this.saveToItemStack();
    }
    
    @Override
    public void setRarity(String rarity)
    {
        super.setRarity(rarity);
        this.saveToItemStack();
    }
    
    @Override
    public void setCode(String code)
    {
        super.setCode(code);
        this.saveToItemStack();
    }
    
    @Override
    public void override(CardHolder cardHolder)
    {
        super.override(cardHolder);
        this.saveToItemStack();
    }
}
