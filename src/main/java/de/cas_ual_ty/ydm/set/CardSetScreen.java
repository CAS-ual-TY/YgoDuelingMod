package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.carditeminventory.CardItemInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CardSetScreen extends CardItemInventoryScreen<CardSetContainer>
{
    public CardSetScreen(CardSetContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
    }
}
