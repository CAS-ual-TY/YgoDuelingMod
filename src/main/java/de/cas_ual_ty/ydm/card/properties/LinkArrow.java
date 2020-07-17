package de.cas_ual_ty.ydm.card.properties;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.util.YDMUtil;

public enum LinkArrow
{
    TOP_LEFT("Top-Left", 0), TOP("Top", 1), TOP_RIGHT("Top-Right", 2), RIGHT("Right", 3), BOTTOM_RIGHT("Bottom-Right", 4), BOTTOM("Bottom", 5), BOTTOM_LEFT("Bottom-Left", 6), LEFT("Left", 7);
    
    public final String name;
    public final int index;
    public final int number;
    
    private LinkArrow(String name, int index)
    {
        this.name = name;
        this.index = index;
        this.number = YDMUtil.getPow2(index);
    }
    
    public boolean isContainedIn(short linkNumber)
    {
        return (linkNumber % YDMUtil.getPow2(this.index + 1)) >= this.number;
    }
    
    public static final LinkArrow[] VALUES = LinkArrow.values();
    
    public static LinkArrow fromString(String s)
    {
        for(LinkArrow m : LinkArrow.VALUES)
        {
            if(m.name.equals(s))
            {
                return m;
            }
        }
        
        return null;
    }
    
    public static List<LinkArrow> fromShort(short sum)
    {
        if(sum == 0)
        {
            return new ArrayList<>(0);
        }
        else
        {
            List<LinkArrow> list = new ArrayList<>(1);
            
            LinkArrow linkArrow;
            for(int i = LinkArrow.VALUES.length - 1; i >= 0; --i)
            {
                linkArrow = LinkArrow.VALUES[i];
                if(sum >= linkArrow.number)
                {
                    list.add(0, linkArrow);
                    sum %= linkArrow.number;
                }
            }
            
            return list;
        }
    }
    
    public static short toShort(List<LinkArrow> linkArrows)
    {
        short sum = 0;
        
        for(LinkArrow linkArrow : linkArrows)
        {
            sum += linkArrow.number;
        }
        
        return sum;
    }
}
