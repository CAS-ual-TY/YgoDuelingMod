package de.cas_ual_ty.ydm.duel.playfield;

import de.cas_ual_ty.ydm.card.CardSleevesType;
import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelPhase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlayField
{
    public static final int MIN_LP = 0;
    public static final int MAX_LP = 99999;
    
    public final DuelManager duelManager;
    public final PlayFieldType playFieldType;
    public final List<Zone> zones;
    
    public byte player1Offset;
    public byte player2Offset;
    public byte extraOffset;
    
    public Zone player1Deck;
    public Zone player1ExtraDeck;
    public Zone player2Deck;
    public Zone player2ExtraDeck;
    
    public int player1LP;
    public int player2LP;
    
    protected Zone player1ClickedZone;
    protected DuelCard player1ClickedCard;
    protected Zone player2ClickedZone;
    protected DuelCard player2ClickedCard;
    
    public boolean player1Turn;
    public DuelPhase phase;
    
    public CardSleevesType player1Sleeves;
    public CardSleevesType player2Sleeves;
    
    public PlayField(DuelManager duelManager, PlayFieldType type)
    {
        if(type.player1Deck == null || type.player1ExtraDeck == null || type.player2Deck == null || type.player2ExtraDeck == null)
        {
            throw new IllegalArgumentException();
        }
        
        this.duelManager = duelManager;
        playFieldType = type;
        zones = new ArrayList<>(type.zoneEntries.size());
        
        byte index = 0;
        Zone z;
        for(PlayFieldType.ZoneEntry e : type.zoneEntries)
        {
            zones.add(z = new Zone(this, e.type, index++, e.owner, e.x, e.y, e.width, e.height));
            
            if(e == type.player1Deck)
            {
                player1Deck = z;
            }
            else if(e == type.player1ExtraDeck)
            {
                player1ExtraDeck = z;
            }
            else if(e == type.player2Deck)
            {
                player2Deck = z;
            }
            else if(e == type.player2ExtraDeck)
            {
                player2ExtraDeck = z;
            }
        }
        
        zones.sort((z1, z2) ->
        {
            int x1;
            int x2;
            
            if(z1.owner == ZoneOwner.PLAYER1)
            {
                x1 = -1;
            }
            else if(z1.owner == ZoneOwner.PLAYER2)
            {
                x1 = 0;
            }
            else
            {
                x1 = 1;
            }
            
            if(z2.owner == ZoneOwner.PLAYER1)
            {
                x2 = -1;
            }
            else if(z2.owner == ZoneOwner.PLAYER2)
            {
                x2 = 0;
            }
            else
            {
                x2 = 1;
            }
            
            return x1 - x2;
        });
        
        player1Offset = 0;
        player2Offset = 0;
        extraOffset = 0;
        
        for(Zone zone : zones)
        {
            if(zone.getOwner() == ZoneOwner.PLAYER2)
            {
                break;
            }
            
            ++player2Offset;
        }
        
        for(Zone zone : zones)
        {
            if(zone.getOwner() == ZoneOwner.NONE)
            {
                break;
            }
            
            ++extraOffset;
        }
        
        player1LP = type.startingLifePoints;
        player2LP = type.startingLifePoints;
        
        player1ClickedCard = null;
        player1ClickedZone = null;
        player2ClickedCard = null;
        player2ClickedZone = null;
        
        player1Turn = true;
        phase = DuelPhase.DP;
        
        player1Sleeves = CardSleevesType.CARD_BACK;
        player2Sleeves = CardSleevesType.CARD_BACK;
    }
    
    public void initSleeves(CardSleevesType player1Sleeves, CardSleevesType player2Sleeves)
    {
        this.player1Sleeves = player1Sleeves;
        this.player2Sleeves = player2Sleeves;
    }
    
    public CardSleevesType getSleeves(ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return player1Sleeves;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return player2Sleeves;
        }
        else
        {
            return CardSleevesType.CARD_BACK;
        }
    }
    
    public List<Zone> getZones()
    {
        return zones;
    }
    
    public List<ZoneInteraction> getActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        return playFieldType.getActionsFor(player, interactor, interactorCard, interactee);
    }
    
    public List<ZoneInteraction> getAdvancedActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        return playFieldType.getAdvancedActionsFor(player, interactor, interactorCard, interactee);
    }
    
    public Zone getReplacementZoneForCard(Zone zone, DuelCard card)
    {
        /*
        if(!zone.getType().getIsStrict())
        {
            // zone isnt strict (eg. player1's monster zones allow player2's cards in them)
            return zone;
        }
        else
        {
            // zone is strict, and zone owner is card owner
            if(zone.getOwner() == card.getOwner())
            {
                return zone;
            }
            else
            {
                // zone is strict, and zone owner is not card owner
                // swap for equivalent zone, shift by offset
                return this.zones[ZoneOwner.convertIndex(zone.getIndex())];
            }
        }
        */
        return null;
    }
    
    @Nullable
    public Zone getZoneByTypeAndPlayer(ZoneType type, ZoneOwner owner)
    {
        for(Zone zone : zones)
        {
            if(zone.type == type && zone.getOwner() == owner && !zone.getIsOwnerTemporary())
            {
                return zone;
            }
        }
        
        return null;
    }
    
    public DuelManager getDuelManager()
    {
        return duelManager;
    }
    
    public Zone getZone(byte zoneId)
    {
        return zones.get(zoneId);
    }
    
    public int changeLifePoints(int amount, ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return (player1LP = Math.min(PlayField.MAX_LP, Math.max(PlayField.MIN_LP, player1LP + amount)));
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return (player2LP = Math.min(PlayField.MAX_LP, Math.max(PlayField.MIN_LP, player2LP + amount)));
        }
        else
        {
            return -1;
        }
    }
    
    public int getLifePoints(ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return player1LP;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return player2LP;
        }
        else
        {
            return -1;
        }
    }
    
    public void setLifePoints(int amount, ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            player1LP = amount;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            player2LP = amount;
        }
    }
    
    public void setPhase(DuelPhase phase)
    {
        this.phase = phase;
    }
    
    public DuelPhase getPhase()
    {
        return phase;
    }
    
    public void endTurn()
    {
        phase = DuelPhase.getFromIndex(DuelPhase.FIRST_INDEX);
        player1Turn = !player1Turn;
    }
    
    public boolean isPlayer1Turn()
    {
        return player1Turn;
    }
    
    public boolean isPlayer2Turn()
    {
        return !player1Turn;
    }
    
    public boolean isPlayerTurn(ZoneOwner player)
    {
        if(player == ZoneOwner.PLAYER2)
        {
            return isPlayer2Turn();
        }
        else
        {
            return isPlayer1Turn();
        }
    }
    
    public void setClickedForPlayer(ZoneOwner owner, @Nullable Zone zone, @Nullable DuelCard card)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            setPlayer1Clicked(zone, card);
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            setPlayer2Clicked(zone, card);
        }
    }
    
    public void setPlayer1Clicked(@Nullable Zone zone, @Nullable DuelCard card)
    {
        player1ClickedZone = zone;
        player1ClickedCard = card;
    }
    
    public void setPlayer2Clicked(@Nullable Zone zone, @Nullable DuelCard card)
    {
        player2ClickedZone = zone;
        player2ClickedCard = card;
    }
    
    @Nullable
    public Zone getClickedZoneForPlayer(ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return player1ClickedZone;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return player2ClickedZone;
        }
        else
        {
            return null;
        }
    }
    
    @Nullable
    public DuelCard getClickedCardForPlayer(ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return player1ClickedCard;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return player2ClickedCard;
        }
        else
        {
            return null;
        }
    }
}
