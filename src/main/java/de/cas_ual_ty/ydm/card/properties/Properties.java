package de.cas_ual_ty.ydm.card.properties;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.clientutil.ImageHandler;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class Properties
{
    public String name;
    public long id;
    public boolean illegal;
    public boolean custom;
    public String text;
    public Type type;
    public String[] images;
    
    public Properties(Properties p0)
    {
        this.name = p0.name;
        this.id = p0.id;
        this.illegal = p0.illegal;
        this.custom = p0.custom;
        this.text = p0.text;
        this.type = p0.type;
        this.images = p0.images;
    }
    
    public Properties(JsonObject j)
    {
        this.readAllProperties(j);
    }
    
    public Properties()
    {
    }
    
    public void readAllProperties(JsonObject j)
    {
        this.readProperties(j);
    }
    
    public void writeAllProperties(JsonObject j)
    {
        this.writeProperties(j);
    }
    
    public void readProperties(JsonObject j)
    {
        this.name = j.get(JsonKeys.NAME).getAsString();
        this.id = j.get(JsonKeys.ID).getAsLong();
        this.illegal = j.get(JsonKeys.ILLEGAL).getAsBoolean();
        this.custom = j.get(JsonKeys.CUSTOM).getAsBoolean();
        this.text = j.get(JsonKeys.TEXT).getAsString();
        this.type = Type.fromString(j.get(JsonKeys.TYPE).getAsString());
        
        JsonArray images = j.get(JsonKeys.IMAGES).getAsJsonArray();
        this.images = new String[images.size()];
        for(int i = 0; i < this.images.length; ++i)
        {
            this.images[i] = images.get(i).getAsString();
        }
    }
    
    public void writeProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.NAME, this.name);
        j.addProperty(JsonKeys.ID, this.id);
        j.addProperty(JsonKeys.ILLEGAL, this.illegal);
        j.addProperty(JsonKeys.CUSTOM, this.custom);
        j.addProperty(JsonKeys.TEXT, this.text);
        j.addProperty(JsonKeys.TYPE, this.type.name);
        
        JsonArray images = new JsonArray();
        for(String image : this.images)
        {
            images.add(image);
        }
        j.add(JsonKeys.IMAGES, images);
    }
    
    public boolean getIsSpell()
    {
        return this.getType() == Type.SPELL;
    }
    
    public boolean getIsTrap()
    {
        return this.getType() == Type.TRAP;
    }
    
    public boolean getIsMonster()
    {
        return this.getType() == Type.MONSTER;
    }
    
    public String getImageURL(byte imageIndex)
    {
        return this.getImages()[imageIndex];
    }
    
    public String getImageName(byte imageIndex)
    {
        return this.getId() + "_" + imageIndex;
    }
    
    public String getInfoImageName(byte imageIndex)
    {
        return ImageHandler.addInfoSuffix(this.getImageName(imageIndex));
    }
    
    public String getItemImageName(byte imageIndex)
    {
        return ImageHandler.addItemSuffix(this.getImageName(imageIndex));
    }
    
    public String getMainImageName(byte imageIndex)
    {
        return ImageHandler.addMainSuffix(this.getImageName(imageIndex));
    }
    
    public ResourceLocation getInfoImageResourceLocation(byte imageIndex)
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ImageHandler.getInfoReplacementImage(this, imageIndex) + ".png");
    }
    
    public ResourceLocation getItemImageResourceLocation(byte imageIndex)
    {
        return new ResourceLocation(YDM.MOD_ID, "item/" + this.getItemImageName(imageIndex));
    }
    
    public ResourceLocation getMainImageResourceLocation(byte imageIndex)
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + ImageHandler.getMainReplacementImage(this, imageIndex) + ".png");
    }
    
    public void addInformation(List<ITextComponent> list)
    {
        List<String> raw = this.getRawStringList();
        
        for(String s1 : raw)
        {
            for(String s2 : s1.split("\n"))
            {
                list.add(new StringTextComponent(s2));
            }
        }
    }
    
    public List<String> getRawStringList()
    {
        List<String> list = new LinkedList<>();
        
        this.addHeader(list);
        list.add("");
        this.addText(list);
        
        return list;
    }
    
    public void addHeader(List<String> list)
    {
        list.add(this.getName());
        
        if(this.custom)
        {
            ITextComponent c = new StringTextComponent("Custom Card");
            c.getStyle().setColor(TextFormatting.RED);
            list.add(c.getFormattedText());
        }
        
        list.add("");
        this.addCardType(list);
    }
    
    public void addText(List<String> list)
    {
        list.add(this.getText());
    }
    
    public void addCardType(List<String> list)
    {
        list.add(this.type.name);
    }
    
    // --- Getters ---
    
    public String getName()
    {
        return this.name;
    }
    
    public long getId()
    {
        return this.id;
    }
    
    public boolean getIllegal()
    {
        return this.illegal;
    }
    
    public boolean getCustom()
    {
        return this.custom;
    }
    
    public String getText()
    {
        return this.text;
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    public String[] getImages()
    {
        return this.images;
    }
}
