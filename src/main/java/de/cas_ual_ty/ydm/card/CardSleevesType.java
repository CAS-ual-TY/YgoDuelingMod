package de.cas_ual_ty.ydm.card;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public enum CardSleevesType
{
    CARD_BACK("card_back"), BRONZE("bronze"), SILVER("silver"), GOLD("gold"), PLATINUM("platinum"), RUBY("ruby"), BLACK("black"), BLUE("blue"), BROWN("brown"), CYAN("cyan"), GRAY("gray"), GREEN("green"), LIGHT_BLUE("light_blue"), LIGHT_GRAY("light_gray"), LIME("lime"), MAGENTA("magenta"), ORANGE("orange"), PINK("pink"), PURPLE("purple"), RED("red"), WHITE("white"), YELLOW("yellow"), VFD("vfd"), OLD_ENTITY("old_entity"), MASTER_PEACE("master_peace"), HERO("hero"), DESTINY_HERO("destiny_hero"), P_1("p_1", "Lucifer"), KINGDOMS_MC("kingdoms_mc"), DUELIST_ACADEMY_NETWORK("duelist_academy_network"), P_2("p_2", "LuisRavenFlame1");
    
    public static final CardSleevesType[] VALUES = CardSleevesType.values();
    
    public static CardSleevesType getFromIndex(byte index)
    {
        return CardSleevesType.VALUES[index];
    }
    
    static
    {
        byte index = 0;
        for(CardSleevesType duelPhase : CardSleevesType.VALUES)
        {
            duelPhase.index = index++;
        }
    }
    
    public final String name;
    public final boolean isPatreonReward;
    public final String patronName;
    private byte index;
    
    private CardSleevesType(String name, boolean isPatreonReward, String patron)
    {
        this.name = name;
        this.isPatreonReward = isPatreonReward;
        this.patronName = patron;
    }
    
    private CardSleevesType(String name)
    {
        this(name, false, null);
    }
    
    private CardSleevesType(String name, String patron)
    {
        this(name, true, patron);
    }
    
    public boolean isCardBack()
    {
        return this == CARD_BACK;
    }
    
    public ResourceLocation getMainRL(int size)
    {
        return new ResourceLocation(YDM.MOD_ID, "textures/item/" + size + "/" + this.getResourceName() + ".png");
    }
    
    public ResourceLocation getItemModelRL(int size)
    {
        if(this.isCardBack())
        {
            return null;
        }
        else
        {
            return new ResourceLocation(YDM.MOD_ID, this.getResourceName() + "_" + size);
        }
    }
    
    public String getResourceName()
    {
        if(this.isCardBack())
        {
            return this.name;
        }
        else
        {
            return "sleeves_" + this.name;
        }
    }
    
    public Item getItem()
    {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(YDM.MOD_ID, this.getResourceName()));
    }
    
    public byte getIndex()
    {
        return this.index;
    }
}
