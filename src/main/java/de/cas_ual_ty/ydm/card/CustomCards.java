package de.cas_ual_ty.ydm.card;

import com.google.common.collect.Lists;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.Attribute;
import de.cas_ual_ty.ydm.card.properties.LevelMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.LinkArrow;
import de.cas_ual_ty.ydm.card.properties.LinkMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.MonsterType;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.card.properties.Species;
import de.cas_ual_ty.ydm.card.properties.SpellProperties;
import de.cas_ual_ty.ydm.card.properties.SpellType;
import de.cas_ual_ty.ydm.card.properties.Type;
import de.cas_ual_ty.ydm.card.properties.XyzMonsterProperties;

public class CustomCards
{
    public static LevelMonsterProperties PATREON_001_PROPERTIES;
    public static CardHolder PATREON_001_CARD;
    
    public static LevelMonsterProperties PATREON_002_PROPERTIES;
    public static CardHolder PATREON_002_CARD;
    
    public static LevelMonsterProperties PATREON_003_PROPERTIES;
    public static CardHolder PATREON_003_CARD;
    
    public static LevelMonsterProperties PATREON_004_PROPERTIES;
    public static CardHolder PATREON_004_CARD;
    
    public static LevelMonsterProperties PATREON_005_PROPERTIES;
    public static CardHolder PATREON_005_CARD;
    
    public static SpellProperties PATREON_006_PROPERTIES;
    public static CardHolder PATREON_006_CARD;
    
    public static SpellProperties PATREON_007_PROPERTIES;
    public static CardHolder PATREON_007_CARD;
    
    public static LevelMonsterProperties PATREON_008_PROPERTIES;
    public static CardHolder PATREON_008_CARD;
    
    public static LinkMonsterProperties PATREON_009_PROPERTIES;
    public static CardHolder PATREON_009_CARD;
    
    public static XyzMonsterProperties PATREON_010_PROPERTIES;
    public static CardHolder PATREON_010_CARD;
    
    public static void createAndRegisterEverything()
    {
        CustomCards.PATREON_001_PROPERTIES = new LevelMonsterProperties();
        CustomCards.PATREON_001_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_001_PROPERTIES.name = "Creator of Darkness - Set";
        CustomCards.PATREON_001_PROPERTIES.id = 1;
        CustomCards.PATREON_001_PROPERTIES.isIllegal = true;
        CustomCards.PATREON_001_PROPERTIES.isCustom = true;
        CustomCards.PATREON_001_PROPERTIES.text = "Cannot be Normal Summoned/Set. Must be Special Summoned (from your hand) by tributing 3 monsters whose original names are \"The Wicked Avatar\", \"The Wicked Dreadroot\", and \"The Wicked Eraser\". This card's Special Summon cannot be negated. This card is unaffected by other cards and effects. When this card is summoned your opponent cannot activate cards and effects until the end of this turn. Halve the ATK and DEF of all monsters your opponent controls while this card is face-up on the field. This card's original ATK and DEF is equal to the highest ATK or DEF currently on the field. (If there is a tie, you get to choose.) This card gains ATK and DEF for each card in your opponent's GY x 100. When this card is sent to the GY (by battle or card effect) banish all other cards on the field ignoring their effects.";
        CustomCards.PATREON_001_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_001_PROPERTIES.images = null;
        CustomCards.PATREON_001_PROPERTIES.attribute = Attribute.DARK.name;
        CustomCards.PATREON_001_PROPERTIES.atk = -1;
        CustomCards.PATREON_001_PROPERTIES.species = Species.DESTROYER_GOD.name;
        CustomCards.PATREON_001_PROPERTIES.monsterType = null;
        CustomCards.PATREON_001_PROPERTIES.isPendulum = false;
        CustomCards.PATREON_001_PROPERTIES.ability = "";
        CustomCards.PATREON_001_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_001_PROPERTIES.def = -1;
        CustomCards.PATREON_001_PROPERTIES.level = 12;
        CustomCards.PATREON_001_PROPERTIES.isTuner = false;
        CustomCards.PATREON_001_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_001_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_001_PROPERTIES); // Wants Secret Rare
        
