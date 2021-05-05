package de.cas_ual_ty.ydm.util;

public class JsonKeys
{
    // general
    
    public static final String VERSION_ITERATION = "version_iteration";
    public static final String DB_ID = "id";
    public static final String DOWNLOAD_LINK = "download_link";
    
    // cards
    
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String IS_ILLEGAL = "is_illegal";
    public static final String IS_CUSTOM = "is_custom";
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
    
    // sets and set entries
    
    public static final String CODE = "code";
    public static final String DATE = "date";
    public static final String IMAGE = "image";
    public static final String PULL_TYPE = "pull_type";
    public static final String IMAGE_INDEX = "image_index";
    public static final String RARITY = "rarity";
    public static final String CARDS = "cards";
    public static final String DISTRIBUTION = "distribution";
    public static final String SUB_SETS = "sub_sets";
    
    // distribution
    
    public static final String PULLS = "pulls";
    public static final String WEIGHT = "weight";
    public static final String ENTRIES = "entries";
    public static final String RARITIES = "rarities";
    public static final String COUNT = "count";
    
    // other
    
    public static final String UUID = "uuid";
}
