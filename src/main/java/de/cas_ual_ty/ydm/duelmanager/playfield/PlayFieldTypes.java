package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.YdmActionIcons;
import de.cas_ual_ty.ydm.YdmZoneTypes;
import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.action.ActionType;
import de.cas_ual_ty.ydm.duelmanager.action.MoveTopAction;

public class PlayFieldTypes
{
    public static final PlayFieldType DEFAULT = new PlayFieldType()
        .addEntry(YdmZoneTypes.HAND, ZoneOwner.PLAYER1, 13, 102, 194, 32)
        .addEntrySlim(YdmZoneTypes.DECK, ZoneOwner.PLAYER1, 98, 68)
        .setLastToPlayer1Deck()
        .addEntryFull(YdmZoneTypes.SPELL_TRAP, ZoneOwner.PLAYER1, 68, 68)
        .repeat(-2, 0, 4)
        .addEntrySlim(YdmZoneTypes.EXTRA_DECK, ZoneOwner.PLAYER1, -98, 68)
        .setLastToPlayer1ExtraDeck()
        .addEntrySlim(YdmZoneTypes.GRAVEYARD, ZoneOwner.PLAYER1, 98, 34)
        .addEntryFull(YdmZoneTypes.MONSTER, ZoneOwner.PLAYER1, 68, 34)
        .repeat(-2, 0, 4)
        .addEntrySlim(YdmZoneTypes.FIELD_SPELL, ZoneOwner.PLAYER1, -98, 34)
        .addEntrySlim(YdmZoneTypes.BANISHED, ZoneOwner.PLAYER1, 98, 0)
        .addEntrySlim(YdmZoneTypes.EXTRA, ZoneOwner.PLAYER1, -98, 102)
        .repeatPlayerZonesForOpponent()
        .addEntryFull(YdmZoneTypes.EXTRA_MONSTER_RIGHT, ZoneOwner.NONE, 34, 0)
        .addEntryFull(YdmZoneTypes.EXTRA_MONSTER_LEFT, ZoneOwner.NONE, -34, 0)
        .registerInteration(YdmActionIcons.ADD_TO_HAND, YdmZoneTypes.DECK, YdmZoneTypes.HAND, (deck, hand) -> hand.getCardsAmount() == 0 ? null : new MoveTopAction(ActionType.MOVE_ON_TOP, deck, (short)0, hand, CardPosition.FACE_DOWN));
}
