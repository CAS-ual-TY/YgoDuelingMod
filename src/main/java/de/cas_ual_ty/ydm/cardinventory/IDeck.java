package de.cas_ual_ty.ydm.cardinventory;

import java.util.List;

import de.cas_ual_ty.ydm.card.CardHolder;

public interface IDeck
{
    public List<CardHolder> getMainDeck();
    
    public List<CardHolder> getExtraDeck();
    
    public List<CardHolder> getAllCards();
}
