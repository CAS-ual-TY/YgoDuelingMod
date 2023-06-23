package de.cas_ual_ty.ydm.card.properties;

import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

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
    
    public static List<Component> buildSymbolsString(List<LinkArrow> arrows, ChatFormatting unactive, ChatFormatting active, String joiner)
    {
        LinkArrow arrow;
        List<Component> list = new ArrayList<>(3);
        
        // Top row
        
        MutableComponent s = Component.literal("");
        
        for(int i = 0; i < 3; ++i)
        {
            arrow = LinkArrow.VALUES[i];
            
            if(arrows.contains(arrow))
            {
                s.append(Component.literal(arrow.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
            }
            else
            {
                s.append(Component.literal(arrow.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
            }
            
            if(i < 2)
            {
                s.append(joiner);
            }
        }
        
        list.add(s);
        
        // Middle row
        
        s = Component.literal("");
        
        if(arrows.contains(LEFT))
        {
            s.append(Component.literal(LEFT.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
        }
        else
        {
            s.append(Component.literal(LEFT.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
        }
        
        s.append(joiner + "" + joiner);
        
        if(arrows.contains(RIGHT))
        {
            s.append(Component.literal(RIGHT.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
        }
        else
        {
            s.append(Component.literal(RIGHT.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
        }
        
        list.add(s);
        
        // Bottom row
        
        s = Component.literal("");
        
        for(int i = 6; i > 3; --i)
        {
            arrow = LinkArrow.VALUES[i];
            
            if(arrows.contains(arrow))
            {
                s.append(Component.literal(arrow.symbolActive).setStyle(Style.EMPTY.applyFormat(active)));
            }
            else
            {
                s.append(Component.literal(arrow.symbolUnactive).setStyle(Style.EMPTY.applyFormat(unactive)));
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
