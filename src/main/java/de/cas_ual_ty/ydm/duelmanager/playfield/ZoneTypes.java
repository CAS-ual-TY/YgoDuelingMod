package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;

public class ZoneTypes
{
    
    public static final ZoneType HAND = new ZoneType(13, 102).size(194, 32);
    public static final ZoneType DECK = new ZoneType(98, 68).slimSize().secret().defaultCardPosition(CardPosition.FACE_DOWN);
    public static final ZoneType SPELL_TRAP = new ZoneType(68, 68).fullSize().withChildren(5, -2);
    public static final ZoneType EXTRA_DECK = new ZoneType(-98, 68).slimSize();
    public static final ZoneType GRAVEYARD = new ZoneType(98, 34).slimSize().strict();
    public static final ZoneType MONSTER = new ZoneType(68, 34).fullSize().allowSideways().withChildren(5, -2);
    public static final ZoneType FIELD_SPELL = new ZoneType(-98, 34).slimSize();
    public static final ZoneType BANISHED = new ZoneType(98, 0).strict().slimSize();
    public static final ZoneType EXTRA = new ZoneType(-98, 102).slimSize();
    public static final ZoneType EXTRA_MONSTER_RIGHT = new ZoneType(34, 0).fullSize().noOwner();
    public static final ZoneType EXTRA_MONSTER_LEFT = new ZoneType(-34, 0).fullSize().noOwner();
}
