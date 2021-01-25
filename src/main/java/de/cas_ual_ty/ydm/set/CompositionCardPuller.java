package de.cas_ual_ty.ydm.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SortedArraySet;

public class CompositionCardPuller extends CardPuller
{
    public final List<String> subSetCodes;
    protected List<CardSet> subSets;
    
    public CompositionCardPuller(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        super(setJson, set);
        
        JsonArray subSetsJson = setJson.get(JsonKeys.SUB_SETS).getAsJsonArray();
        
        this.subSetCodes = new ArrayList<>(subSetsJson.size());
        this.subSets = null;
        
        for(int i = 0; i < subSetsJson.size(); ++i)
        {
            this.subSetCodes.add(subSetsJson.get(i).getAsString());
        }
    }
    
    @Override
    public void postDBInit()
    {
        super.postDBInit();
        this.linkSubSets();
    }
    
    public void linkSubSets()
    {
        if(this.subSets == null)
        {
            this.subSets = new ArrayList<>(this.subSetCodes.size());
            
            CardSet subSet;
            for(String code : this.subSetCodes)
            {
                subSet = YdmDatabase.SETS_LIST.get(code);
                
                if(subSet == null)
                {
                    YDM.log("Can not find sub-set: " + code + " in set:" + this.set.code + " (" + this.set.name + ")");
                }
                else
                {
                    this.subSets.add(subSet);
                }
            }
        }
    }
    
    @Override
    public List<ItemStack> open(Random random)
    {
        List<ItemStack> list = new ArrayList<>(0);
        
        for(CardSet subSet : this.subSets)
        {
            if(subSet.isIndependentAndItem())
            {
                list.add(YdmItems.SET.createItemForSet(subSet));
            }
            else
            {
                list.addAll(subSet.open(random));
            }
        }
        
        return list;
    }
    
    @Override
    public void addAllCardEntries(SortedArraySet<CardHolder> sortedSet)
    {
        for(CardSet subSet : this.subSets)
        {
            subSet.addAllCardEntries(sortedSet);
        }
    }
}
