package de.cas_ual_ty.ydm.duelmanager.playfield;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.cas_ual_ty.ydm.duelmanager.action.Action;
import de.cas_ual_ty.ydm.duelmanager.action.ActionIcons;
import de.cas_ual_ty.ydm.duelmanager.action.ActionTypes;
import de.cas_ual_ty.ydm.duelmanager.action.AttackAction;
import de.cas_ual_ty.ydm.duelmanager.action.ChangePositionAction;
import de.cas_ual_ty.ydm.duelmanager.action.ListAction;
import de.cas_ual_ty.ydm.duelmanager.action.MoveBottomAction;
import de.cas_ual_ty.ydm.duelmanager.action.MoveTopAction;
import de.cas_ual_ty.ydm.duelmanager.action.ShowCardAction;
import de.cas_ual_ty.ydm.duelmanager.action.ShowZoneAction;
import de.cas_ual_ty.ydm.duelmanager.action.ShuffleAction;
import de.cas_ual_ty.ydm.duelmanager.action.ViewZoneAction;

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
        .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_FD).interactorUnequals(ZoneTypes.EXTRA_DECK).interactorCardNotNull().interacteeEquals(ZoneTypes.EXTRA_DECK).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_FD).interactorUnequals(ZoneTypes.DECK).interactorCardNotNull().interacteeEquals(ZoneTypes.DECK).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, interactee.getDefaultCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.TO_BOTTOM_OF_DECK_FD).interactorUnequals(ZoneTypes.DECK).interactorCardNotNull().interacteeEquals(ZoneTypes.DECK).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM, interactor, card, interactee, interactee.getDefaultCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_ATK).interactorUnequals(ZoneTypes.EXTRA_DECK).interactorCardNotNull().interacteeEquals(ZoneTypes.EXTRA_DECK).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.NORMAL_SUMMON).interactorEquals(ZoneTypes.HAND).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SET).interactorEquals(ZoneTypes.HAND).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_ATK).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_DEF).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.DEF, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_SET).interactorIncluded(PlayFieldTypes.getAllStackZones()).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.ATK_TO_DEF).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.DEF, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
        .newInteraction().icon(ActionIcons.ATK_TO_SET).interactorEquals(ZoneTypes.MONSTER).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
        .newInteraction().icon(ActionIcons.DEF_SET_TO_ATK).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
        .newInteraction().icon(ActionIcons.SET_TO_DEF).interactorEquals(ZoneTypes.MONSTER).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.DEF, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.SET).cardIsOnTop().addInteraction()
        .newInteraction().icon(ActionIcons.DEF_TO_SET).interactorEquals(ZoneTypes.MONSTER).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.DEF).cardIsOnTop().addInteraction()
        .newInteraction().icon(ActionIcons.BANISH_ATK).interactorUnequals(ZoneTypes.BANISHED).interactorCardNotNull().interacteeEquals(ZoneTypes.BANISHED).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.BANISH_FD).interactorUnequals(ZoneTypes.BANISHED).interactorCardNotNull().interacteeEquals(ZoneTypes.BANISHED).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.ACTIVATE_SPELL_TRAP).interactorExcluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.ACTIVATE_SPELL_TRAP).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.ATK).addInteraction()
        .newInteraction().icon(ActionIcons.SET_SPELL_TRAP).interactorExcluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SET_SPELL_TRAP).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.FD).addInteraction()
        .newInteraction().icon(ActionIcons.OVERLAY).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, card.getCardPosition(), player))).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeNonEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.UNDERLAY).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeNonEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_OVERLAY_ATK).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player))).playerAndInteractorSameOwner().interacteeNonEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_OVERLAY_DEF).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.DEF, player))).playerAndInteractorSameOwner().interacteeNonEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.ADD_TO_HAND).interactorUnequals(ZoneTypes.HAND).interactorCardNotNull().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.SHUFFLE_DECK).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllDeckZones()).interaction((player, interactor, card, interactee) -> new ShuffleAction(ActionTypes.SHUFFLE, interactee)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.SHUFFLE_HAND).interactorEquals(ZoneTypes.HAND).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShuffleAction(ActionTypes.SHUFFLE, interactee)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.VIEW_DECK).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllDeckZones()).interaction((player, interactor, card, interactee) -> new ViewZoneAction(ActionTypes.VIEW_ZONE, interactor)).playerAndInteractorSameOwner().interactorEqualsInteractee().addInteraction()
        .newInteraction().icon(ActionIcons.SHOW_HAND).interactorEquals(ZoneTypes.HAND).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShowZoneAction(ActionTypes.SHOW_ZONE, interactor)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.SHOW_DECK).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShowZoneAction(ActionTypes.SHOW_ZONE, interactor)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.SHOW_CARD).interactorEquals(ZoneTypes.HAND).interactorCardNotNull().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShowCardAction(ActionTypes.SHOW_CARD, interactor, card)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.MOVE).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, card.getCardPosition(), player)).playerAndInteractorSameOwner().interactorUnequalsInteractee().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.MOVE).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(ImmutableList.of(ZoneTypes.EXTRA_MONSTER_RIGHT, ZoneTypes.EXTRA_MONSTER_LEFT)).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, card.getCardPosition(), player)).playerAndInteractorSameOwner().interactorUnequalsInteractee().interacteeEmpty().cardNotInPosition(CardPosition.SET).addInteraction()
        .newInteraction().icon(ActionIcons.MOVE).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, card.getCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.TO_GRAVEYARD).interactorUnequals(ZoneTypes.GRAVEYARD).interactorCardNotNull().interacteeEquals(ZoneTypes.GRAVEYARD).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
        .newInteraction().icon(ActionIcons.ATTACK).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new AttackAction(ActionTypes.ATTACK, interactor, interactee)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().interacteeNonEmpty().addInteraction()
        .newInteraction().icon(ActionIcons.ATTACK).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new AttackAction(ActionTypes.ATTACK, interactor, interactee)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().addInteraction();
    
    public static Action straightenCardsBelowAndDoAction(ZoneOwner player, Zone zone, Action action)
    {
        List<Action> actions = new ArrayList<>(1 + zone.getCardsAmount());
        
        for(short i = 0; i < zone.getCardsAmount(); ++i)
        {
            actions.add(new ChangePositionAction(ActionTypes.CHANGE_POSITION, zone.index, i, CardPosition.ATK));
        }
        
        actions.add(action);
        
        return new ListAction(ActionTypes.LIST, actions);
    }
    
    public static ImmutableList<ZoneType> getAllMonsterZones()
    {
        return ImmutableList.of(ZoneTypes.MONSTER, ZoneTypes.EXTRA_MONSTER_RIGHT, ZoneTypes.EXTRA_MONSTER_LEFT);
    }
    
    public static ImmutableList<ZoneType> getAllStackZones()
    {
        return ImmutableList.of(ZoneTypes.DECK, ZoneTypes.EXTRA_DECK, ZoneTypes.GRAVEYARD, ZoneTypes.EXTRA);
    }
    
    public static ImmutableList<ZoneType> getAllDeckZones()
    {
        return ImmutableList.of(ZoneTypes.DECK, ZoneTypes.EXTRA_DECK);
    }
    
    public static ImmutableList<ZoneType> getAllSpellZones()
    {
        return ImmutableList.of(ZoneTypes.SPELL_TRAP, ZoneTypes.FIELD_SPELL);
    }
}
