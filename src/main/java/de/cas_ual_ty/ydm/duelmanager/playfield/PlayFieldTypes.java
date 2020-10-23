package de.cas_ual_ty.ydm.duelmanager.playfield;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.action.ActionIcons;
import de.cas_ual_ty.ydm.duelmanager.action.ActionTypes;
import de.cas_ual_ty.ydm.duelmanager.action.MoveBottomAction;
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
        //        .registerInteraction(ActionIcons.ADD_TO_HAND, ZoneTypes.DECK, ZoneTypes.HAND, (player, deck, card, hand) -> (player == deck.getOwner() && card != null && deck.getOwner() == hand.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, deck, deck.getCardIndexShort(card), hand, CardPosition.FACE_DOWN) : null)
        //        .registerInteraction(ActionIcons.TO_GRAVEYARD, (interactor) -> interactor != ZoneTypes.GRAVEYARD, (interactee) -> interactee == ZoneTypes.GRAVEYARD, (player, zone, card, gy) -> (player == zone.getOwner() && card != null && zone.getOwner() == gy.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), gy, CardPosition.ATK) : null)
        //        .registerInteraction(ActionIcons.BANISH_FA, (interactor) -> interactor != ZoneTypes.BANISHED, (interactee) -> interactee == ZoneTypes.BANISHED, (player, zone, card, banished) -> (player == zone.getOwner() && card != null && zone.getOwner() == banished.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.ATK) : null)
        //        .registerInteraction(ActionIcons.BANISH_FD, (interactor) -> interactor != ZoneTypes.BANISHED, (interactee) -> interactee == ZoneTypes.BANISHED, (player, zone, card, banished) -> (player == zone.getOwner() && card != null && zone.getOwner() == banished.getOwner()) ? new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.FACE_DOWN) : null)
        .registerGenericZoneOwnedCardInteraction(ActionIcons.ADD_TO_HAND, (zoneType) -> zoneType == ZoneTypes.HAND, (player, deck, card, hand) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM, deck, deck.getCardIndexShort(card), hand, CardPosition.FACE_DOWN))
        .registerGenericZoneOwnedCardOwnedInteraction(ActionIcons.TO_GRAVEYARD, (zoneType) -> zoneType == ZoneTypes.GRAVEYARD, (player, zone, card, graveyard) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), graveyard, CardPosition.ATK))
        .registerGenericZoneOwnedCardOwnedInteraction(ActionIcons.BANISH_FA, (zoneType) -> zoneType == ZoneTypes.BANISHED, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.ATK))
        .registerGenericZoneOwnedCardOwnedInteraction(ActionIcons.BANISH_FD, (zoneType) -> zoneType == ZoneTypes.BANISHED, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.FACE_DOWN))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.NORMAL_SUMMON, (zoneType) -> zoneType == ZoneTypes.MONSTER, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.ATK))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.SPECIAL_SUMMON_ATK, (zoneType) -> zoneType == ZoneTypes.MONSTER, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.ATK))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.SET_MONSTER, (zoneType) -> zoneType == ZoneTypes.MONSTER, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.SET))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.SPECIAL_SUMMON_DEF, (zoneType) -> zoneType == ZoneTypes.MONSTER, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.DEF))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.SPECIAL_SUMMON_ATK, (zoneType) -> zoneType == ZoneTypes.EXTRA_MONSTER_LEFT, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.ATK))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.SPECIAL_SUMMON_DEF, (zoneType) -> zoneType == ZoneTypes.EXTRA_MONSTER_RIGHT, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.DEF))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.NORMAL_SUMMON, (zoneType) -> zoneType == ZoneTypes.SPELL_TRAP, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.ATK))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.SET_SPELL_TRAP_FD, (zoneType) -> zoneType == ZoneTypes.SPELL_TRAP, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.FACE_DOWN))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.NORMAL_SUMMON, (zoneType) -> zoneType == ZoneTypes.FIELD_SPELL, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.ATK))
        .registerGenericZoneOwnedCardInteraction(ActionIcons.SET_SPELL_TRAP_FD, (zoneType) -> zoneType == ZoneTypes.FIELD_SPELL, (player, zone, card, banished) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, zone, zone.getCardIndexShort(card), banished, CardPosition.FACE_DOWN));
}
