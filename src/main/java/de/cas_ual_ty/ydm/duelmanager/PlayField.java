package de.cas_ual_ty.ydm.duelmanager;

import java.util.Random;

public class PlayField
{
    public DuelManager duelManager;
    public Zone[] zones;
    
    public PlayField(DuelManager duelManager)
    {
        this.zones = new Zone[] {
            new Zone(this, ZoneType.HAND, ZoneOwner.PLAYER1, 75, false),
            new Zone(this, ZoneType.DECK, ZoneOwner.PLAYER1, 60, true),
            new Zone(this, ZoneType.SPELL_TRAP_PENDULUM, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.SPELL_TRAP, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.SPELL_TRAP, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.SPELL_TRAP, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.SPELL_TRAP_PENDULUM, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.EXTRA_DECK, ZoneOwner.PLAYER1, 15, false),
            new Zone(this, ZoneType.GRAVEYARD, ZoneOwner.PLAYER1, 75, false),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.FIELD_SPELL, ZoneOwner.PLAYER1),
            new Zone(this, ZoneType.BANISHED, ZoneOwner.PLAYER1, 15, false),
            new Zone(this, ZoneType.EXTRA, ZoneOwner.PLAYER1),
            
            new Zone(this, ZoneType.HAND, ZoneOwner.PLAYER2, 75, false),
            new Zone(this, ZoneType.DECK, ZoneOwner.PLAYER2, 60, true),
            new Zone(this, ZoneType.SPELL_TRAP_PENDULUM, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.SPELL_TRAP, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.SPELL_TRAP, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.SPELL_TRAP, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.SPELL_TRAP_PENDULUM, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.EXTRA_DECK, ZoneOwner.PLAYER2, 15, false),
            new Zone(this, ZoneType.GRAVEYARD, ZoneOwner.PLAYER2, 75, false),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.MONSTER, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.FIELD_SPELL, ZoneOwner.PLAYER2),
            new Zone(this, ZoneType.BANISHED, ZoneOwner.PLAYER2, 15, false),
            new Zone(this, ZoneType.EXTRA, ZoneOwner.PLAYER2),
            
            new Zone(this, ZoneType.EXTRA_MONSTER, ZoneOwner.NONE),
            new Zone(this, ZoneType.EXTRA_MONSTER, ZoneOwner.NONE),
        };
        
        byte index = 0;
        for(Zone zone : this.zones)
        {
            zone.initIndex(index++);
        }
    }
    
    public Zone getReplacementZoneForCard(Zone zone, DuelCard card)
    {
        if(!zone.getType().getIsStrict() || !zone.hasOwner())
        {
            // zone isnt strict (eg. player1's monster zones allow player2's cards in them)
            return zone;
        }
        else
        {
            // zone is strict, and zone owner is card owner
            if(zone.isOwner(card.getOwner()))
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
    }
    
    public DuelManager getDuelManager()
    {
        return this.duelManager;
    }
    
    public Zone getZone(byte zoneId)
    {
        return this.zones[zoneId];
    }
    
    public Random getRandom()
    {
        return null; // TODO
    }
}
