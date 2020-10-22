package de.cas_ual_ty.ydm.duelmanager.playfield;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import de.cas_ual_ty.ydm.duelmanager.action.ActionIcon;

public class PlayFieldType
{
    public DuelManager duelManager;
    public List<ZoneEntry> zoneEntries;
    public List<InteractionEntry> interactionEntries;
    
    public ZoneEntry player1Deck;
    public ZoneEntry player1ExtraDeck;
    public ZoneEntry player2Deck;
    public ZoneEntry player2ExtraDeck;
    
    public PlayFieldType()
    {
        this.zoneEntries = new ArrayList<>(0);
        this.interactionEntries = new ArrayList<>(0);
    }
    
    public PlayFieldType addEntry(ZoneType type, ZoneOwner owner, int x, int y, int width, int height)
    {
        this.zoneEntries.add(new ZoneEntry(type, owner, x, y, width, height));
        return this;
    }
    
    public PlayFieldType addEntryFull(ZoneType type, ZoneOwner owner, int x, int y)
    {
        return this.addEntry(type, owner, x, y, 32, 32);
    }
    
    public PlayFieldType addEntrySlim(ZoneType type, ZoneOwner owner, int x, int y)
    {
        return this.addEntry(type, owner, x, y, 24, 32);
    }
    
    // width and height already accounted form, so deltaX/Y is the space between zones
    public PlayFieldType repeat(int deltaX, int deltaY, int times)
    {
        if(deltaX == 0 && deltaY == 0)
        {
            throw new IllegalArgumentException();
        }
        
        ZoneEntry e = this.zoneEntries.get(this.zoneEntries.size() - 1);
        int x = e.x;
        int y = e.y;
        
        if(deltaX > 0)
        {
            deltaX += e.width;
        }
        else if(deltaX < 0)
        {
            deltaX -= e.width;
        }
        
        if(deltaY > 0)
        {
            deltaY += e.height;
        }
        else if(deltaY < 0)
        {
            deltaY -= e.height;
        }
        
        for(int i = 0; i < times; ++i)
        {
            x += deltaX;
            y += deltaY;
            this.addEntry(e.type, e.owner, x, y, e.width, e.height);
        }
        
        return this;
    }
    
    public PlayFieldType repeatPlayerZonesForOpponent()
    {
        List<ZoneEntry> newEntries = new ArrayList<>(this.zoneEntries.size());
        
        ZoneEntry e1;
        for(ZoneEntry e : this.zoneEntries)
        {
            if(e.owner != ZoneOwner.NONE)
            {
                newEntries.add(e1 = new ZoneEntry(e.type, e.owner == ZoneOwner.PLAYER1 ? ZoneOwner.PLAYER2 : ZoneOwner.PLAYER1, -e.x, -e.y, e.width, e.height));
                
                if(e == this.player1Deck)
                {
                    this.player2Deck = e1;
                }
                else if(e == this.player1ExtraDeck)
                {
                    this.player2ExtraDeck = e1;
                }
                else if(e == this.player2Deck)
                {
                    this.player1Deck = e1;
                }
                else if(e == this.player2ExtraDeck)
                {
                    this.player1ExtraDeck = e1;
                }
            }
        }
        
        this.zoneEntries.addAll(newEntries);
        
        return this;
    }
    
    public PlayFieldType setLastToPlayer1Deck()
    {
        this.player1Deck = this.zoneEntries.get(this.zoneEntries.size() - 1);
        return this;
    }
    
    public PlayFieldType setLastToPlayer1ExtraDeck()
    {
        this.player1ExtraDeck = this.zoneEntries.get(this.zoneEntries.size() - 1);
        return this;
    }
    
    public PlayFieldType setLastToPlayer2Deck()
    {
        this.player2Deck = this.zoneEntries.get(this.zoneEntries.size() - 1);
        return this;
    }
    
    public PlayFieldType setLastToPlayer2ExtraDeck()
    {
        this.player2ExtraDeck = this.zoneEntries.get(this.zoneEntries.size() - 1);
        return this;
    }
    
    public PlayFieldType registerInteration(ActionIcon icon, ZoneType interactor, ZoneType interactee, SingleZoneInteraction interaction)
    {
        this.interactionEntries.add(new InteractionEntry(icon, (test) -> test == interactor, (test) -> test == interactee, interaction));
        return this;
    }
    
    public PlayFieldType registerInteration(ActionIcon icon, Predicate<ZoneType> interactor, Predicate<ZoneType> interactee, SingleZoneInteraction interaction)
    {
        this.interactionEntries.add(new InteractionEntry(icon, interactor, interactee, interaction));
        return this;
    }
    
    public List<ZoneInteraction> getActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        List<ZoneInteraction> list = new ArrayList<>(4);
        
        Action action;
        for(InteractionEntry e : this.interactionEntries)
        {
            if(e.interactor.test(interactor.type) && e.interactee.test(interactee.type))
            {
                action = e.interaction.createAction(player, interactor, interactee);
                
                if(action != null)
                {
                    list.add(new ZoneInteraction(interactor, interactorCard, interactee, action, e.icon));
                }
            }
        }
        
        return list;
    }
    
    public static final class ZoneEntry
    {
        public final ZoneType type;
        public final ZoneOwner owner;
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        
        public ZoneEntry(ZoneType type, ZoneOwner owner, int x, int y, int width, int height)
        {
            this.type = type;
            this.owner = owner;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    
    public static final class InteractionEntry
    {
        public final ActionIcon icon;
        public final Predicate<ZoneType> interactor;
        public final Predicate<ZoneType> interactee;
        public final SingleZoneInteraction interaction;
        
        public InteractionEntry(ActionIcon icon, Predicate<ZoneType> interactor, Predicate<ZoneType> interactee, SingleZoneInteraction interaction)
        {
            this.icon = icon;
            this.interactor = interactor;
            this.interactee = interactee;
            this.interaction = interaction;
        }
    }
    
    public static interface SingleZoneInteraction
    {
        public @Nullable Action createAction(ZoneOwner player, Zone interactor, Zone interactee);
    }
}
