package de.cas_ual_ty.ydm.cardinventory;

import de.cas_ual_ty.ydm.card.CardHolder;

import java.util.List;

public interface ICardInventory
{
    int getPagesAmount();
    
    List<CardHolder> getCardsForPage(int page);
    
    void addCard(CardHolder card);
    
    CardHolder extractCard(int page, int index);
    
    void updateCardsList(String search);
    
    int totalCardsSize();
}
