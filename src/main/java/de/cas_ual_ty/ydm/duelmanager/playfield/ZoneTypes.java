package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;

public class ZoneTypes
{
    public static final ZoneType HAND = new ZoneType();
    public static final ZoneType DECK = new ZoneType().secret().defaultCardPosition(CardPosition.FACE_DOWN);
    public static final ZoneType SPELL_TRAP = new ZoneType();
    public static final ZoneType EXTRA_DECK = new ZoneType();
    public static final ZoneType GRAVEYARD = new ZoneType().strict();
    public static final ZoneType MONSTER = new ZoneType().allowSideways();
    public static final ZoneType FIELD_SPELL = new ZoneType();
    public static final ZoneType BANISHED = new ZoneType().strict();
    public static final ZoneType EXTRA = new ZoneType();
    public static final ZoneType EXTRA_MONSTER_RIGHT = new ZoneType().noOwner();
    public static final ZoneType EXTRA_MONSTER_LEFT = new ZoneType().noOwner();
}
