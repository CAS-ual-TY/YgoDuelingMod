package de.cas_ual_ty.ydm.duel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Zone
{
    public final PlayField playField;
    
    public ZoneType type;
    public ZoneOwner owner;
    
    // cards list, 0 is top, size()-1 is bottom
    public List<DuelCard> cardsList;
    private final int size;
    
    // allows you to "take the stack" and see every card thats face up in it
    // otherwise (false) you can only see the top card
    // facedown cards can always only be seen by zone owner
    public boolean isSecret;
    
    public byte index;
    
    public Zone(PlayField playField, ZoneType type, ZoneOwner owner, int size, boolean isSecret)
    {
        this.playField = playField;
        this.type = type;
        this.owner = owner;
        this.cardsList = new ArrayList<>(size);
        this.size = size;
        this.isSecret = isSecret;
        
        this.index = -1;
    }
    
    public Zone(PlayField playField, ZoneType type, ZoneOwner owner)
    {
        this(playField, type, owner, 1, false);
    }
    
    public void initIndex(byte index)
    {
        this.index = index;
    }
    
    public boolean isOwner(PlayerRole player)
    {
        return this.getOwner().getPlayer() == player;
    }
    
    public boolean hasOwner()
    {
        return this.getOwner() != ZoneOwner.NONE;
    }
    
    public DuelCard getCard(short index)
    {
        return this.getCardsList().get(index);
    }
    
    public short getCardIndexShort(DuelCard card)
    {
        return (short)this.getCardIndex(card);
    }
    
    public int getCardIndex(DuelCard card)
    {
        return this.getCardsList().indexOf(card);
    }
    
    public boolean containsCard(DuelCard card)
    {
        return this.getCardsList().contains(card);
    }
    
    public void addCard(DuelCard card, int index)
    {
        this.getCardsList().add(index, card);
    }
    
    public void addTopCard(DuelCard card)
    {
        this.addCard(card, 0);
    }
    
    public void addBottomCard(DuelCard card)
    {
        this.addCard(card, this.getCardsAmount());
    }
    
    public boolean removeCard(DuelCard card)
    {
        return this.getCardsList().remove(card);
    }
    
    public DuelCard removeCard(int index)
    {
        return this.getCardsList().remove(index);
    }
    
    public DuelCard removeTopCard()
    {
        return this.removeCard(0);
    }
    
    public int getCardsAmount()
    {
        return this.getCardsList().size();
    }
    
    public void shuffle()
    {
        this.cardsList = new ArrayList<>(this.size);
        this.cardsList.addAll(this.getCardsList());
        
        Collections.shuffle(this.cardsList, this.playField.getRandom());
    }
    
    public void setCardsList(List<DuelCard> list)
    {
        this.cardsList.clear();
        this.cardsList.addAll(list);
    }
    
    // --- Getters ---
    
    public ZoneType getType()
    {
        return this.type;
    }
    
    public ZoneOwner getOwner()
    {
        return this.owner;
    }
    
    public List<DuelCard> getCardsList()
    {
        return this.cardsList;
    }
    
    public byte getIndex()
    {
        return this.index;
    }
}
