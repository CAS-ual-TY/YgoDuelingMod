package de.cas_ual_ty.ydm.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.set.Distribution.Pull;
import de.cas_ual_ty.ydm.set.Distribution.Pull.PullEntry;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class DistributionCardPuller extends CardPuller
{
    public final Distribution distribution;
    
    public DistributionCardPuller(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        super(setJson, set);
        
        String distributionName = setJson.get(JsonKeys.DISTRIBUTION).getAsString();
        
        this.distribution = YdmDatabase.DISTRIBUTIONS_LIST.get(distributionName);
        
        if(this.distribution == null)
        {
            throw new IllegalArgumentException("Cannot find distribution: " + distributionName);
        }
    }
    
    @Override
    public List<ItemStack> open(Random random)
    {
        List<ItemStack> list = new ArrayList<>();
        List<CardHolder> cards = this.openDistribution(random);
        
        for(CardHolder c : cards)
        {
            list.add(YdmItems.CARD.createItemForCardHolder(c));
        }
        
        return list;
    }
    
    public List<CardHolder> openDistribution(Random random)
    {
        Pull pull = this.choosePull(random);
        
        if(pull != null)
        {
            List<CardHolder> cards = new ArrayList<>();
            
            List<CardHolder> cardPool;
            
            for(PullEntry pe : pull.pullEntries)
            {
                cardPool = this.makeCardPool(pe);
                
                if(cardPool.size() <= 0)
                {
                    continue;
                }
                
                this.chooseCardsFromPool(random, cardPool, pe, cards);
            }
            
            return cards;
        }
        
        return null;
    }
    
    protected Pull choosePull(Random random)
    {
        int weightViewed = 0;
        int x = random.nextInt(this.distribution.totalWeight);
        
        for(Pull p : this.distribution.pulls)
        {
            if(weightViewed + p.weight > x)
            {
                return p;
            }
            
            weightViewed += p.weight;
        }
        
        return null;
    }
    
    protected List<CardHolder> makeCardPool(PullEntry pe)
    {
        List<CardHolder> cardPool = new ArrayList<>(this.set.cards.size() * pe.rarities.length);
        
        for(String rarity : pe.rarities)
        {
            for(CardHolder c : this.set.cards)
            {
                if(c.getRarity().equals(rarity))
                {
                    cardPool.add(c);
                }
            }
        }
        
        cardPool.sort((ch1, ch2) -> Long.compare(ch1.getCard().getId(), ch2.getCard().getId()));
        
        return cardPool;
    }
    
    protected void chooseCardsFromPool(Random random, List<CardHolder> cardPool, PullEntry pe, List<CardHolder> cards)
    {
        // Not enough cards -> we just add all of them
        if(cardPool.size() < pe.count)
        {
            cards.addAll(cardPool);
            return;
        }
        
        // We try to avoid including the same card twice, even with different rarities
        
        // First we count the amount of unique cards
        // (so the same card but with different rarities only counts once)
        // The cardPool list is sorted by card IDs
        int uniqueCards = DistributionCardPuller.countUniqueCards(cardPool);
        
        // Now we check if there enough unique cards
        if(uniqueCards >= pe.count)
        {
            List<CardHolder> cardPoolCopy = new ArrayList<>(cardPool.size());
            cardPoolCopy.addAll(cardPool);
            
            for(int i = 0; i < pe.count; ++i)
            {
                CardHolder ch = cardPoolCopy.get(random.nextInt(cardPoolCopy.size()));
                cardPoolCopy.removeIf((ch2) -> ch.getCard() == ch2.getCard());
                cards.add(ch);
            }
        }
        else // Otherwise we just randomly choose them, knowingly including duplicates
        {
            for(int i = 0; i < pe.count; ++i)
            {
                cards.add(cardPool.get(random.nextInt(cardPool.size())));
            }
        }
    }
    
    @Override
    public void addInformation(List<ITextComponent> tooltip)
    {
        this.distribution.addInformation(tooltip, this.set);
    }
    
    @Override
    public boolean addInformationInComposition()
    {
        return this.distribution.pulls.length == 1;
    }
    
    @Override
    public void logErrors()
    {
        for(Pull pull : this.distribution.pulls)
        {
            List<CardHolder> cardPool;
            
            for(PullEntry pe : pull.pullEntries)
            {
                if(pe.rarities.length <= 0 || pe.count <= 0)
                {
                    YDM.log("Set " + this.set.code + ": One pull entry has no rarities or count = 0 and will not do anything.");
                    continue;
                }
                
                cardPool = this.makeCardPool(pe);
                
                StringBuilder s = new StringBuilder();
                for(String rarity : pe.rarities)
                {
                    s.append(rarity + " / ");
                }
                s.delete(s.length() - 3, s.length());
                
                if(cardPool.size() < pe.count)
                {
                    YDM.log("Set " + this.set.code + ": Not enough cards for rarities: " + s.toString());
                    continue;
                }
                
                int uniqueCards = DistributionCardPuller.countUniqueCards(cardPool);
                
                if(uniqueCards < pe.count)
                {
                    YDM.log("Set " + this.set.code + ": Not enough unique cards for rarities (will contain duplicates): " + s.toString());
                }
            }
        }
        
        for(String rarity : this.set.rarityPool)
        {
            if(!this.distribution.pullableRarities.contains(rarity))
            {
                YDM.log("Set " + this.set.code + ": Rarity and the cards using it can never be pulled: " + rarity);
            }
        }
    }
    
    public static int countUniqueCards(List<CardHolder> list)
    {
        int uniqueCards = 0;
        Properties lastCard = null;
        
        for(CardHolder ch : list)
        {
            if(ch.getCard() != lastCard)
            {
                lastCard = ch.getCard();
                ++uniqueCards;
            }
        }
        
        return uniqueCards;
    }
}
