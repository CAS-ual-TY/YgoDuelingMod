package de.cas_ual_ty.ydm.cardinventory;

import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;

public interface ICardInventory
{
    public int getPagesAmount();
    
    public List<CardHolder> getCardsForPage(int page);
    
    public void addCard(CardHolder card);
    
    public CardHolder extractCard(int page, int index);
    
    public void updateCardsList(String search);
}
