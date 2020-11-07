package de.cas_ual_ty.ydm.duel.playfield;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duel.DuelManager;
import de.cas_ual_ty.ydm.duel.action.Action;
import de.cas_ual_ty.ydm.duel.action.ActionIcon;

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
    
    public InteractionBuilder newInteraction()
    {
        return new InteractionBuilder();
    }
    
    public PlayFieldType registerInteraction(ActionIcon icon, Predicate<ZoneType> interactor, Predicate<DuelCard> interactorCard, Predicate<ZoneType> interactee, SingleZoneInteraction interaction)
    {
        this.interactionEntries.add(new InteractionEntry(icon, interactor, interactorCard, interactee, interaction));
        return this;
    }
    
    public PlayFieldType registerInteractions(List<InteractionEntry> list)
    {
        this.interactionEntries.addAll(list);
        return this;
    }
    
    public PlayFieldType copyInteractions(PlayFieldType playFieldType)
    {
        return this.registerInteractions(playFieldType.interactionEntries);
    }
    
    public List<ZoneInteraction> getActionsFor(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee)
    {
        List<ZoneInteraction> list = new ArrayList<>(4);
        
        Action action;
        for(InteractionEntry e : this.interactionEntries)
        {
            if(e.interactor.test(interactor.type) && e.interactorCard.test(interactorCard) && e.interactee.test(interactee.type))
            {
                action = e.interaction.createAction(player, interactor, interactorCard, interactee);
                
                if(action != null)
                {
                    list.add(new ZoneInteraction(interactor, interactorCard, interactee, action, e.icon));
                }
            }
        }
        
        return list;
    }
    
    public class InteractionBuilder
    {
        private ActionIcon icon;
        private Predicate<ZoneType> interactor;
        private Predicate<DuelCard> interactorCard;
        private Predicate<ZoneType> interactee;
        private SingleZoneInteraction interaction;
        
        public InteractionBuilder()
        {
            this.interactor = null;
            this.interactorCard = null;
            this.interactee = null;
            this.interaction = null;
        }
        
        public InteractionBuilder icon(ActionIcon icon)
        {
            this.icon = icon;
            return this;
        }
        
        public InteractionBuilder interactorPredicate(Predicate<ZoneType> interactor)
        {
            this.interactor = interactor;
            return this;
        }
        
        public InteractionBuilder interactorEquals(ZoneType interactor)
        {
            this.interactor = (zoneType) -> zoneType == interactor;
            return this;
        }
        
        public InteractionBuilder interactorUnequals(ZoneType interactor)
        {
            this.interactor = (zoneType) -> zoneType != interactor;
            return this;
        }
        
        public InteractionBuilder interactorIncluded(List<ZoneType> interactor)
        {
            this.interactor = (zoneType) -> interactor.contains(zoneType);
            return this;
        }
        
        public InteractionBuilder interactorExcluded(List<ZoneType> interactor)
        {
            this.interactor = (zoneType) -> !interactor.contains(zoneType);
            return this;
        }
        
        public InteractionBuilder interactorAny()
        {
            this.interactor = (zoneType) -> true;
            return this;
        }
        
        public InteractionBuilder interactorCardPredicate(Predicate<DuelCard> interactorCard)
        {
            this.interactorCard = interactorCard;
            return this;
        }
        
        public InteractionBuilder interactorCardNotNull()
        {
            this.interactorCard = (interactorCard) -> interactorCard != null;
            return this;
        }
        
        public InteractionBuilder interactorCardNull()
        {
            this.interactorCard = (interactorCard) -> interactorCard != null;
            return this;
        }
        
        public InteractionBuilder interactorCardAny()
        {
            this.interactorCard = (interactorCard) -> true;
            return this;
        }
        
        public InteractionBuilder interacteePredicate(Predicate<ZoneType> interactee)
        {
            this.interactee = interactee;
            return this;
        }
        
        public InteractionBuilder interacteeEquals(ZoneType interactee)
        {
            this.interactee = (zoneType) -> zoneType == interactee;
            return this;
        }
        
        public InteractionBuilder interacteeUnequals(ZoneType interactee)
        {
            this.interactee = (zoneType) -> zoneType != interactee;
            return this;
        }
        
        public InteractionBuilder interacteeIncluded(List<ZoneType> interactee)
        {
            this.interactee = (zoneType) -> interactee.contains(zoneType);
            return this;
        }
        
        public InteractionBuilder interacteeExcluded(List<ZoneType> interactee)
        {
            this.interactee = (zoneType) -> interactee.contains(zoneType);
            return this;
        }
        
        public InteractionBuilder interaction(SingleZoneInteraction interaction)
        {
            this.interaction = interaction;
            return this;
        }
        
        public InteractionBuilder playerAndInteractorSameOwner()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> player == interactor.getOwner() ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interactorAndInteracteeSameOwner()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactor.getOwner() == interactee.getOwner() ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interactorAndInteracteeNotSameOwner()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactor.getOwner() != interactee.getOwner() ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder cardAndInteracteeSameOwner()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactorCard.getOwner() == interactee.getOwner() ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interactorEmpty()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactor.getCardsAmount() == 0 ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interactorNonEmpty()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactor.getCardsAmount() > 0 ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interacteeEmpty()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactee.getCardsAmount() == 0 ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interacteeNonEmpty()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactee.getCardsAmount() > 0 ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interactorEqualsInteractee()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactor == interactee ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder interactorUnequalsInteractee()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactor != interactee ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder cardInPosition(CardPosition position)
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactorCard.getCardPosition() == position ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder cardNotInPosition(CardPosition position)
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactorCard.getCardPosition() != position ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public InteractionBuilder cardIsOnTop()
        {
            if(this.interaction == null)
            {
                return this;
            }
            
            SingleZoneInteraction interaction = this.interaction;
            this.interaction = (player, interactor, interactorCard, interactee) -> interactor.getCardIndex(interactorCard) == 0 ? interaction.createAction(player, interactor, interactorCard, interactee) : null;
            return this;
        }
        
        public PlayFieldType addInteraction()
        {
            if(this.icon == null || this.interactor == null || this.interactorCard == null || this.interactee == null || this.interaction == null)
            {
                new IllegalStateException("InteractionBuilder: Missing params or wrong order params!").printStackTrace();
                return PlayFieldType.this;
            }
            
            PlayFieldType.this.registerInteraction(this.icon, this.interactor, this.interactorCard, this.interactee, this.interaction);
            return PlayFieldType.this;
        }
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
        public final Predicate<DuelCard> interactorCard;
        public final Predicate<ZoneType> interactee;
        public final SingleZoneInteraction interaction;
        
        public InteractionEntry(ActionIcon icon, Predicate<ZoneType> interactor, Predicate<DuelCard> interactorCard, Predicate<ZoneType> interactee, SingleZoneInteraction interaction)
        {
            this.icon = icon;
            this.interactor = interactor;
            this.interactorCard = interactorCard;
            this.interactee = interactee;
            this.interaction = interaction;
        }
    }
    
    public static interface SingleZoneInteraction
    {
        public @Nullable Action createAction(ZoneOwner player, Zone interactor, @Nullable DuelCard interactorCard, Zone interactee);
    }
}
