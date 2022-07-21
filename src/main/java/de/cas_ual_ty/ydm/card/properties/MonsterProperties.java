package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.util.JsonKeys;
import net.minecraft.util.text.*;

import java.util.List;

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
        readMonsterProperties(j);
    }
    
    public MonsterProperties(Properties p0)
    {
        super(p0);
        
        if(p0 instanceof MonsterProperties)
        {
            MonsterProperties p1 = (MonsterProperties) p0;
            attribute = p1.attribute;
            atk = p1.atk;
            species = p1.species;
            monsterType = p1.monsterType;
            isPendulum = p1.isPendulum;
            ability = p1.ability;
            hasEffect = p1.hasEffect;
            
            if(p1.isPendulum)
            {
                pendulumText = p1.pendulumText;
                pendulumScaleLeftBlue = p1.pendulumScaleLeftBlue;
                pendulumScaleRightRed = p1.pendulumScaleRightRed;
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
        readMonsterProperties(j);
    }
    
    @Override
    public void writeAllProperties(JsonObject j)
    {
        super.writeAllProperties(j);
        writeMonsterProperties(j);
    }
    
    public void readMonsterProperties(JsonObject j)
    {
        attribute = j.get(JsonKeys.ATTRIBUTE).getAsString();
        atk = j.get(JsonKeys.ATK).getAsInt();
        species = j.get(JsonKeys.SPECIES).getAsString();
        monsterType = MonsterType.fromString(j.get(JsonKeys.MONSTER_TYPE).getAsString());
        isPendulum = j.get(JsonKeys.IS_PENDULUM).getAsBoolean();
        ability = j.get(JsonKeys.ABILITY).getAsString();
        hasEffect = j.get(JsonKeys.HAS_EFFECT).getAsBoolean();
        
        if(getIsPendulum())
        {
            pendulumText = j.get(JsonKeys.PENDULUM_TEXT).getAsString();
            pendulumScaleLeftBlue = j.get(JsonKeys.PENDULUM_SCALE_LEFT_BLUE).getAsByte();
            pendulumScaleRightRed = j.get(JsonKeys.PENDULUM_SCALE_RIGHT_RED).getAsByte();
        }
    }
    
    public void writeMonsterProperties(JsonObject j)
    {
        j.addProperty(JsonKeys.ATTRIBUTE, attribute);
        j.addProperty(JsonKeys.ATK, atk);
        j.addProperty(JsonKeys.SPECIES, species);
        j.addProperty(JsonKeys.MONSTER_TYPE, monsterType.name);
        j.addProperty(JsonKeys.IS_PENDULUM, isPendulum);
        j.addProperty(JsonKeys.ABILITY, ability);
        j.addProperty(JsonKeys.HAS_EFFECT, hasEffect);
        
        if(getIsPendulum())
        {
            j.addProperty(JsonKeys.PENDULUM_TEXT, pendulumText);
            j.addProperty(JsonKeys.PENDULUM_SCALE_LEFT_BLUE, pendulumScaleLeftBlue);
            j.addProperty(JsonKeys.PENDULUM_SCALE_RIGHT_RED, pendulumScaleRightRed);
        }
    }
    
    public boolean getIsNormal()
    {
        return getMonsterType() == null && !getHasEffect();
    }
    
    public boolean getIsEffect()
    {
        return getMonsterType() == null && getHasEffect();
    }
    
    public boolean getIsFusion()
    {
        return getMonsterType() == MonsterType.FUSION;
    }
    
    public boolean getIsLink()
    {
        return getMonsterType() == MonsterType.LINK;
    }
    
    public boolean getIsRitual()
    {
        return getMonsterType() == MonsterType.RITUAL;
    }
    
    public boolean getIsSynchro()
    {
        return getMonsterType() == MonsterType.SYNCHRO;
    }
    
    public boolean getIsXyz()
    {
        return getMonsterType() == MonsterType.XYZ;
    }
    
    public boolean getHasLevel()
    {
        return getMonsterType() == null || getIsFusion() || getIsRitual() || getIsSynchro();
    }
    
    public boolean getHasDef()
    {
        return getMonsterType() == null || getIsFusion() || getIsRitual() || getIsSynchro() || getIsXyz();
    }
    
    @Override
    public void addHeader(List<ITextComponent> list)
    {
        super.addHeader(list);
        addMonsterHeader(list);
    }
    
    @Override
    public void addText(List<ITextComponent> list)
    {
        if(getIsPendulum())
        {
            addPendulumTextHeader(list);
            list.add(new StringTextComponent(getPendulumText()));
            list.add(StringTextComponent.EMPTY);
        }
        addMonsterTextHeader(list);
        super.addText(list);
    }
    
    public void addPendulumTextHeader(List<ITextComponent> list)
    {
        IFormattableTextComponent leftScale = new StringTextComponent("" + getPendulumScaleLeftBlue());//.setStyle(Style.EMPTY.applyFormatting(TextFormatting.WHITE));
        IFormattableTextComponent leftArrow = new StringTextComponent("◀").setStyle(Style.EMPTY.applyFormat(TextFormatting.BLUE));
        IFormattableTextComponent rightArrow = new StringTextComponent("▶").setStyle(Style.EMPTY.applyFormat(TextFormatting.RED));
        IFormattableTextComponent rightScale = new StringTextComponent("" + getPendulumScaleRightRed());//.setStyle(Style.EMPTY.applyFormatting(TextFormatting.WHITE));
        list.add(leftScale.append(" ").append(leftArrow).append(" / ").append(rightArrow).append(" ").append(rightScale));
    }
    
    @Override
    public void addCardType(List<ITextComponent> list)
    {
        if(getMonsterType() != null)
        {
            list.add(new StringTextComponent(getMonsterType().name + " " + getType().name));
        }
        else if(getHasEffect())
        {
            list.add(new StringTextComponent("Effect " + getType().name));
        }
        else
        {
            list.add(new StringTextComponent("Normal " + getType().name));
        }
    }
    
    public void addMonsterHeader(List<ITextComponent> list)
    {
        addMonsterHeader1(list);
        addMonsterHeader2(list);
    }
    
    public void addMonsterHeader1(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(getAttribute()));
    }
    
    public void addMonsterHeader2(List<ITextComponent> list)
    {
        list.add(new StringTextComponent(getAtk() + " ATK"));
    }
    
    public void addMonsterTextHeader(List<ITextComponent> list)
    {
        IFormattableTextComponent s = new StringTextComponent(getSpecies() + " / ");
        
        if(getMonsterType() != null)
        {
            s.append(getMonsterType().name + " / ");
        }
        
        if(getIsPendulum())
        {
            s.append("Pendulum" + " / ");
        }
        
        if(getAbility() != null && !getAbility().isEmpty())
        {
            s.append(getAbility() + " / ");
        }
        
        if(getHasEffect())
        {
            s.append("Effect");
        }
        else
        {
            s.append("Normal");
        }
        
        list.add(s);
    }
    
    // --- Getters ---
    
    public String getAttribute()
    {
        return attribute;
    }
    
    public int getAtk()
    {
        return atk;
    }
    
    public String getSpecies()
    {
        return species;
    }
    
    public MonsterType getMonsterType()
    {
        return monsterType;
    }
    
    public boolean getIsPendulum()
    {
        return isPendulum;
    }
    
    public String getAbility()
    {
        return ability;
    }
    
    public boolean getHasEffect()
    {
        return hasEffect;
    }
    
    public String getPendulumText()
    {
        return pendulumText;
    }
    
    public byte getPendulumScaleLeftBlue()
    {
        return pendulumScaleLeftBlue;
    }
    
    public byte getPendulumScaleRightRed()
    {
        return pendulumScaleRightRed;
    }
}
