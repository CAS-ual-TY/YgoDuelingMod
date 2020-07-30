package de.cas_ual_ty.ydm.cardinventory;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.YdmUtil;

public class CardInventory implements ICardInventory
{
    public static final int DEFAULT_PAGE_ROWS = 6;
    public static final int DEFAULT_PAGE_COLUMNS = 9;
    public static final int DEFAULT_CARDS_PER_PAGE = CardInventory.DEFAULT_PAGE_ROWS * CardInventory.DEFAULT_PAGE_COLUMNS;
    
    protected List<CardHolder> list;
    protected List<CardHolder> activeList;
    
    public CardInventory(List<CardHolder> list)
    {
        this.list = list;
        this.activeList = new ArrayList<>(this.cardsPerPage());
    }
    
    protected int cardsPerPage()
    {
        return CardInventory.DEFAULT_CARDS_PER_PAGE;
    }
    
    @Override
    public int getPagesAmount()
    {
        return this.activeList.size() / this.cardsPerPage() + 1;
    }
    
    /*
     * cardsPerPage = 9
     * totalCards = 12
     * totalPages = 2
     * 
     * Page = 1
     * cards indices 0-9 (0 incl, 9 excl)
     * min = 0 = cardsPerPage * (page - 1) = 9 * 0
     * max = 9 = MIN( cardsPerPage + min = 9 + 0 = 9 , totalCards = 12 )
     * 
     * Page = 2
     * cards indices 9-12
     * min = 9 = cardsPerPage * (page - 1) = 9 * 1
     * max = 12 = MIN( cardsPerPage + 9 = 9 + 9 = 18 , totalCards = 12 )
     */
    
    /*
     * cardsPerPage = 9
     * totalCards = 9
     * totalPages = 9/9 + 1 = 2
     * 
     * Page = 2
     * cards indices 9-9
     * min = 9 = cardsPerPage * (page - 1) = 9 * 1
     * max = 9 = MIN( cardsPerPage + min = 9 + 9 = 18 , totalCards = 9 )
     */
    
    /*
     * cardsPerPage = 6 * 9 = 54
     * totalCards = 0
     * totalPages = 0/54 + 1 = 1
     * 
     * Page = 1
     * cards indices 0-0
     * min = 0 = cardsPerPage * (page - 1) = 0
     * max = 0 = MIN( cardsPerPage + min = 54 + 0 = 18 , totalCards = 0 )
     */
    
    @Override
    public List<CardHolder> getCardsForPage(int page)
    {
        page = YdmUtil.range(page, 1, this.getPagesAmount());
        
        int min = this.cardsPerPage() * (page - 1);
        int max = Math.min(this.cardsPerPage() + min, this.list.size());
        
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
        CardHolder card = this.list.remove((page - 1) * this.cardsPerPage() + index);
        this.activeList.remove(card);
        return card;
    }
    
    @Override
    public void updateCardsList(String search)
    {
        search = search.trim();
        this.activeList.clear();
        
        if(!search.isEmpty())
        {
            for(CardHolder card : this.list)
            {
                if(card.getProperties().getName().contains(search))
                {
                    this.activeList.add(card);
                }
            }
        }
        else
        {
            this.activeList.addAll(this.list);
        }
    }
    
    @Override
    public int totalCardsSize()
    {
        return this.list.size();
    }
}
