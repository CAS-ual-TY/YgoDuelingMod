package de.cas_ual_ty.ydm.card;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemStackCardHolder extends CardHolder
{
    private ItemStack itemStack;
    
    public ItemStackCardHolder(ItemStack itemStack)
    {
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
    public void setCard(Card card)
    {
        super.setCard(card);
        this.saveToItemStack();
    }
    
    @Override
    public void overrideImageIndex(byte imageIndex)
    {
        super.overrideImageIndex(imageIndex);
        this.saveToItemStack();
    }
    
    @Override
    public void overrideRarity(Rarity rarity)
    {
        super.overrideRarity(rarity);
        this.saveToItemStack();
    }
}
