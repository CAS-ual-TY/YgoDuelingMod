package de.cas_ual_ty.ydm.card.properties;

import com.google.gson.JsonObject;

import de.cas_ual_ty.ydm.util.JsonKeys;

public class MonsterProperties extends Properties
{
    public Attribute attribute;
    public int atk;
    public Species species;
    public MonsterType monsterType;
    public boolean isPendulum;
    public Ability ability;
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
        this.attribute = Attribute.fromString(j.get(JsonKeys.ATTRIBUTE).getAsString());
        this.atk = j.get(JsonKeys.ATK).getAsInt();
        this.species = Species.fromString(j.get(JsonKeys.SPECIES).getAsString());
        this.monsterType = MonsterType.fromString(j.get(JsonKeys.MONSTER_TYPE).getAsString());
        this.isPendulum = j.get(JsonKeys.IS_PENDULUM).getAsBoolean();
        this.ability = Ability.fromString(j.get(JsonKeys.ABILITY).getAsString());
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
        j.addProperty(JsonKeys.ATTRIBUTE, this.attribute.name);
        j.addProperty(JsonKeys.ATK, this.atk);
        j.addProperty(JsonKeys.SPECIES, this.species.name);
        j.addProperty(JsonKeys.MONSTER_TYPE, this.monsterType.name);
        j.addProperty(JsonKeys.IS_PENDULUM, this.isPendulum);
        j.addProperty(JsonKeys.ABILITY, this.ability.name);
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
        return this.getType() == null || this.getIsFusion() || this.getIsRitual() || this.getIsSynchro();
    }
    
    // --- Getters ---
    
    public Attribute getAttribute()
    {
        return this.attribute;
    }
    
    public int getAtk()
    {
        return this.atk;
    }
    
    public Species getSpecies()
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
    
    public Ability getAbility()
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
