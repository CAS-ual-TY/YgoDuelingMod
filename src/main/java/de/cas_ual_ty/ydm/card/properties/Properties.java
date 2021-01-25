package de.cas_ual_ty.ydm.card.properties;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class Properties
{
    public static final Properties DUMMY = new Properties()
    {
        @Override
        public String getImageName(byte imageIndex)
        {
            return "blanc_card";
        }
        
        @Override
        public void addCardType(List<ITextComponent> list)
        {
            
        }
    };
    
    static
    {
        Properties.DUMMY.isHardcoded = true;
        Properties.DUMMY.name = "Dummy";
        Properties.DUMMY.id = 0;
        Properties.DUMMY.isIllegal = false;
        Properties.DUMMY.isCustom = true;
        Properties.DUMMY.text = "This is a replacement card!";
        Properties.DUMMY.type = null;
        Properties.DUMMY.images = null;
    }
    
    public boolean isHardcoded;
    public String name;
    public long id;
    public boolean isIllegal;
    public boolean isCustom;
    public String text;
    public Type type;
    public String[] images;
    
    protected int imageIndicesAmt;
    
    public Properties(Properties p0)
    {
        this.isHardcoded = false;
        this.name = p0.name;
        this.id = p0.id;
        this.isIllegal = p0.isIllegal;
        this.isCustom = p0.isCustom;
        this.text = p0.text;
        this.type = p0.type;
        this.images = p0.images;
        this.imageIndicesAmt = this.images.length;
    }
    
    public Properties(JsonObject j)
    {
        this.isHardcoded = false;
        this.readAllProperties(j);
        this.imageIndicesAmt = 1;
    }
    
    public Properties()
    {
        this.isHardcoded = false;
        this.imageIndicesAmt = 1;
    }
    
    public void postDBInit()
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
        this.isIllegal = j.get(JsonKeys.IS_ILLEGAL).getAsBoolean();
        this.isCustom = j.get(JsonKeys.IS_CUSTOM).getAsBoolean();
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
        j.addProperty(JsonKeys.IS_ILLEGAL, this.isIllegal);
        j.addProperty(JsonKeys.IS_CUSTOM, this.isCustom);
        j.addProperty(JsonKeys.TEXT, this.text);
        j.addProperty(JsonKeys.TYPE, this.type.name);
        
        JsonArray images = new JsonArray();
        for(String image : this.images)
        {
            images.add(image);
        }
        j.add(JsonKeys.IMAGES, images);
    }
    
    public boolean getIsHardcoded()
    {
        return this.isHardcoded;
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
    
    public int getImageIndicesAmt()
    {
        return this.imageIndicesAmt;
    }
    
    public boolean isAcceptedImageIndex(byte imageIndex)
    {
        return imageIndex >= 0 && imageIndex < this.getImageIndicesAmt();
    }
    
    public byte adjustImageIndex(byte imageIndex)
    {
        if(!this.isAcceptedImageIndex(imageIndex))
        {
            return 0;
        }
        else
        {
            return imageIndex;
        }
    }
    
    public String getImageURL(byte imageIndex)
    {
        return this.getImages()[this.adjustImageIndex(imageIndex)];
    }
    
    public String getImageName(byte imageIndex)
    {
        return this.getId() + "_" + this.adjustImageIndex(imageIndex);
    }
    
    public String getInfoImageName(byte imageIndex)
    {
        return YDM.proxy.addCardInfoTag(this.getImageName(imageIndex));
    }
    
    public String getItemImageName(byte imageIndex)
    {
        return YDM.proxy.addCardItemTag(this.getImageName(imageIndex));
    }
    
    public String getMainImageName(byte imageIndex)
    {
        return YDM.proxy.addCardMainTag(this.getImageName(imageIndex));
    }
    
    public ResourceLocation getInfoImageResourceLocation(byte imageIndex)
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getCardInfoReplacementImage(this, this.adjustImageIndex(imageIndex)) + ".png");
    }
    
    public ResourceLocation getItemImageResourceLocation(byte imageIndex)
    {
        return new ResourceLocation(YDM.MOD_ID, "item/" + this.getItemImageName(imageIndex));
    }
    
    public ResourceLocation getMainImageResourceLocation(byte imageIndex)
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + YDM.proxy.getCardMainReplacementImage(this, this.adjustImageIndex(imageIndex)) + ".png");
    }
    
    public void addInformation(List<ITextComponent> list)
    {
        this.addHeader(list);
        list.add(StringTextComponent.EMPTY);
        this.addText(list);
    }
    
    public void addHeader(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.getName()));
        
        if(this.isCustom)
        {
            list.add(new StringTextComponent("Custom Card").setStyle(Style.EMPTY.applyFormatting(TextFormatting.RED)));
        }
        
        list.add(StringTextComponent.EMPTY);
        this.addCardType(list);
    }
    
    public void addText(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.getText()));
    }
    
    public void addCardType(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.type.name));
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
        return this.isIllegal;
    }
    
    public boolean getCustom()
    {
        return this.isCustom;
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
