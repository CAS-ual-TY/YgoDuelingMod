package de.cas_ual_ty.ydm.capability;

import de.cas_ual_ty.ydm.Database;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.Rarity;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemStackCardHolder implements ICardHolder
{
    private ItemStack itemStack;
    
    public ItemStackCardHolder(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }
    
    private CompoundNBT getNBT()
    {
        return this.itemStack.getOrCreateTag();
    }
    
    @Override
    public Card getCard()
    {
        return Database.CARDS_LIST.get(this.getNBT().getString(JsonKeys.SET_ID));
    }
    
    @Override
    public void setCard(Card card)
    {
        this.getNBT().putString(JsonKeys.SET_ID, card.getSetId());
    }
    
    @Override
    public void overrideImageIndex(byte imageIndex)
    {
        this.getNBT().putByte(JsonKeys.IMAGE_INDEX, imageIndex);
    }
    
    @Override
    public byte getOverriddenImageIndex()
    {
        return this.getNBT().getByte(JsonKeys.IMAGE_INDEX);
    }
    
    @Override
    public void overrideRarity(Rarity rarity)
    {
        this.getNBT().putString(JsonKeys.RARITY, rarity.name);
    }
    
    @Override
    public Rarity getOverriddenRarity()
    {
        return Rarity.fromString(this.getNBT().getString(JsonKeys.RARITY));
    }
}
