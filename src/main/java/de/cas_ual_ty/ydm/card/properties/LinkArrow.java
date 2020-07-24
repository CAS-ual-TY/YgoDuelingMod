package de.cas_ual_ty.ydm.card.properties;

import java.util.ArrayList;
import java.util.List;

import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public enum LinkArrow
{
    TOP_LEFT("Top-Left", 0, "◸", "◤"), TOP("Top", 1, "△", "▲"), TOP_RIGHT("Top-Right", 2, "◹", "◥"), RIGHT("Right", 3, "▷", "▶"), BOTTOM_RIGHT("Bottom-Right", 4, "◿", "◢"), BOTTOM("Bottom", 5, "▽", "▼"), BOTTOM_LEFT("Bottom-Left", 6, "◺", "◣"), LEFT("Left", 7, "◁", "◀");
    
    public final String name;
    public final int index;
    public final int number;
    
    private final String symbolUnactive;
    private final String symbolActive;
    
    private LinkArrow(String name, int index, String textUnactive, String textActive)
    {
        this.name = name;
        this.index = index;
        this.number = YdmUtil.getPow2(index);
        this.symbolUnactive = textUnactive;
        this.symbolActive = textUnactive;
    }
    
    public boolean isContainedIn(short linkNumber)
    {
        return (linkNumber % YdmUtil.getPow2(this.index + 1)) >= this.number;
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
    
    public static List<String> buildSymbolsString(List<LinkArrow> arrows, TextFormatting unactive, TextFormatting active, String joiner)
    {
        LinkArrow arrow;
        List<String> list = new ArrayList<>(3);
        
        ITextComponent c;
        
        // Top row
        
        String s = "";
        
        for(int i = 0; i < 3; ++i)
        {
            arrow = LinkArrow.VALUES[i];
            
            if(arrows.contains(arrow))
            {
                c = new StringTextComponent(arrow.symbolActive);
                c.getStyle().setColor(active);
                s += c.getFormattedText();
            }
            else
            {
                c = new StringTextComponent(arrow.symbolUnactive);
                c.getStyle().setColor(unactive);
                s += c.getFormattedText();
            }
            
            if(i < 2)
            {
                s += joiner;
            }
        }
        
        list.add(s);
        
        // Middle row
        
        s = "";
        
        if(arrows.contains(LEFT))
        {
            c = new StringTextComponent(LEFT.symbolActive);
            c.getStyle().setColor(active);
            s += c.getFormattedText();
        }
        else
        {
            c = new StringTextComponent(LEFT.symbolUnactive);
            c.getStyle().setColor(unactive);
            s += c.getFormattedText();
        }
        
        s += joiner + " " + joiner;
        
        if(arrows.contains(RIGHT))
        {
            c = new StringTextComponent(RIGHT.symbolActive);
            c.getStyle().setColor(active);
            s += c.getFormattedText();
        }
        else
        {
            c = new StringTextComponent(RIGHT.symbolUnactive);
            c.getStyle().setColor(unactive);
            s += c.getFormattedText();
        }
        
        list.add(s);
        
        // Bottom row
        
        s = "";
        
        for(int i = 6; i > 3; --i)
        {
            arrow = LinkArrow.VALUES[i];
            
            if(arrows.contains(arrow))
            {
                c = new StringTextComponent(arrow.symbolActive);
                c.getStyle().setColor(active);
                s += c.getFormattedText();
            }
            else
            {
                c = new StringTextComponent(arrow.symbolUnactive);
                c.getStyle().setColor(unactive);
                s += c.getFormattedText();
            }
            
            if(i > 4)
            {
                s += joiner;
            }
        }
        
        list.add(s);
        
        return list;
    }
}
