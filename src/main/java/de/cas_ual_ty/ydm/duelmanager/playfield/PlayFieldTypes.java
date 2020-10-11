package de.cas_ual_ty.ydm.duelmanager.playfield;

public class PlayFieldTypes
{
    public static final PlayFieldType DEFAULT = new PlayFieldType()
        .addEntry(ZoneTypes.HAND, ZoneOwner.PLAYER1, 13, 102, 194, 32)
        .addEntrySlim(ZoneTypes.DECK, ZoneOwner.PLAYER1, 98, 68)
        .setLastToPlayer1Deck()
        .addEntryFull(ZoneTypes.SPELL_TRAP, ZoneOwner.PLAYER1, 68, 68)
        .repeat(-2, 0, 4)
        .addEntrySlim(ZoneTypes.EXTRA_DECK, ZoneOwner.PLAYER1, -98, 68)
        .setLastToPlayer1ExtraDeck()
        .addEntrySlim(ZoneTypes.GRAVEYARD, ZoneOwner.PLAYER1, 98, 34)
        .addEntryFull(ZoneTypes.MONSTER, ZoneOwner.PLAYER1, 68, 34)
        .repeat(-2, 0, 4)
        .addEntrySlim(ZoneTypes.FIELD_SPELL, ZoneOwner.PLAYER1, -98, 34)
        .addEntrySlim(ZoneTypes.BANISHED, ZoneOwner.PLAYER1, 98, 0)
        .addEntrySlim(ZoneTypes.EXTRA, ZoneOwner.PLAYER1, -98, 102)
        .repeatPlayerZonesForOpponent()
        .addEntryFull(ZoneTypes.EXTRA_MONSTER_RIGHT, ZoneOwner.NONE, 34, 0)
        .addEntryFull(ZoneTypes.EXTRA_MONSTER_LEFT, ZoneOwner.NONE, -34, 0);
}
