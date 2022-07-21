package de.cas_ual_ty.ydm.duel.playfield;

import com.google.common.collect.ImmutableList;
import de.cas_ual_ty.ydm.duel.action.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PlayFieldTypes
{
    public static final PlayFieldType DEFAULT = new PlayFieldType(8000)
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
            .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_FD).interactorUnequals(ZoneTypes.EXTRA_DECK).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA_DECK).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_FD).interactorUnequals(ZoneTypes.DECK).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.DECK).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, interactee.getDefaultCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_BOTTOM_OF_DECK_FD).interactorUnequals(ZoneTypes.DECK).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.DECK).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM, interactor, card, interactee, interactee.getDefaultCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_ATK).interactorUnequals(ZoneTypes.EXTRA_DECK).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA_DECK).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.NORMAL_SUMMON).interactorEquals(ZoneTypes.HAND).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SET).interactorEquals(ZoneTypes.HAND).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_ATK).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.SPECIAL_SUMMON, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_DEF).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.SPECIAL_SUMMON, interactor, card, interactee, CardPosition.DEF, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_SET).interactorIncluded(PlayFieldTypes.getAllStackZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.SPECIAL_SUMMON, interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.ATK_TO_DEF).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION, interactor, card, CardPosition.DEF)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.ATK_TO_SET).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION, interactor, card, CardPosition.SET)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.DEF_SET_TO_ATK).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION, interactor, card, CardPosition.ATK)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.SET_TO_DEF).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION, interactor, card, CardPosition.DEF)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.SET).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.DEF_TO_SET).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION, interactor, card, CardPosition.SET)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.DEF).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.BANISH_ATK).interactorUnequals(ZoneTypes.BANISHED).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.BANISHED).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.BANISH_FD).interactorUnequals(ZoneTypes.BANISHED).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.BANISHED).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.ACTIVATE_SPELL_TRAP).interactorExcluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.ACTIVATE_SPELL_TRAP).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.ATK).addInteraction()
            .newInteraction().icon(ActionIcons.SET_SPELL_TRAP).interactorExcluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SET_SPELL_TRAP).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.FD).addInteraction()
            .newInteraction().icon(ActionIcons.OVERLAY).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(ActionTypes.LIST, player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, card.getCardPosition(), player))).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.UNDERLAY).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_OVERLAY_ATK).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(ActionTypes.SPECIAL_SUMMON_OVERLAY, player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player))).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeNonEmptyNoTokens().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_OVERLAY_DEF).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(ActionTypes.SPECIAL_SUMMON_OVERLAY, player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.DEF, player))).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeNonEmptyNoTokens().addInteraction()
            .newInteraction().icon(ActionIcons.ADD_TO_HAND).interactorUnequals(ZoneTypes.HAND).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.SHUFFLE_DECK).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllDeckZones()).interaction((player, interactor, card, interactee) -> new ShuffleAction(ActionTypes.SHUFFLE_ZONE, interactee)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHUFFLE_HAND).interactorEquals(ZoneTypes.HAND).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShuffleAction(ActionTypes.SHUFFLE_ZONE, interactee)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.VIEW_DECK).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllDeckZones()).interaction((player, interactor, card, interactee) -> new ViewZoneAction(ActionTypes.VIEW_ZONE, interactor)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHOW_HAND).interactorEquals(ZoneTypes.HAND).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShowZoneAction(ActionTypes.SHOW_ZONE, interactor)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHOW_DECK).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShowZoneAction(ActionTypes.SHOW_ZONE, interactor)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHOW_CARD).interactorEquals(ZoneTypes.HAND).interactorCardNotNull().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new ShowCardAction(ActionTypes.SHOW_CARD, interactor, card)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.MOVE).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, card.getCardPosition(), player)).playerAndInteractorSameOwner().interactorUnequalsInteractee().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.MOVE).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, card.getCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.TO_GRAVEYARD).interactorUnequals(ZoneTypes.GRAVEYARD).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.GRAVEYARD).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.ATTACK).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new AttackAction(ActionTypes.ATTACK, interactor, interactee)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.ATTACK_DIRECTLY).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardAny().interacteeEquals(ZoneTypes.HAND).interaction((player, interactor, card, interactee) -> new AttackAction(ActionTypes.ATTACK, interactor, interactee)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.TO_EXTRA_ATK).interactorUnequals(ZoneTypes.EXTRA).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_EXTRA_FD).interactorUnequals(ZoneTypes.EXTRA).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP, interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newAdvancedInteraction().icon(ActionIcons.SPECIAL_SUMMON_TOKEN_ATK).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new CreateTokenAction(ActionTypes.CREATE_TOKEN, interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newAdvancedInteraction().icon(ActionIcons.SPECIAL_SUMMON_TOKEN_DEF).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new CreateTokenAction(ActionTypes.CREATE_TOKEN, interactor, card, interactee, CardPosition.DEF, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.REMOVE_TOKEN_ATK).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardToken().interacteeIncluded(PlayFieldTypes.getAllFieldZones()).interaction((player, interactor, card, interactee) -> new RemoveTokenAction(ActionTypes.REMOVE_TOKEN, interactor, card, interactee, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).addInteraction()
            .newInteraction().icon(ActionIcons.REMOVE_TOKEN_DEF).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardToken().interacteeIncluded(PlayFieldTypes.getAllFieldZones()).interaction((player, interactor, card, interactee) -> new RemoveTokenAction(ActionTypes.REMOVE_TOKEN, interactor, card, interactee, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.DEF).addInteraction();
    //TODO advanced interaction: moved all to GY/banished
    
    public static Action doForAllCardsInZone(ActionType listActionType, Zone sourceZone, Function<DuelCard, Action> consumer)
    {
        List<Action> actions = new ArrayList<>(1 + sourceZone.getCardsAmount());
        
        for(short i = (short) (sourceZone.getCardsAmount() - 1); i >= 0; --i)
        {
            actions.add(consumer.apply(sourceZone.getCard(i)));
        }
        
        return new ListAction(listActionType, actions);
    }
    
    public static Action straightenCardsBelowAndDoAction(ActionType listActionType, ZoneOwner player, Zone zone, Action action)
    {
        List<Action> actions = new ArrayList<>(1 + zone.getCardsAmount());
        
        DuelCard c;
        for(short i = 0; i < zone.getCardsAmount(); ++i)
        {
            c = zone.getCard(i);
            
            if(c.getCardPosition() != CardPosition.ATK)
            {
                actions.add(new ChangePositionAction(ActionTypes.CHANGE_POSITION, zone.index, i, CardPosition.ATK));
            }
        }
        
        actions.add(action);
        
        return new ListAction(listActionType, actions);
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
    
    public static ImmutableList<ZoneType> getAllFieldZones()
    {
        return ImmutableList.of(ZoneTypes.SPELL_TRAP, ZoneTypes.FIELD_SPELL, ZoneTypes.MONSTER, ZoneTypes.EXTRA_MONSTER_LEFT, ZoneTypes.EXTRA_MONSTER_RIGHT);
    }
}
