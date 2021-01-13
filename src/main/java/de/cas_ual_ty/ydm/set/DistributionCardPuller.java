package de.cas_ual_ty.ydm.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.set.Distribution.Pull;
import de.cas_ual_ty.ydm.set.Distribution.Pull.PullEntry;
import de.cas_ual_ty.ydm.util.JsonKeys;
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
    public List<CardHolder> open(Random random)
    {
        int weightViewed = 0;
        int x = random.nextInt(this.distribution.totalWeight);
        
        Pull pull = null;
        
        for(Pull p : this.distribution.pulls)
        {
            if(weightViewed + p.weight > x)
            {
                pull = p;
                break;
            }
            
            weightViewed += p.weight;
        }
        
        if(pull != null)
        {
            ArrayList<CardHolder> cards = new ArrayList<>();
            
            ArrayList<CardHolder> cardPool;
            int i;
            
            for(PullEntry pe : pull.pullEntries)
            {
                cardPool = new ArrayList<>(this.set.cards.size() * pe.rarities.length);
                
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
                
                if(cardPool.size() <= 0)
                {
                    continue;
                }
                
                for(i = 0; i < pe.count; ++i)
                {
                    cards.add(cardPool.get(random.nextInt(cardPool.size())));
                }
            }
            
            return cards;
        }
        
        return null;
    }
    
    @Override
    public void addInformation(List<ITextComponent> tooltip)
    {
        this.distribution.addInformation(tooltip);
    }
}
