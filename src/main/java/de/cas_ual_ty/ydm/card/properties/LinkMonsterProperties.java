package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class LinkMonsterProperties extends MonsterProperties
{
    public byte linkRating;
    public List<LinkArrow> linkArrows;
    
    public LinkMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        readLinkMonsterProperties(j);
    }
    
    public LinkMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof LinkMonsterProperties)
        {
            LinkMonsterProperties p1 = (LinkMonsterProperties) p0;
            linkRating = p1.linkRating;
            linkArrows = p1.linkArrows;
        }
    }
    
    public LinkMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        readLinkMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        writeLinkProperties(j);
    }
    
    public void readLinkMonsterProperties(JsonObject j)
    {
        linkRating = j.get(JsonKeys.LINK_RATING).getAsByte();
        
        JsonArray linkArrows = j.get(JsonKeys.LINK_ARROWS).getAsJsonArray();
        this.linkArrows = new ArrayList<>(linkArrows.size());
        for(JsonElement linkArrow : linkArrows)
        {
            this.linkArrows.add(LinkArrow.fromString(linkArrow.getAsString()));
        }
    }
    
    public void writeLinkProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.LINK_RATING, linkRating);
        
        JsonArray linkArrows = new JsonArray();
        for(LinkArrow linkArrow : this.linkArrows)
        {
            linkArrows.add(linkArrow.name);
        }
        j.add(JsonKeys.LINK_ARROWS, linkArrows);
    }
    
    @Override
    public void addMonsterHeader2(List<Component> list)
    {
        list.add(Component.literal(getAtk() + " ATK / LINK-" + getLinkRating()));
    }
    
    @Override
    public void addText(List<Component> list)
    {
        addLinkMarkers(list);
        list.add(Component.empty());
        super.addText(list);
    }
    
    public void addLinkMarkers(List<Component> list)
    {
        //        list.add(this.linkArrows.stream().map((arrow) -> arrow.name).collect(Collectors.joining(", ")));
        list.addAll(LinkArrow.buildSymbolsString(getLinkArrows(), ChatFormatting.DARK_GRAY, ChatFormatting.RED, "  "));
    }
    
    // --- Getters ---
    
    public byte getLinkRating()
    {
        return linkRating;
    }
    
    public List<LinkArrow> getLinkArrows()
    {
        return linkArrows;
    }
}
