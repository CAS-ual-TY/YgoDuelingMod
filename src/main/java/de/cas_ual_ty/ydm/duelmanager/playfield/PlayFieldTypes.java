package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.action.ActionType;
import de.cas_ual_ty.ydm.duelmanager.action.MoveTopAction;

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
        .addEntryFull(ZoneTypes.EXTRA_MONSTER_LEFT, ZoneOwner.NONE, -34, 0)
        .registerInteration(ZoneInteractionIcon.ADD_TO_HAND, ZoneTypes.DECK, ZoneTypes.HAND, (deck, hand) -> hand.getCardsAmount() == 0 ? null : new MoveTopAction(ActionType.MOVE_ON_TOP, deck, (short)0, hand, CardPosition.FACE_DOWN));
}
