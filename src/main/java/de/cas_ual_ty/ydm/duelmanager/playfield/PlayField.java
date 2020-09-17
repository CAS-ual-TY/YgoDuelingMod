package de.cas_ual_ty.ydm.duelmanager.playfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;

public class PlayField
{
    public DuelManager duelManager;
    public List<Zone> zones;
    
    public byte player1Offset;
    public byte player2Offset;
    public byte zonesPerPlayer;
    public byte extraOffset;
    
    public PlayField(DuelManager duelManager, List<ZoneType> types)
    {
        this.zones = new ArrayList<>(types.stream().mapToInt((def) -> def.getChildrenAmount() * (def.getNoOwner() ? 1 : 2)).sum());
        
        byte i;
        byte index = 0;
        
        for(ZoneOwner player : ZoneOwner.PLAYERS)
        {
            for(ZoneType type : types)
            {
                if(!type.getNoOwner())
                {
                    for(i = 0; i < type.getChildrenAmount(); ++i)
                    {
                        this.zones.add(new Zone(this, type, index++, i, player));
                    }
                }
            }
        }
        
        this.extraOffset = (byte)(index + 1);
        
        this.player1Offset = 0;
        this.player2Offset = (byte)(this.extraOffset / 2);
        this.zonesPerPlayer = this.extraOffset;
        
        for(ZoneType type : types)
        {
            if(type.getNoOwner())
            {
                for(i = 0; i < type.getChildrenAmount(); ++i)
                {
                    this.zones.add(new Zone(this, type, index++, i, ZoneOwner.NONE));
                }
            }
        }
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
    
    // for zones without children
    public Zone getSingleZone(ZoneType type, ZoneOwner owner)
    {
        if(type.getChildrenAmount() != 1)
        {
            return null;
        }
        
        byte i;
        byte end;
        
        if(owner == ZoneOwner.PLAYER1)
        {
            i = this.player1Offset;
            end = this.player2Offset;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            i = this.player2Offset;
            end = this.extraOffset;
        }
        else
        {
            i = this.extraOffset;
            end = (byte)this.zones.size();
        }
        
        Zone zone;
        for(; i < end; ++i)
        {
            zone = this.getZone(i);
            
            if(zone.getType().getChildrenAmount() == 1 && zone.getType() == type)
            {
                return zone;
            }
        }
        
        return null;
    }
    
    // for zone types with or without children
    public List<Zone> getGroupZone(ZoneType type, ZoneOwner owner)
    {
        byte i;
        byte end;
        
        if(owner == ZoneOwner.PLAYER1)
        {
            i = this.player1Offset;
            end = this.player2Offset;
        }
        else if(owner == ZoneOwner.PLAYER2)
        {
            i = this.player2Offset;
            end = this.extraOffset;
        }
        else
        {
            i = this.extraOffset;
            end = (byte)this.zones.size();
        }
        
        Zone zone;
        for(; i < end; ++i)
        {
            zone = this.getZone(i);
            
            if(zone.getType().getChildrenAmount() == 1 && zone.getType() == type)
            {
                return ImmutableList.copyOf(this.zones.subList(i, i + zone.getType().getChildrenAmount()));
            }
        }
        
        return ImmutableList.of();
    }
    
    public DuelManager getDuelManager()
    {
        return this.duelManager;
    }
    
    public Zone getZone(byte zoneId)
    {
        return this.zones.get(zoneId);
    }
    
    public Random getRandom()
    {
        return null; // TODO
    }
}
