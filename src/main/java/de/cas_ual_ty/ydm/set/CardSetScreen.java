package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import de.cas_ual_ty.ydm.carditeminventory.CIIScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CardSetScreen extends CIIScreen<CIIContainer>
{
    public CardSetScreen(CIIContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
    }
}
