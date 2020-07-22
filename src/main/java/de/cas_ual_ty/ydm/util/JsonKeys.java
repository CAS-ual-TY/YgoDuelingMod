package de.cas_ual_ty.ydm.util;

public class JsonKeys
{
    // cards
    
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String IS_ILLEGAL = "is_illegal";
    public static final String TEXT = "text";
    public static final String TYPE = "type";
    public static final String IMAGES = "images";
    
    // type = "Spell"
    public static final String SPELL_TYPE = "spell_type";
    
    // type = "Trap"
    public static final String TRAP_TYPE = "trap_type";
    
    // type = "Monster"
    public static final String ATTRIBUTE = "attribute";
    public static final String ATK = "atk";
    public static final String SPECIES = "species";
    public static final String MONSTER_TYPE = "monster_type";
    public static final String IS_PENDULUM = "is_pendulum";
    public static final String ABILITY = "ability";
    public static final String HAS_EFFECT = "has_effect";
    
    // type = "Monster" & monster_type = ""/"Ritual"/"Fusion"/"Synchro"/"Xyz"
    public static final String DEF = "def";
    
    // type = "Monster" & monster_type = ""/"Ritual"/"Fusion"/"Synchro"
    public static final String LEVEL = "level";
    public static final String IS_TUNER = "is_tuner";
    
    // type = "Monster" & monster_type = "Xyz"
    public static final String RANK = "rank";
    
    // type = "Monster" & monster_type = "Link"
    public static final String LINK_RATING = "link_rating";
    public static final String LINK_ARROWS = "link_arrows";
    
    // type = "Monster" & is_pendulum = true
    public static final String PENDULUM_TEXT = "pendulum_text";
    public static final String PENDULUM_SCALE_LEFT_BLUE = "pendulum_scale_left_blue";
    public static final String PENDULUM_SCALE_RIGHT_RED = "pendulum_scale_right_red";
    
    // set entries
    
    public static final String SET_ID = "set_id";
    public static final String IMAGE_INDEX = "image_index";
    public static final String RARITY = "rarity";
    
    // other
    
    public static final String COUNT = "count";
    public static final String UUID = "uuid";
}
