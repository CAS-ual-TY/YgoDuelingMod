package de.cas_ual_ty.ydm.duel.playfield;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.DuelPhase;

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
    
    public boolean player1Turn;
    public DuelPhase phase;
    
    public PlayField(DuelManager duelManager, PlayFieldType type)
    {
        if(type.player1Deck == null || type.player1ExtraDeck == null || type.player2Deck == null || type.player2ExtraDeck == null)
        {
            throw new IllegalArgumentException();
        }
        
        this.duelManager = duelManager;
        this.playFieldType = type;
        this.zones = new ArrayList<>(type.zoneEntries.size());
        
        byte index = 0;
        Zone z;
        for(PlayFieldType.ZoneEntry e : type.zoneEntries)
        {
            this.zones.add(z = new Zone(this, e.type, index++, e.owner, e.x, e.y, e.width, e.height));
            
            if(e == type.player1Deck)
            {
                this.player1Deck = z;
            }
            else if(e == type.player1ExtraDeck)
            {
                this.player1ExtraDeck = z;
            }
            else if(e == type.player2Deck)
            {
                this.player2Deck = z;
            }
            else if(e == type.player2ExtraDeck)
            {
                this.player2ExtraDeck = z;
            }
        }
        
        this.zones.sort((z1, z2) ->
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
        
        this.player1Offset = 0;
        this.player2Offset = 0;
        this.extraOffset = 0;
        
        for(Zone zone : this.zones)
        {
            if(zone.getOwner() == ZoneOwner.PLAYER2)
            {
                break;
            }
            
            ++this.player2Offset;
        }
        
        for(Zone zone : this.zones)
        {
            if(zone.getOwner() == ZoneOwner.NONE)
            {
                break;
            }
            
            ++this.extraOffset;
        }
        
        this.player1LP = type.startingLifePoints;
        this.player2LP = type.startingLifePoints;
        
        this.player1Turn = true;
        this.phase = DuelPhase.DP;
    }
    
    public List<Zone> getZones()
    {
        return this.zones;
    }
    
    public List<ZoneInteraction> getActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        return this.playFieldType.getActionsFor(player, interactor, interactorCard, interactee);
    }
    
    public List<ZoneInteraction> getAdvancedActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        return this.playFieldType.getAdvancedActionsFor(player, interactor, interactorCard, interactee);
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
        */ return null;
    }
    
    @Nullable
    public Zone getZoneByTypeAndPlayer(ZoneType type, ZoneOwner owner)
    {
        for(Zone zone : this.zones)
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
        return this.duelManager;
    }
    
    public Zone getZone(byte zoneId)
    {
        return this.zones.get(zoneId);
    }
    
    public int changeLifePoints(int amount, ZoneOwner owner)
    {
        if(owner == ZoneOwner.PLAYER1)
        {
            return (this.player1LP = Math.min(PlayField.MAX_LP, Math.max(PlayField.MIN_LP, this.player1LP + amount)));
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return (this.player2LP = Math.min(PlayField.MAX_LP, Math.max(PlayField.MIN_LP, this.player2LP + amount)));
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
            return this.player1LP;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            return this.player2LP;
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
            this.player1LP = amount;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            this.player2LP = amount;
        }
    }
    
    public void setPhase(DuelPhase phase)
    {
        this.phase = phase;
    }
    
    public DuelPhase getPhase()
    {
        return this.phase;
    }
    
    public void endTurn()
    {
        this.phase = DuelPhase.getFromIndex(DuelPhase.FIRST_INDEX);
        this.player1Turn = !this.player1Turn;
    }
    
    public boolean isPlayer1Turn()
    {
        return this.player1Turn;
    }
    
    public boolean isPlayer2Turn()
    {
        return !this.player1Turn;
    }
    
    public boolean isPlayerTurn(ZoneOwner player)
    {
        if(player == ZoneOwner.PLAYER2)
        {
            return this.isPlayer2Turn();
        }
        else
        {
            return this.isPlayer1Turn();
        }
    }
}
