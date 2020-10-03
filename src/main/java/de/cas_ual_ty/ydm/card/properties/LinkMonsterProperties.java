package de.cas_ual_ty.ydm.card.properties;

import java.util.List;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class LinkMonsterProperties extends MonsterProperties
{
    public byte linkRating;
    public List<LinkArrow> linkArrows;
    
    public LinkMonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        this.readLinkMonsterProperties(j);
    }
    
    public LinkMonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof LinkMonsterProperties)
        {
            LinkMonsterProperties p1 = (LinkMonsterProperties)p0;
            this.linkRating = p1.linkRating;
            this.linkArrows = p1.linkArrows;
        }
    }
    
    public LinkMonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        this.readLinkMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        this.writeLinkProperties(j);
    }
    
    public void readLinkMonsterProperties(JsonObject j)
    {
        this.linkRating = j.get(JsonKeys.LINK_RATING).getAsByte();
        this.linkArrows = LinkArrow.fromShort(j.get(JsonKeys.LINK_ARROWS).getAsShort());
    }
    
    public void writeLinkProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.LINK_RATING, this.linkRating);
        j.addProperty(JsonKeys.LINK_ARROWS, LinkArrow.toShort(this.linkArrows));
    }
    
    @Override
    public void addMonsterHeader2(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.getAtk() + " ATK / LINK-" + this.getLinkRating()));
    }
    
    @Override
    public void addText(List<ITextComponent> list)
    {
        // TODO Link Marker Formatting and Colors
        this.addLinkMarkers(list);
        list.add(StringTextComponent.EMPTY);
        super.addText(list);
    }
    
    public void addLinkMarkers(List<ITextComponent> list)
    {
        //        list.add(this.linkArrows.stream().map((arrow) -> arrow.name).collect(Collectors.joining(", ")));
        list.addAll(LinkArrow.buildSymbolsString(this.getLinkArrows(), TextFormatting.WHITE, TextFormatting.RED, "  "));
    }
    
    // --- Getters ---
    
    public byte getLinkRating()
    {
        return this.linkRating;
    }
    
    public List<LinkArrow> getLinkArrows()
    {
        return this.linkArrows;
    }
}
