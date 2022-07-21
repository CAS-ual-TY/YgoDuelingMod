package de.cas_ual_ty.ydm.duel.playfield;

import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.duel.PlayerRole;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Zone
{
    public final PlayField playField;
    public final ZoneType type;
    public final byte index;
    
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    
    public ZoneOwner owner;
    public final boolean isOwnerTemporary;
    
    // cards list, 0 is top, size()-1 is bottom
    public List<DuelCard> cardsList;
    
    @Nullable
    public CardPosition defaultCardPosition;
    
    public int counters;
    
    public Zone(PlayField playField, ZoneType type, byte index, ZoneOwner owner, int x, int y, int width, int height)
    {
        this.playField = playField;
        this.type = type;
        this.index = index;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.owner = owner;
        isOwnerTemporary = !hasOwner();
        cardsList = new ArrayList<>(0);
        defaultCardPosition = this.type.defaultCardPosition;
        counters = 0;
    }
    
    public CardSleevesType getSleeves()
    {
        return playField.getSleeves(getOwner());
    }
    
    public boolean isOwner(PlayerRole player)
    {
        return getOwner().getPlayer() == player;
    }
    
    public boolean hasOwner()
    {
        return getOwner() != ZoneOwner.NONE;
    }
    
    // unsafe: error if empty
    public DuelCard getTopCard()
    {
        return cardsList.get(0);
    }
    
    @Nullable
    public DuelCard getTopCardSafely()
    {
        if(getCardsAmount() > 0)
        {
            return getTopCard();
        }
        else
        {
            return null;
        }
    }
    
    public DuelCard getCardUnsafe(int index)
    {
        return getCardsList().get(index);
    }
    
    public DuelCard getCard(short index)
    {
        return getCardsList().get(index);
    }
    
    public short getCardIndexShort(DuelCard card)
    {
        return (short) getCardIndex(card);
    }
    
    public int getCardIndex(DuelCard card)
    {
        return getCardsList().indexOf(card);
    }
    
    public boolean containsCard(DuelCard card)
    {
        return getCardsList().contains(card);
    }
    
    public void addCard(ZoneOwner source, DuelCard card, int index)
    {
        trySetOwner(source);
        getCardsList().add(index, card);
    }
    
    public void addTopCard(ZoneOwner source, DuelCard card)
    {
        addCard(source, card, 0);
    }
    
    public void addBottomCard(ZoneOwner source, DuelCard card)
    {
        addCard(source, card, getCardsAmount());
    }
    
    public boolean removeCard(DuelCard card)
    {
        boolean b = getCardsList().remove(card);
        onCardsRemoval();
        return b;
    }
    
    public DuelCard removeCard(int index)
    {
        DuelCard c = removeCardKeepCounters(index);
        onCardsRemoval();
        return c;
    }
    
    public DuelCard removeCardKeepCounters(int index)
    {
        return getCardsList().remove(index);
    }
    
    public DuelCard removeTopCard()
    {
        return removeCard(0);
    }
    
    protected void onCardsRemoval()
    {
        if(getCardsAmount() <= 0)
        {
            removeAllCounters();
        }
    }
    
    public int getCardsAmount()
    {
        return getCardsList().size();
    }
    
    public void trySetOwner(ZoneOwner owner)
    {
        if(getCardsList().isEmpty() && getIsOwnerTemporary())
        {
            this.owner = owner;
        }
    }
    
    public void shuffle(Random random)
    {
        Collections.shuffle(cardsList, random);
    }
    
    public void setCardsList(List<DuelCard> list)
    {
        cardsList.clear();
        cardsList.addAll(list);
    }
    
    // --- Getters ---
    
    public ZoneType getType()
    {
        return type;
    }
    
    public ZoneOwner getOwner()
    {
        return owner;
    }
    
    public boolean getIsOwnerTemporary()
    {
        return isOwnerTemporary;
    }
    
    public List<DuelCard> getCardsList()
    {
        return cardsList;
    }
    
    // this is for when you have to flip the main deck, to make sure all new cards go in the right way
    @Nullable
    public CardPosition getDefaultCardPosition()
    {
        return defaultCardPosition;
    }
    
    public void flipDefaultCardPosition()
    {
        if(defaultCardPosition != null)
        {
            defaultCardPosition = defaultCardPosition.flip();
        }
    }
    
    public int getCounters()
    {
        return counters;
    }
    
    public void setCounters(int counters)
    {
        this.counters = counters;
    }
    
    public void changeCounters(int change)
    {
        counters = Math.max(0, Math.min(99, counters + change));
    }
    
    public void removeAllCounters()
    {
        counters = 0;
    }
}
