package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.action.ActionIcons;
import de.cas_ual_ty.ydm.duelmanager.action.ActionTypes;
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
        .registerInteration(ActionIcons.ADD_TO_HAND, ZoneTypes.DECK, ZoneTypes.HAND, (player, deck, hand) -> (player == deck.getOwner() && deck.getCardsAmount() != 0 && deck.getOwner() == hand.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, deck, (short)0, hand, CardPosition.FACE_DOWN) : null)
        .registerInteration(ActionIcons.TO_GRAVEYARD, (interactor) -> interactor != ZoneTypes.GRAVEYARD, (interactee) -> interactee == ZoneTypes.GRAVEYARD, (player, zone, gy) -> (player == zone.getOwner() && zone.getOwner() == gy.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, (short)0, gy, CardPosition.ATK) : null)
        .registerInteration(ActionIcons.BANISH_FA, (interactor) -> interactor != ZoneTypes.BANISHED, (interactee) -> interactee == ZoneTypes.BANISHED, (player, zone, banished) -> (player == zone.getOwner() && zone.getOwner() == banished.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, (short)0, banished, CardPosition.ATK) : null)
        .registerInteration(ActionIcons.BANISH_FD, (interactor) -> interactor != ZoneTypes.BANISHED, (interactee) -> interactee == ZoneTypes.BANISHED, (player, zone, banished) -> (player == zone.getOwner() && zone.getOwner() == banished.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, (short)0, banished, CardPosition.FACE_DOWN) : null);
}
