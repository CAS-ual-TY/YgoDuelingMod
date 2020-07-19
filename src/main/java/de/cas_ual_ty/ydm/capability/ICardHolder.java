package de.cas_ual_ty.ydm.capability;

import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.Rarity;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.util.Database;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.nbt.CompoundNBT;

public interface ICardHolder
{
    public Card getCard();
    
    public void setCard(Card card);
    
    public void overrideImageIndex(byte imageIndex);
    
    public byte getOverriddenImageIndex();
    
    public void overrideRarity(Rarity rarity);
    
    public Rarity getOverriddenRarity();
    
    public default boolean isImageIndexOverridden()
    {
        return this.getOverriddenImageIndex() != -1;
    }
    
    public default byte getActiveImageIndex()
    {
        return this.isImageIndexOverridden() ? this.getOverriddenImageIndex() : this.getCard().getImageIndex();
    }
    
    public default boolean isRarityOverridden()
    {
        return this.getOverriddenRarity() != null;
    }
    
    public default Rarity getActiveRarity()
    {
        return this.isRarityOverridden() ? this.getOverriddenRarity() : this.getCard().getRarity();
    }
    
    public default Properties getProperties()
    {
        return this.getCard().getProperties();
    }
    
    public default void readCardHolderFromNBT(CompoundNBT nbt)
    {
        this.setCard(Database.CARDS_LIST.get(nbt.getString(JsonKeys.SET_ID)));
        this.overrideImageIndex(nbt.getByte(JsonKeys.IMAGE_INDEX));
        this.overrideRarity(Rarity.fromString(nbt.getString(JsonKeys.RARITY)));
    }
    
    public default void writeCardHolderToNBT(CompoundNBT nbt)
    {
        nbt.putString(JsonKeys.SET_ID, this.getCard().getSetId());
        nbt.putByte(JsonKeys.IMAGE_INDEX, this.getOverriddenImageIndex());
        
        if(this.isRarityOverridden())
        {
            nbt.putString(JsonKeys.RARITY, this.getOverriddenRarity().name);
        }
        else
        {
            nbt.putString(JsonKeys.RARITY, "");
        }
    }
}
