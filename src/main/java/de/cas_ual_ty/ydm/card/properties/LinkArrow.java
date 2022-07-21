package de.cas_ual_ty.ydm.card.properties;

import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.List;

public enum LinkArrow
{
    TOP_LEFT("Top-Left", 0, "◸", "◤"), TOP("Top", 1, "△", "▲"), TOP_RIGHT("Top-Right", 2, "◹", "◥"), RIGHT("Right", 3, "▷", "▶"), BOTTOM_RIGHT("Bottom-Right", 4, "◿", "◢"), BOTTOM("Bottom", 5, "▽", "▼"), BOTTOM_LEFT("Bottom-Left", 6, "◺", "◣"), LEFT("Left", 7, "◁", "◀");
    
    public final String name;
    public final int index;
    public final int number;
    
    private final String symbolUnactive;
    private final String symbolActive;
    
    LinkArrow(String name, int index, String textUnactive, String textActive)
    {
        this.name = name;
        this.index = index;
        number = YdmUtil.getPow2(index);
        symbolUnactive = textUnactive;
        symbolActive = textUnactive;
    }
    
    public boolean isContainedIn(short linkNumber)
    {
        return (linkNumber % YdmUtil.getPow2(index + 1)) >= number;
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
    
    public static List<ITextComponent> buildSymbolsString(List<LinkArrow> arrows, TextFormatting unactive, TextFormatting active, String joiner)
    {
        LinkArrow arrow;
        List<ITextComponent> list = new ArrayList<>(3);
        
        // Top row
        
        IFormattableTextComponent s = new StringTextComponent("");
        
        for(int i = 0; i < 3; ++i)
        {
            arrow = LinkArrow.VALUES[i];
            
            if(arrows.contains(arrow))
            {
                s.append(new StringTextComponent(arrow.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
            }
            else
            {
                s.append(new StringTextComponent(arrow.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
            }
            
            if(i < 2)
            {
                s.append(joiner);
            }
        }
        
        list.add(s);
        
        // Middle row
        
        s = new StringTextComponent("");
        
        if(arrows.contains(LEFT))
        {
            s.append(new StringTextComponent(LEFT.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
        }
        else
        {
            s.append(new StringTextComponent(LEFT.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
        }
        
        s.append(joiner + "" + joiner);
        
        if(arrows.contains(RIGHT))
        {
            s.append(new StringTextComponent(RIGHT.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
        }
        else
        {
            s.append(new StringTextComponent(RIGHT.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
        }
        
        list.add(s);
        
        // Bottom row
        
        s = new StringTextComponent("");
        
        for(int i = 6; i > 3; --i)
        {
            arrow = LinkArrow.VALUES[i];
            
            if(arrows.contains(arrow))
            {
                s.append(new StringTextComponent(arrow.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
            }
            else
            {
                s.append(new StringTextComponent(arrow.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
            }
            
            if(i > 4)
            {
                s.append(joiner);
            }
        }
        
        list.add(s);
        
        return list;
    }
}
