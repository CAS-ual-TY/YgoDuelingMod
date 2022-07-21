package de.cas_ual_ty.ydm.set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CompositionCardPuller extends CardPuller
{
    public final List<String> subSetCodes;
    protected List<CardSet> subSets;
    
    public CompositionCardPuller(JsonObject setJson, CardSet set) throws IllegalArgumentException
    {
        super(setJson, set);
        
        JsonArray subSetsJson = setJson.get(JsonKeys.SUB_SETS).getAsJsonArray();
        
        subSetCodes = new ArrayList<>(subSetsJson.size());
        subSets = null;
        
        for(int i = 0; i < subSetsJson.size(); ++i)
        {
            subSetCodes.add(subSetsJson.get(i).getAsString());
        }
    }
    
    @Override
    public void postDBInit()
    {
        super.postDBInit();
        linkSubSets();
    }
    
    public void linkSubSets()
    {
        if(subSets == null)
        {
            subSets = new ArrayList<>(subSetCodes.size());
            
            CardSet subSet;
            for(String code : subSetCodes)
            {
                subSet = YdmDatabase.SETS_LIST.get(code);
                
                if(subSet == null)
                {
                    YDM.log("Can not find sub-set: " + code + " in set: " + set.code + " (" + set.name + ")");
                }
                else
                {
                    subSets.add(subSet);
                }
            }
        }
    }
    
    @Override
    public List<ItemStack> open(Random random)
    {
        List<ItemStack> list = new ArrayList<>(0);
        
        for(CardSet subSet : subSets)
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
    public void addInformation(List<ITextComponent> tooltip)
    {
        if(addInformationInComposition())
        {
            for(CardSet subSet : subSets)
            {
                if(subSet.isIndependentAndItem())
                {
                    tooltip.add(new StringTextComponent(subSet.name));
                }
                else
                {
                    subSet.pull.addInformation(tooltip);
                }
            }
        }
    }
    
    @Override
    public boolean addInformationInComposition()
    {
        for(CardSet subSet : subSets)
        {
            if(!subSet.pull.addInformationInComposition())
            {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void addAllCardEntries(SortedArraySet<CardHolder> sortedSet)
    {
        for(CardSet subSet : subSets)
        {
            subSet.addAllCardEntries(sortedSet);
        }
    }
}
