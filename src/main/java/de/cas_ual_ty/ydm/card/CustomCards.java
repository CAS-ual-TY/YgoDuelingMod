package de.cas_ual_ty.ydm.card;

import java.util.List;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.Attribute;
import de.cas_ual_ty.ydm.card.properties.LevelMonsterProperties;
import de.cas_ual_ty.ydm.card.properties.Properties;
import de.cas_ual_ty.ydm.card.properties.Species;
import de.cas_ual_ty.ydm.card.properties.Type;

public class CustomCards
{
    public static Properties DUMMY_PROPERTIES;
    public static Card DUMMY_CARD;
    
    public static LevelMonsterProperties PATREON_001_PROPERTIES;
    public static Card PATREON_001_CARD;
    
    public static void createAndRegisterEverything()
    {
        CustomCards.DUMMY_PROPERTIES = new Properties()
        {
            @Override
            public String getImageName(byte imageIndex)
            {
                return "blanc_card";
            }
            
            @Override
            public void addCardType(List<String> list)
            {
                
            }
        };
        CustomCards.DUMMY_PROPERTIES.isHardcoded = true;
        CustomCards.DUMMY_PROPERTIES.name = "Dummy";
        CustomCards.DUMMY_PROPERTIES.id = 0;
        CustomCards.DUMMY_PROPERTIES.isIllegal = false;
        CustomCards.DUMMY_PROPERTIES.isCustom = true;
        CustomCards.DUMMY_PROPERTIES.text = "This is a replacement card!";
        CustomCards.DUMMY_PROPERTIES.type = null;
        CustomCards.DUMMY_PROPERTIES.images = null;
        
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.DUMMY_PROPERTIES);
        
        CustomCards.DUMMY_CARD = new Card();
        CustomCards.DUMMY_CARD.properties = CustomCards.DUMMY_PROPERTIES;
        CustomCards.DUMMY_CARD.setId = "DUM-MY";
        CustomCards.DUMMY_CARD.imageIndex = (byte)0;
        CustomCards.DUMMY_CARD.rarity = Rarity.COMMON;
        
        YdmDatabase.CARDS_LIST.add(CustomCards.DUMMY_CARD);
        
        CustomCards.PATREON_001_PROPERTIES = new LevelMonsterProperties();
        CustomCards.PATREON_001_PROPERTIES.isHardcoded = true;
        CustomCards.PATREON_001_PROPERTIES.name = "Creator of Darkness - Set";
        CustomCards.PATREON_001_PROPERTIES.id = 1;
        CustomCards.PATREON_001_PROPERTIES.isIllegal = true;
        CustomCards.PATREON_001_PROPERTIES.isCustom = true;
        CustomCards.PATREON_001_PROPERTIES.text = "Cannot be Normal Summoned/Set. Must be Special Summoned (from your hand) by tributing 3 monsters whose original names are \"The Wicked Avatar\", \"The Wicked Dreadroot\", and \"The Wicked Eraser\". This card's Special Summon cannot be negated. When this card is summoned cannot activate cards and effects until the end of this turn. Halve the ATK and DEF of all monsters your opponent controls while this card is face-up on the field. This card's original ATK and DEF is equal to the highest ATK or DEF currently on the field. (If there is a tie, you get to choose.) This card gains ATK and DEF for each card in your opponent's GY x 100. When this card is sent to the GY (by battle or card effect) banish all other cards on the field ignoring their effects.";
        CustomCards.PATREON_001_PROPERTIES.type = Type.MONSTER;
        CustomCards.PATREON_001_PROPERTIES.images = null;
        CustomCards.PATREON_001_PROPERTIES.attribute = Attribute.DARK;
        CustomCards.PATREON_001_PROPERTIES.atk = -1;
        CustomCards.PATREON_001_PROPERTIES.species = Species.DESTROYER_GOD;
        CustomCards.PATREON_001_PROPERTIES.monsterType = null;
        CustomCards.PATREON_001_PROPERTIES.isPendulum = false;
        CustomCards.PATREON_001_PROPERTIES.ability = null;
        CustomCards.PATREON_001_PROPERTIES.hasEffect = true;
        CustomCards.PATREON_001_PROPERTIES.def = -1;
        CustomCards.PATREON_001_PROPERTIES.level = 12;
        CustomCards.PATREON_001_PROPERTIES.isTuner = false;
        
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.PATREON_001_PROPERTIES);
        YdmDatabase.CARDS_LIST.add(CustomCards.PATREON_001_CARD = CustomCards.createPatreonCard(CustomCards.PATREON_001_PROPERTIES));
    }
    
    public static Card createPatreonCard(Properties p)
    {
        Card card = new Card();
        card.properties = p;
        card.setId = "PATREON-" + p.id;
        card.imageIndex = (byte)0;
        card.rarity = Rarity.COMMON;
        return card;
    }
}
