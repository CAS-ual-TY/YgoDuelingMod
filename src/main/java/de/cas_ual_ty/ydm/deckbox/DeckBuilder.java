package de.cas_ual_ty.ydm.deckbox;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.rarity.Rarities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Supplier;

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
        main = new LinkedList<>();
        extra = new LinkedList<>();
        side = new LinkedList<>();
        mode = 0;
    }
    
    private LinkedList<Entry> getList()
    {
        switch(mode)
        {
            case MAIN_MODE:
                return main;
            case EXTRA_MODE:
                return extra;
            case SIDE_MODE:
                return side;
        }
        
        return null;
    }
    
    private void addEntry(Entry entry)
    {
        LinkedList<Entry> list = getList();
        
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
        setMode(DeckBuilder.MAIN_MODE);
        return this;
    }
    
    public DeckBuilder startExtraDeck()
    {
        setMode(DeckBuilder.EXTRA_MODE);
        return this;
    }
    
    public DeckBuilder startSideDeck()
    {
        setMode(DeckBuilder.SIDE_MODE);
        return this;
    }
    
    public DeckBuilder end()
    {
        setMode(-1);
        return this;
    }
    
    public DeckBuilder id(long id)
    {
        return id(id, (byte) 0);
    }
    
    public DeckBuilder id(long id, byte imageIndex)
    {
        addEntry(new IdEntry(id, imageIndex));
        return this;
    }
    
    public DeckBuilder name(String name)
    {
        return name(name, (byte) 0);
    }
    
    public DeckBuilder name(String name, byte imageIndex)
    {
        addEntry(new NameEntry(name, imageIndex));
        return this;
    }
    
    public DeckBuilder repeat()
    {
        LinkedList<Entry> list = getList();
        
        if(list != null)
        {
            list.addLast(list.getLast());
        }
        
        return this;
    }
    
    private static HashSet<String> sentErrors = new HashSet<>();
    
    public Supplier<DeckHolder> build()
    {
        if(mode >= 0)
        {
            end();
        }
        
        return () ->
        {
            DeckHolder deck = new DeckHolder();
            
            CardHolder card;
            
            LinkedList<Entry> errors = new LinkedList<>();
            
            for(Entry s : main)
            {
                card = s.get();
                
                if(card != null)
                {
                    deck.getMainDeck().add(card);
                }
                else
                {
                    errors.add(s);
                }
            }
            
            for(Entry s : extra)
            {
                card = s.get();
                
                if(card != null)
                {
                    deck.getExtraDeck().add(card);
                }
                else
                {
                    errors.add(s);
                }
            }
            
            for(Entry s : side)
            {
                card = s.get();
                
                if(card != null)
                {
                    deck.getSideDeck().add(card);
                }
                else
                {
                    errors.add(s);
                }
            }
            
            for(Entry e : errors)
            {
                if(!sentErrors.contains(e.getErrorString()))
                {
                    sentErrors.add(e.getErrorString());
                    YDM.log("Deck Builder Entry gives null: " + e.getErrorString());
                    //new NullPointerException().printStackTrace();
                }
            }
            
            return deck;
        };
    }
    
    private abstract static class Entry implements Supplier<CardHolder>
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
            this(id, (byte) 0);
        }
        
        @Override
        public CardHolder get()
        {
            Properties p = YdmDatabase.PROPERTIES_LIST.getFirst((c) -> c.getId() == id);
            
            if(p == null)
            {
                return null;
            }
            
            return new CardHolder(p, imageIndex, Rarities.CREATIVE.name);
        }
        
        @Override
        public String getErrorString()
        {
            return "Id Entry (id, image index): " + id + " " + imageIndex;
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
            Properties p = YdmDatabase.PROPERTIES_LIST.getFirst((c) -> c.getName().equals(name));
            
            if(p == null)
            {
                return null;
            }
            
            return new CardHolder(p, imageIndex, Rarities.CREATIVE.name);
        }
        
        @Override
        public String getErrorString()
        {
            return "Name Entry (name, image index): " + name + " " + imageIndex;
        }
    }
}
