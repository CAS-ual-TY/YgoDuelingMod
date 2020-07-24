package de.cas_ual_ty.ydm.cardinventory;

import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.entity.player.PlayerEntity;

public interface ICardInventory
{
    public int getPagesAmount();
    
    public List<CardHolder> getCardsForPage(int page);
    
    public void addCard(CardHolder card);
    
    public default void clickedOn(PlayerEntity player, int page, int index)
    {
        this.extractCard(page, index);
    }
    
    public CardHolder extractCard(int page, int index);
    
    public void updateCardsList(String search);
}
