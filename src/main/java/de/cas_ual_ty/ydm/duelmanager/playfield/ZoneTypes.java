package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;

public class ZoneTypes
{
    
    public static final ZoneType HAND = new ZoneType(13, 227).size(194, 32);
    public static final ZoneType DECK = new ZoneType(106, 193).slimSize().secret().defaultCardPosition(CardPosition.FACE_DOWN);
    public static final ZoneType SPELL_TRAP = new ZoneType(68, 193).fullSize().withChildren(5, -2);
    public static final ZoneType EXTRA_DECK = new ZoneType(-90, 193).slimSize();
    public static final ZoneType GRAVEYARD = new ZoneType(106, 159).slimSize().strict();
    public static final ZoneType MONSTER = new ZoneType(68, 159).fullSize().allowSideways().withChildren(5, -2);
    public static final ZoneType FIELD_SPELL = new ZoneType(-90, 159).slimSize();
    public static final ZoneType BANISHED = new ZoneType(106, 125).strict().slimSize();
    public static final ZoneType EXTRA = new ZoneType(-90, 227).slimSize();
    public static final ZoneType EXTRA_MONSTER_RIGHT = new ZoneType(34, 0).fullSize().noOwner();
    public static final ZoneType EXTRA_MONSTER_LEFT = new ZoneType(-34, 0).fullSize().noOwner();
    
}