        CustomCards.PATREON_002_PROPERTIES = new LevelMonsterProperties();
        CustomCards.PATREON_002_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_002_PROPERTIES.name = "Onomic: Wanderer With Two Tails";
        CustomCards.PATREON_002_PROPERTIES.id = 2;
        CustomCards.PATREON_002_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_002_PROPERTIES.isCustom = true;
        CustomCards.PATREON_002_PROPERTIES.text = "Can not be Destroyed by card effect. If this card is Special summoned: You may target 1 card on the field destroy it. You can only use this effect of \"Onomic: Wander With Two Tails\" once per turn. This card gains 100 ATK for each fire monster in the Graveyard.";
        CustomCards.PATREON_002_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_002_PROPERTIES.images = null;
        CustomCards.PATREON_002_PROPERTIES.attribute = Attribute.FIRE.name;
        CustomCards.PATREON_002_PROPERTIES.atk = 1800;
        CustomCards.PATREON_002_PROPERTIES.species = Species.BEAST_WARRIOR.name;
        CustomCards.PATREON_002_PROPERTIES.monsterType = null;
        CustomCards.PATREON_002_PROPERTIES.isPendulum = false;
        CustomCards.PATREON_002_PROPERTIES.ability = "";
        CustomCards.PATREON_002_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_002_PROPERTIES.def = 1600;
        CustomCards.PATREON_002_PROPERTIES.level = 4;
        CustomCards.PATREON_002_PROPERTIES.isTuner = false;
        CustomCards.PATREON_002_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_002_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_002_PROPERTIES);
        
        CustomCards.PATREON_003_PROPERTIES = new LevelMonsterProperties();
        CustomCards.PATREON_003_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_003_PROPERTIES.name = "Onomic: Silent Fox";
        CustomCards.PATREON_003_PROPERTIES.id = 3;
        CustomCards.PATREON_003_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_003_PROPERTIES.isCustom = true;
        CustomCards.PATREON_003_PROPERTIES.text = "All of your opponent's monster lose 100 ATK ad DEF x the monster's level.";
        CustomCards.PATREON_003_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_003_PROPERTIES.images = null;
        CustomCards.PATREON_003_PROPERTIES.attribute = Attribute.FIRE.name;
        CustomCards.PATREON_003_PROPERTIES.atk = 1300;
        CustomCards.PATREON_003_PROPERTIES.species = Species.WARRIOR.name;
        CustomCards.PATREON_003_PROPERTIES.monsterType = null;
        CustomCards.PATREON_003_PROPERTIES.isPendulum = true;
        CustomCards.PATREON_003_PROPERTIES.pendulumText = "Once Per turn, you can pay 2000 LP to Special Summon 1 \"Onomic:\" monster from your Deck or Graveyard. All \"Onomic\" Monsters you control gain 100 ATK for each FIRE monster In both players' Graveyard and on the field.";
        CustomCards.PATREON_003_PROPERTIES.pendulumScaleLeftBlue = (byte)6;
        CustomCards.PATREON_003_PROPERTIES.pendulumScaleRightRed = (byte)6;
        CustomCards.PATREON_003_PROPERTIES.ability = "";
        CustomCards.PATREON_003_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_003_PROPERTIES.def = 1400;
        CustomCards.PATREON_003_PROPERTIES.level = 3;
        CustomCards.PATREON_003_PROPERTIES.isTuner = false;
        CustomCards.PATREON_003_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_003_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_003_PROPERTIES);
        
        CustomCards.PATREON_004_PROPERTIES = new LevelMonsterProperties();
        CustomCards.PATREON_004_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_004_PROPERTIES.name = "Onomic: Thousand Year Beast";
        CustomCards.PATREON_004_PROPERTIES.id = 4;
        CustomCards.PATREON_004_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_004_PROPERTIES.isCustom = true;
        CustomCards.PATREON_004_PROPERTIES.text = "2 \"Onomic\" monsters\nMust first be Special Summoned (from the Extra Deck) by sending the above cards you control to the Graveyard. (You do not use \"Polymerization\".) Once per turn during either player's turn you can banish one \"Onomic: card from your Graveyard, then target one card on the field: banish it. During your opponents turns you can tribute this card, then target 2 of your banished \"Onomic:\" monster with different names and Special summon them.";
        CustomCards.PATREON_004_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_004_PROPERTIES.images = null;
        CustomCards.PATREON_004_PROPERTIES.attribute = Attribute.FIRE.name;
        CustomCards.PATREON_004_PROPERTIES.atk = 2600;
        CustomCards.PATREON_004_PROPERTIES.species = Species.BEAST.name;
        CustomCards.PATREON_004_PROPERTIES.monsterType = MonsterType.FUSION;
        CustomCards.PATREON_004_PROPERTIES.isPendulum = false;
        CustomCards.PATREON_004_PROPERTIES.ability = "";
        CustomCards.PATREON_004_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_004_PROPERTIES.def = 3000;
        CustomCards.PATREON_004_PROPERTIES.level = 8;
        CustomCards.PATREON_004_PROPERTIES.isTuner = false;
        CustomCards.PATREON_004_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_004_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_004_PROPERTIES);
        
        CustomCards.PATREON_005_PROPERTIES = new LevelMonsterProperties();
        CustomCards.PATREON_005_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_005_PROPERTIES.name = "Onomic: Firespreader";
        CustomCards.PATREON_005_PROPERTIES.id = 5;
        CustomCards.PATREON_005_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_005_PROPERTIES.isCustom = true;
        CustomCards.PATREON_005_PROPERTIES.text = "When this monster is Normal  or Special summoned; You can add 1 \"Onomic:\" card from your Deck to your hand, except  \"Onomic: Firespreader\", and you cannot set cards for the rest of the turn. You can only use the effect of  \" Onomic: Firespreader\" once per turn. During the turn this effect is activated, you cannot special Summon Monster, except \"Onomic:\" monsters.";
        CustomCards.PATREON_005_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_005_PROPERTIES.images = null;
        CustomCards.PATREON_005_PROPERTIES.attribute = Attribute.FIRE.name;
        CustomCards.PATREON_005_PROPERTIES.atk = 1300;
        CustomCards.PATREON_005_PROPERTIES.species = Species.WARRIOR.name;
        CustomCards.PATREON_005_PROPERTIES.monsterType = null;
        CustomCards.PATREON_005_PROPERTIES.isPendulum = true;
        CustomCards.PATREON_005_PROPERTIES.pendulumText = "Your opponent cannot activate any cards in response to \"Onomic:\" monster effects.";
        CustomCards.PATREON_005_PROPERTIES.pendulumScaleLeftBlue = (byte)2;
        CustomCards.PATREON_005_PROPERTIES.pendulumScaleRightRed = (byte)2;
        CustomCards.PATREON_005_PROPERTIES.ability = "";
        CustomCards.PATREON_005_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_005_PROPERTIES.def = 2000;
        CustomCards.PATREON_005_PROPERTIES.level = 4;
        CustomCards.PATREON_005_PROPERTIES.isTuner = false;
        CustomCards.PATREON_005_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_005_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_005_PROPERTIES);
        
        CustomCards.PATREON_006_PROPERTIES = new SpellProperties();
        CustomCards.PATREON_006_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_006_PROPERTIES.name = "Onomic: Forest Fire";
        CustomCards.PATREON_006_PROPERTIES.id = 6;
        CustomCards.PATREON_006_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_006_PROPERTIES.isCustom = true;
        CustomCards.PATREON_006_PROPERTIES.text = "All \"Onomic:\" Monsters you control gain 200 ATK and DEF. All monsters on the field and in the Graveyard become FIRE Attribute. When this card is sent from the field to the Graveyard, Special summon up to 2 \"Onomic:\" monsters from your hand and/or Graveyard in DEF Position.";
        CustomCards.PATREON_006_PROPERTIES.type = Type.SPELL;
        CustomCards.PATREON_006_PROPERTIES.images = null;
        CustomCards.PATREON_006_PROPERTIES.spellType = SpellType.FIELD;
        CustomCards.PATREON_006_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_006_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_006_PROPERTIES);
        
        CustomCards.PATREON_007_PROPERTIES = new SpellProperties();
        CustomCards.PATREON_007_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_007_PROPERTIES.name = "Onomic: Savior";
        CustomCards.PATREON_007_PROPERTIES.id = 7;
        CustomCards.PATREON_007_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_007_PROPERTIES.isCustom = true;
        CustomCards.PATREON_007_PROPERTIES.text = "While this card is on the field, during battle involving an \"Onomic\" monster, the battle damage becomes 0. When this card leaves the field, special summon 1 \"Onomic\" monster from your deck. While this card is in the graveyard, you can banish this card to excavate 4 cards from the top of your deck, and if any of those cards are \"Onomic\" monster, special summon as many \"Onomic\" monsters among them and send the rest of the cards to the graveyard. You can only use this effect of \"Onomic: Savior\" once per turn.";
        CustomCards.PATREON_007_PROPERTIES.type = Type.SPELL;
        CustomCards.PATREON_007_PROPERTIES.images = null;
        CustomCards.PATREON_007_PROPERTIES.spellType = SpellType.CONTINUOUS;
        CustomCards.PATREON_007_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_007_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_007_PROPERTIES);
        
        CustomCards.PATREON_008_PROPERTIES = new LevelMonsterProperties();
        CustomCards.PATREON_008_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_008_PROPERTIES.name = "Onomic: Haragi, The Lone Wolf";
        CustomCards.PATREON_008_PROPERTIES.id = 8;
        CustomCards.PATREON_008_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_008_PROPERTIES.isCustom = true;
        CustomCards.PATREON_008_PROPERTIES.text = "When this card is normal summoned or flip summoned, you can add one Beast, Winged-Beast or Beast-Warrior from your deck to your hand. If \"Onomic: Wanderer With Two Tails\" is on the field while this card is in the graveyard, you can special summon this card and destory one card on the field. You can only use 1 \"Onomic: Haragi, the Lone Wolf\" effect per turn, and only once that turn.";
        CustomCards.PATREON_008_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_008_PROPERTIES.images = null;
        CustomCards.PATREON_008_PROPERTIES.attribute = Attribute.FIRE.name;
        CustomCards.PATREON_008_PROPERTIES.atk = 1850;
        CustomCards.PATREON_008_PROPERTIES.species = Species.BEAST_WARRIOR.name;
        CustomCards.PATREON_008_PROPERTIES.monsterType = null;
        CustomCards.PATREON_008_PROPERTIES.isPendulum = false;
        CustomCards.PATREON_008_PROPERTIES.ability = "";
        CustomCards.PATREON_008_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_008_PROPERTIES.def = 1600;
        CustomCards.PATREON_008_PROPERTIES.level = 4;
        CustomCards.PATREON_008_PROPERTIES.isTuner = false;
        CustomCards.PATREON_008_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_008_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_008_PROPERTIES);
        
        CustomCards.PATREON_009_PROPERTIES = new LinkMonsterProperties();
        CustomCards.PATREON_009_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_009_PROPERTIES.name = "Cybeast Corruption Dragon";
        CustomCards.PATREON_009_PROPERTIES.id = 9;
        CustomCards.PATREON_009_PROPERTIES.isIllegal = false;
        CustomCards.PATREON_009_PROPERTIES.isCustom = true;
        CustomCards.PATREON_009_PROPERTIES.text = "2+ \"Cybyte\" / \"Cybeast\" Monsters\nWhen this card is Link Summoned; place a Corruption Counter on each of your opponent's monsters. During your main phase 1: You can place an opponent's monster that has a Corruption Counter in a zone this card points to and take control of it. That monster cannot attack while it has a Corruption Counter. If this card would be destroyed by an effect: You can tribute one of your monsters that has a Corruption Counter; negate that effect, and if you do, destory that card.";
        CustomCards.PATREON_009_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_009_PROPERTIES.images = null;
        CustomCards.PATREON_009_PROPERTIES.attribute = Attribute.DARK.name;
        CustomCards.PATREON_009_PROPERTIES.atk = 1850;
        CustomCards.PATREON_009_PROPERTIES.species = Species.CYBERSE.name;
        CustomCards.PATREON_009_PROPERTIES.monsterType = MonsterType.LINK;
        CustomCards.PATREON_009_PROPERTIES.isPendulum = false;
        CustomCards.PATREON_009_PROPERTIES.ability = "";
        CustomCards.PATREON_009_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_009_PROPERTIES.linkRating = (byte)4;
        CustomCards.PATREON_009_PROPERTIES.linkArrows = Lists.newArrayList(LinkArrow.TOP_RIGHT, LinkArrow.BOTTOM_RIGHT, LinkArrow.BOTTOM, LinkArrow.BOTTOM_LEFT);
        CustomCards.PATREON_009_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_009_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_009_PROPERTIES);
        
        CustomCards.PATREON_010_PROPERTIES = new XyzMonsterProperties();
        CustomCards.PATREON_010_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_010_PROPERTIES.name = "Jin Mori - Monkey King";
        CustomCards.PATREON_010_PROPERTIES.id = 10;
        CustomCards.PATREON_010_PROPERTIES.isIllegal = true;
        CustomCards.PATREON_010_PROPERTIES.isCustom = true;
        CustomCards.PATREON_010_PROPERTIES.text = "2 or more (max. 5) Level 4 monsters\nThis card gains effects based on the number of Xyz materials attached to it.\n2+: This card is unaffected by your opponent's cards and effects.\n3+: Double this card's ATK and Def.\n4+: Once per turn, you can negate one card's effect up to the number of Xyz material attached to this card.\n5: Triples this card's ATK and Def (on top of the doubled ATK), this card can also attack your opponent directly.";
        CustomCards.PATREON_010_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_010_PROPERTIES.images = null;
        CustomCards.PATREON_010_PROPERTIES.attribute = Attribute.DIVINE.name;
        CustomCards.PATREON_010_PROPERTIES.atk = 2500;
        CustomCards.PATREON_010_PROPERTIES.species = Species.WARRIOR_GOD.name;
        CustomCards.PATREON_010_PROPERTIES.monsterType = MonsterType.XYZ;
        CustomCards.PATREON_010_PROPERTIES.isPendulum = false;
        CustomCards.PATREON_010_PROPERTIES.ability = "";
        CustomCards.PATREON_010_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_010_PROPERTIES.def = 2500;
        CustomCards.PATREON_010_PROPERTIES.rank = 4;
        CustomCards.PATREON_010_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_010_PROPERTIES);
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_010_PROPERTIES);
    }
    
    public static CardHolder createPatreonCard(Properties c) // add rarity here as parameter
    {
        return new CardHolder(c, (byte)0, Rarity.COMMON.name, "PATREON-" + c.id);
    }
}
