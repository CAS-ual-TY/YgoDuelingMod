package de.cas_ual_ty.ydm.cardinventory;

import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;

public class CardInventory implements ICardInventory
{
    public static final int DEFAULT_CARDS_PER_PAGE = 6 * 9;
    
    protected List<CardHolder> list;
    
    public CardInventory(List<CardHolder> list)
    {
        this.list = list;
    }
    
    protected int cardsPerPage()
    {
        return CardInventory.DEFAULT_CARDS_PER_PAGE;
    }
    
    @Override
    public int getPagesAmount()
    {
        return this.list.size();
    }
    
    @Override
    public List<CardHolder> getCardsForPage(int page)
    {
        int min = this.cardsPerPage() * page;
        int max = this.cardsPerPage() + min;
        return this.list.subList(min, max);
    }
    
    @Override
    public void addCard(CardHolder card)
    {
        this.list.add(card);
    }
    
    @Override
    public CardHolder extractCard(int page, int index)
    {
        return this.list.remove(page * this.cardsPerPage() + index);
    }
}
