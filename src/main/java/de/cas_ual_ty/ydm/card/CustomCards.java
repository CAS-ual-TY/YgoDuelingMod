package de.cas_ual_ty.ydm.card;

import java.util.List;

import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.card.properties.Properties;

public class CustomCards
{
    public static Properties DUMMY_PROPERTIES;
    public static Card DUMMY_CARD;
    
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
        CustomCards.DUMMY_PROPERTIES.id = 1;
        CustomCards.DUMMY_PROPERTIES.isIllegal = false;
        CustomCards.DUMMY_PROPERTIES.isCustom = true;
        CustomCards.DUMMY_PROPERTIES.text = "This is a replacement card!";
        CustomCards.DUMMY_PROPERTIES.type = null;
        
        YdmDatabase.PROPERTIES_LIST.add(CustomCards.DUMMY_PROPERTIES);
        
        CustomCards.DUMMY_CARD = new Card();
        CustomCards.DUMMY_CARD.properties = CustomCards.DUMMY_PROPERTIES;
        CustomCards.DUMMY_CARD.setId = "DUM-MY";
        CustomCards.DUMMY_CARD.imageIndex = (byte)0;
        CustomCards.DUMMY_CARD.rarity = Rarity.COMMON;
        
        YdmDatabase.CARDS_LIST.add(CustomCards.DUMMY_CARD);
    }
}
