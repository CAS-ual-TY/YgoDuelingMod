package de.cas_ual_ty.ydm.card.properties;

import java.util.List;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class MonsterProperties extends Properties
{
    public String attribute;
    public int atk;
    public String species;
    public MonsterType monsterType;
    public boolean isPendulum;
    public String ability;
    public boolean hasEffect;
    
    // Only if isPendulum = true
    public String pendulumText;
    public byte pendulumScaleLeftBlue;
    public byte pendulumScaleRightRed;
    
    public MonsterProperties(Properties p0, JsonObject j)
    {
        super(p0);
        this.readMonsterProperties(j);
    }
    
    public MonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof MonsterProperties)
        {
            MonsterProperties p1 = (MonsterProperties)p0;
            this.attribute = p1.attribute;
            this.atk = p1.atk;
            this.species = p1.species;
            this.monsterType = p1.monsterType;
            this.isPendulum = p1.isPendulum;
            this.ability = p1.ability;
            this.hasEffect = p1.hasEffect;
            
            if(p1.isPendulum)
            {
                this.pendulumText = p1.pendulumText;
                this.pendulumScaleLeftBlue = p1.pendulumScaleLeftBlue;
                this.pendulumScaleRightRed = p1.pendulumScaleRightRed;
            }
        }
    }
    
    public MonsterProperties()
    {
    }
    
    @Override
    public void readAllProperties(JsonObject j)
    {
        super.readAllProperties(j);
        this.readMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        this.writeMonsterProperties(j);
    }
    
    public void readMonsterProperties(JsonObject j)
    {
        this.attribute = j.get(JsonKeys.ATTRIBUTE).getAsString();
        this.atk = j.get(JsonKeys.ATK).getAsInt();
        this.species = j.get(JsonKeys.SPECIES).getAsString();
        this.monsterType = MonsterType.fromString(j.get(JsonKeys.MONSTER_TYPE).getAsString());
        this.isPendulum = j.get(JsonKeys.IS_PENDULUM).getAsBoolean();
        this.ability = j.get(JsonKeys.ABILITY).getAsString();
        this.hasEffect = j.get(JsonKeys.HAS_EFFECT).getAsBoolean();
        
        if(this.getIsPendulum())
        {
            this.pendulumText = j.get(JsonKeys.PENDULUM_TEXT).getAsString();
            this.pendulumScaleLeftBlue = j.get(JsonKeys.PENDULUM_SCALE_LEFT_BLUE).getAsByte();
            this.pendulumScaleRightRed = j.get(JsonKeys.PENDULUM_SCALE_RIGHT_RED).getAsByte();
        }
    }
    
    public void writeMonsterProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.ATTRIBUTE, this.attribute);
        j.addProperty(JsonKeys.ATK, this.atk);
        j.addProperty(JsonKeys.SPECIES, this.species);
        j.addProperty(JsonKeys.MONSTER_TYPE, this.monsterType.name);
        j.addProperty(JsonKeys.IS_PENDULUM, this.isPendulum);
        j.addProperty(JsonKeys.ABILITY, this.ability);
        j.addProperty(JsonKeys.HAS_EFFECT, this.hasEffect);
        
        if(this.getIsPendulum())
        {
            j.addProperty(JsonKeys.PENDULUM_TEXT, this.pendulumText);
            j.addProperty(JsonKeys.PENDULUM_SCALE_LEFT_BLUE, this.pendulumScaleLeftBlue);
            j.addProperty(JsonKeys.PENDULUM_SCALE_RIGHT_RED, this.pendulumScaleRightRed);
        }
    }
    
    public boolean getIsNormal()
    {
        return this.getMonsterType() == null && !this.getHasEffect();
    }
    
    public boolean getIsEffect()
    {
        return this.getMonsterType() == null && this.getHasEffect();
    }
    
    public boolean getIsFusion()
    {
        return this.getMonsterType() == MonsterType.FUSION;
    }
    
    public boolean getIsLink()
    {
        return this.getMonsterType() == MonsterType.LINK;
    }
    
    public boolean getIsRitual()
    {
        return this.getMonsterType() == MonsterType.RITUAL;
    }
    
    public boolean getIsSynchro()
    {
        return this.getMonsterType() == MonsterType.SYNCHRO;
    }
    
    public boolean getIsXyz()
    {
        return this.getMonsterType() == MonsterType.XYZ;
    }
    
    public boolean getHasLevel()
    {
        return this.getMonsterType() == null || this.getIsFusion() || this.getIsRitual() || this.getIsSynchro();
    }
    
    public boolean getHasDef()
    {
        return this.getMonsterType() == null || this.getIsFusion() || this.getIsRitual() || this.getIsSynchro() || this.getIsXyz();
    }
    
    @Override
    public void addHeader(List<ITextComponent> list)
    {
        super.addHeader(list);
        this.addMonsterHeader(list);
    }
    
    @Override
    public void addText(List<ITextComponent> list)
    {
        if(this.getIsPendulum())
        {
            this.addPendulumTextHeader(list);
            list.add(new StringTextComponent(this.getPendulumText()));
            list.add(StringTextComponent.EMPTY);
        }
        this.addMonsterTextHeader(list);
        super.addText(list);
    }
    
    public void addPendulumTextHeader(List<ITextComponent> list)
    {
        // TODO Pendulum Text Header Formatting and Color
        IFormattableTextComponent leftScale = new StringTextComponent("" + this.getPendulumScaleLeftBlue());//.setStyle(Style.EMPTY.applyFormatting(TextFormatting.WHITE));
        IFormattableTextComponent leftArrow = new StringTextComponent("◀").setStyle(Style.EMPTY.applyFormatting(TextFormatting.BLUE));
        IFormattableTextComponent rightArrow = new StringTextComponent("▶").setStyle(Style.EMPTY.applyFormatting(TextFormatting.RED));
        IFormattableTextComponent rightScale = new StringTextComponent("" + this.getPendulumScaleRightRed());//.setStyle(Style.EMPTY.applyFormatting(TextFormatting.WHITE));
        list.add(leftScale.appendString(" ").append(leftArrow).appendString(" / ").append(rightArrow).appendString(" ").append(rightScale));
    }
    
    @Override
    public void addCardType(List<ITextComponent> list)
    {
        if(this.getMonsterType() != null)
        {
            list.add(new StringTextComponent(this.getMonsterType().name + " " + this.getType().name));
        }
        else if(this.getHasEffect())
        {
            list.add(new StringTextComponent("Effect " + this.getType().name));
        }
        else
        {
            list.add(new StringTextComponent("Normal " + this.getType().name));
        }
    }
    
    public void addMonsterHeader(List<ITextComponent> list)
    {
        this.addMonsterHeader1(list);
        this.addMonsterHeader2(list);
    }
    
    public void addMonsterHeader1(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.getAttribute()));
    }
    
    public void addMonsterHeader2(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(this.getAtk() + " ATK"));
    }
    
    public void addMonsterTextHeader(List<ITextComponent> list)
    {
        IFormattableTextComponent s = new StringTextComponent(this.getSpecies() + " / ");
        
        if(this.getMonsterType() != null)
        {
            s.appendString(this.getMonsterType().name + " / ");
        }
        
        if(this.getIsPendulum())
        {
            s.appendString("Pendulum" + " / ");
        }
        
        if(this.getAbility() != null)
        {
            s.appendString(this.getAbility() + " / ");
        }
        
        if(this.getHasEffect())
        {
            s.appendString("Effect");
        }
        else
        {
            s.appendString("Normal");
        }
        
        list.add(s);
    }
    
    // --- Getters ---
    
    public String getAttribute()
    {
        return this.attribute;
    }
    
    public int getAtk()
    {
        return this.atk;
    }
    
    public String getSpecies()
    {
        return this.species;
    }
    
    public MonsterType getMonsterType()
    {
        return this.monsterType;
    }
    
    public boolean getIsPendulum()
    {
        return this.isPendulum;
    }
    
    public String getAbility()
    {
        return this.ability;
    }
    
    public boolean getHasEffect()
    {
        return this.hasEffect;
    }
    
    public String getPendulumText()
    {
        return this.pendulumText;
    }
    
    public byte getPendulumScaleLeftBlue()
    {
        return this.pendulumScaleLeftBlue;
    }
    
    public byte getPendulumScaleRightRed()
    {
        return this.pendulumScaleRightRed;
    }
}
