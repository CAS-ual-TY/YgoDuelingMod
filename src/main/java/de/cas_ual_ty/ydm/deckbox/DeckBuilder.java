package de.cas_ual_ty.ydm.deckbox;

import java.util.LinkedList;
import java.util.function.Supplier;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.Rarity;

public class DeckBuilder
{
    private static final int MAIN_MODE = 1;
    private static final int EXTRA_MODE = 2;
    private static final int SIDE_MODE = 3;
    
    private LinkedList<Entry> main;
    private LinkedList<Entry> extra;
    private LinkedList<Entry> side;
    private int mode;
    
    public DeckBuilder()
    {
        this.main = new LinkedList<>();
        this.extra = new LinkedList<>();
        this.side = new LinkedList<>();
        this.mode = 0;
    }
    
    private LinkedList<Entry> getList()
    {
        switch(this.mode)
        {
            case MAIN_MODE:
                return this.main;
            case EXTRA_MODE:
                return this.extra;
            case SIDE_MODE:
                return this.side;
        }
        
        return null;
    }
    
    private void addEntry(Entry entry)
    {
        LinkedList<Entry> list = this.getList();
        
        if(list != null)
        {
            list.addLast(entry);
        }
    }
    
    private void setMode(int mode)
    {
        if(this.mode >= 0)
        {
            this.mode = mode;
        }
    }
    
    public DeckBuilder startMainDeck()
    {
        this.setMode(DeckBuilder.MAIN_MODE);
        return this;
    }
    
    public DeckBuilder startExtraDeck()
    {
        this.setMode(DeckBuilder.EXTRA_MODE);
        return this;
    }
    
    public DeckBuilder startSideDeck()
    {
        this.setMode(DeckBuilder.SIDE_MODE);
        return this;
    }
    
    public DeckBuilder end()
    {
        this.setMode(-1);
        return this;
    }
    
    public DeckBuilder id(long id)
    {
        return this.id(id, (byte)0);
    }
    
    public DeckBuilder id(long id, byte imageIndex)
    {
        this.addEntry(new IdEntry(id, imageIndex));
        return this;
    }
    
    public DeckBuilder name(String name)
    {
        return this.name(name, (byte)0);
    }
    
    public DeckBuilder name(String name, byte imageIndex)
    {
        this.addEntry(new NameEntry(name, imageIndex));
        return this;
    }
    
    public DeckBuilder repeat()
    {
        LinkedList<Entry> list = this.getList();
        
        if(list != null)
        {
            list.addLast(list.getLast());
        }
        
        return this;
    }
    
    public Supplier<DeckHolder> build()
    {
        if(this.mode >= 0)
        {
            this.end();
        }
        
        return () ->
        {
            DeckHolder deck = new DeckHolder();
            
            CardHolder card;
            
            for(Entry s : this.main)
            {
                card = s.get();
                
                if(card != null)
                {
                    deck.getMainDeck().add(card);
                }
                else
                {
                    YDM.log("Deck Builder Entry gives null: " + s.getErrorString());
                }
            }
            
            for(Entry s : this.extra)
            {
                card = s.get();
                
                if(card != null)
                {
                    deck.getExtraDeck().add(card);
                }
                else
                {
                    YDM.log("Deck Builder Entry gives null: " + s.getErrorString());
                }
            }
            
            for(Entry s : this.side)
            {
                card = s.get();
                
                if(card != null)
                {
                    deck.getSideDeck().add(card);
                }
                else
                {
                    YDM.log("Deck Builder Entry gives null: " + s.getErrorString());
                }
            }
            
            return deck;
        };
    }
    
    private static abstract class Entry implements Supplier<CardHolder>
    {
        public abstract String getErrorString();
    }
    
    private static class IdEntry extends Entry
    {
        private final long id;
        private final byte imageIndex;
        
        public IdEntry(long id, byte imageIndex)
        {
            this.id = id;
            this.imageIndex = imageIndex;
        }
        
        public IdEntry(long id)
        {
            this(id, (byte)0);
        }
        
        @Override
        public CardHolder get()
        {
            return new CardHolder(YdmDatabase.PROPERTIES_LIST.getFirst((c) -> c.getId() == this.id), this.imageIndex, Rarity.CREATIVE.name);
        }
        
        @Override
        public String getErrorString()
        {
            return "Id Entry (id, image index): " + this.id + " " + this.imageIndex;
        }
    }
    
    private static class NameEntry extends Entry
    {
        private final String name;
        private final byte imageIndex;
        
        public NameEntry(String name, byte imageIndex)
        {
            this.name = name;
            this.imageIndex = imageIndex;
        }
        
        @Override
        public CardHolder get()
        {
            return new CardHolder(YdmDatabase.PROPERTIES_LIST.getFirst((c) -> c.getName().equals(this.name)), this.imageIndex, Rarity.CREATIVE.name);
        }
        
        @Override
        public String getErrorString()
        {
            return "Name Entry (name, image index): " + this.name + " " + this.imageIndex;
        }
    }
}
