package de.cas_ual_ty.ydm.duel.playfield;

import com.google.common.collect.ImmutableList;
import de.cas_ual_ty.ydm.duel.action.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayFieldTypes
{
    public static final Supplier<PlayFieldType> DEFAULT = () -> new PlayFieldType(8000)
            .addEntry(ZoneTypes.HAND.get(), ZoneOwner.PLAYER1, 13, 102, 194, 32)
            .addEntrySlim(ZoneTypes.DECK.get(), ZoneOwner.PLAYER1, 98, 68)
            .setLastToPlayer1Deck()
            .addEntryFull(ZoneTypes.SPELL_TRAP.get(), ZoneOwner.PLAYER1, 68, 68)
            .repeat(-2, 0, 4)
            .addEntrySlim(ZoneTypes.EXTRA_DECK.get(), ZoneOwner.PLAYER1, -98, 68)
            .setLastToPlayer1ExtraDeck()
            .addEntrySlim(ZoneTypes.GRAVEYARD.get(), ZoneOwner.PLAYER1, 98, 34)
            .addEntryFull(ZoneTypes.MONSTER.get(), ZoneOwner.PLAYER1, 68, 34)
            .repeat(-2, 0, 4)
            .addEntrySlim(ZoneTypes.FIELD_SPELL.get(), ZoneOwner.PLAYER1, -98, 34)
            .addEntrySlim(ZoneTypes.BANISHED.get(), ZoneOwner.PLAYER1, 98, 0)
            .addEntrySlim(ZoneTypes.EXTRA.get(), ZoneOwner.PLAYER1, -98, 102)
            .repeatPlayerZonesForOpponent()
            .addEntryFull(ZoneTypes.EXTRA_MONSTER_RIGHT.get(), ZoneOwner.NONE, 34, 0)
            .addEntryFull(ZoneTypes.EXTRA_MONSTER_LEFT.get(), ZoneOwner.NONE, -34, 0)
            .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_FD.get()).interactorUnequals(ZoneTypes.EXTRA_DECK.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA_DECK.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_FD.get()).interactorUnequals(ZoneTypes.DECK.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.DECK.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, interactee.getDefaultCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_BOTTOM_OF_DECK_FD.get()).interactorUnequals(ZoneTypes.DECK.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.DECK.get()).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM.get(), interactor, card, interactee, interactee.getDefaultCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_TOP_OF_DECK_ATK.get()).interactorUnequals(ZoneTypes.EXTRA_DECK.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA_DECK.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.NORMAL_SUMMON.get()).interactorEquals(ZoneTypes.HAND.get()).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SET.get()).interactorEquals(ZoneTypes.HAND.get()).interactorCardNotNull().interacteeEquals(ZoneTypes.MONSTER.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_ATK.get()).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.SPECIAL_SUMMON.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_DEF.get()).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.SPECIAL_SUMMON.get(), interactor, card, interactee, CardPosition.DEF, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_SET.get()).interactorIncluded(PlayFieldTypes.getAllStackZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.SPECIAL_SUMMON.get(), interactor, card, interactee, CardPosition.SET, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.ATK_TO_DEF.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION.get(), interactor, card, CardPosition.DEF)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.ATK_TO_SET.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION.get(), interactor, card, CardPosition.SET)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.DEF_SET_TO_ATK.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION.get(), interactor, card, CardPosition.ATK)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.ATK).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.SET_TO_DEF.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION.get(), interactor, card, CardPosition.DEF)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.SET).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.DEF_TO_SET.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new ChangePositionAction(ActionTypes.CHANGE_POSITION.get(), interactor, card, CardPosition.SET)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.DEF).cardIsOnTop().addInteraction()
            .newInteraction().icon(ActionIcons.BANISH_ATK.get()).interactorUnequals(ZoneTypes.BANISHED.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.BANISHED.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.BANISH_FD.get()).interactorUnequals(ZoneTypes.BANISHED.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.BANISHED.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.ACTIVATE_SPELL_TRAP.get()).interactorExcluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.ACTIVATE_SPELL_TRAP.get()).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.ATK).addInteraction()
            .newInteraction().icon(ActionIcons.SET_SPELL_TRAP.get()).interactorExcluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SET_SPELL_TRAP.get()).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardNotInPosition(CardPosition.FD).addInteraction()
            .newInteraction().icon(ActionIcons.OVERLAY.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(ActionTypes.LIST.get(), player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, card.getCardPosition(), player))).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.UNDERLAY.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNullNoToken().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_OVERLAY_ATK.get()).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(ActionTypes.SPECIAL_SUMMON_OVERLAY.get(), player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player))).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeNonEmptyNoTokens().addInteraction()
            .newInteraction().icon(ActionIcons.SPECIAL_SUMMON_OVERLAY_DEF.get()).interactorExcluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> PlayFieldTypes.straightenCardsBelowAndDoAction(ActionTypes.SPECIAL_SUMMON_OVERLAY.get(), player, interactee, new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.DEF, player))).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interacteeNonEmptyNoTokens().addInteraction()
            .newInteraction().icon(ActionIcons.ADD_TO_HAND.get()).interactorUnequals(ZoneTypes.HAND.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.HAND.get()).interaction((player, interactor, card, interactee) -> new MoveBottomAction(ActionTypes.MOVE_TO_BOTTOM.get(), interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.SHUFFLE_DECK.get()).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllDeckZones()).interaction((player, interactor, card, interactee) -> new ShuffleAction(ActionTypes.SHUFFLE_ZONE.get(), interactee)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHUFFLE_HAND.get()).interactorEquals(ZoneTypes.HAND.get()).interactorCardAny().interacteeEquals(ZoneTypes.HAND.get()).interaction((player, interactor, card, interactee) -> new ShuffleAction(ActionTypes.SHUFFLE_ZONE.get(), interactee)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.VIEW_DECK.get()).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllDeckZones()).interaction((player, interactor, card, interactee) -> new ViewZoneAction(ActionTypes.VIEW_ZONE.get(), interactor)).playerAndInteractorSameOwner().interactorEqualsInteractee().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHOW_HAND.get()).interactorEquals(ZoneTypes.HAND.get()).interactorCardAny().interacteeEquals(ZoneTypes.HAND.get()).interaction((player, interactor, card, interactee) -> new ShowZoneAction(ActionTypes.SHOW_ZONE.get(), interactor)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHOW_DECK.get()).interactorIncluded(PlayFieldTypes.getAllDeckZones()).interactorCardAny().interacteeEquals(ZoneTypes.HAND.get()).interaction((player, interactor, card, interactee) -> new ShowZoneAction(ActionTypes.SHOW_ZONE.get(), interactor)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.SHOW_CARD.get()).interactorEquals(ZoneTypes.HAND.get()).interactorCardNotNull().interacteeEquals(ZoneTypes.HAND.get()).interaction((player, interactor, card, interactee) -> new ShowCardAction(ActionTypes.SHOW_CARD.get(), interactor, card)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.MOVE.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, card.getCardPosition(), player)).playerAndInteractorSameOwner().interactorUnequalsInteractee().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.MOVE.get()).interactorIncluded(PlayFieldTypes.getAllSpellZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllSpellZones()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, card.getCardPosition(), player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().interactorUnequalsInteractee().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.TO_GRAVEYARD.get()).interactorUnequals(ZoneTypes.GRAVEYARD.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.GRAVEYARD.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().cardAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.ATTACK.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardAny().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new AttackAction(ActionTypes.ATTACK.get(), interactor, interactee)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().interacteeNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.ATTACK_DIRECTLY.get()).interactorIncluded(PlayFieldTypes.getAllMonsterZones()).interactorCardAny().interacteeEquals(ZoneTypes.HAND.get()).interaction((player, interactor, card, interactee) -> new AttackAction(ActionTypes.ATTACK.get(), interactor, interactee)).playerAndInteractorSameOwner().interactorAndInteracteeNotSameOwner().interactorNonEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.TO_EXTRA_ATK.get()).interactorUnequals(ZoneTypes.EXTRA.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newInteraction().icon(ActionIcons.TO_EXTRA_FD.get()).interactorUnequals(ZoneTypes.EXTRA.get()).interactorCardNotNullNoToken().interacteeEquals(ZoneTypes.EXTRA.get()).interaction((player, interactor, card, interactee) -> new MoveTopAction(ActionTypes.MOVE_ON_TOP.get(), interactor, card, interactee, CardPosition.FD, player)).playerAndInteractorSameOwner().interactorAndInteracteeSameOwner().addInteraction()
            .newAdvancedInteraction().icon(ActionIcons.SPECIAL_SUMMON_TOKEN_ATK.get()).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new CreateTokenAction(ActionTypes.CREATE_TOKEN.get(), interactor, card, interactee, CardPosition.ATK, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newAdvancedInteraction().icon(ActionIcons.SPECIAL_SUMMON_TOKEN_DEF.get()).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardNotNull().interacteeIncluded(PlayFieldTypes.getAllMonsterZones()).interaction((player, interactor, card, interactee) -> new CreateTokenAction(ActionTypes.CREATE_TOKEN.get(), interactor, card, interactee, CardPosition.DEF, player)).playerAndInteractorSameOwner().interacteeEmpty().addInteraction()
            .newInteraction().icon(ActionIcons.REMOVE_TOKEN_ATK.get()).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardToken().interacteeIncluded(PlayFieldTypes.getAllFieldZones()).interaction((player, interactor, card, interactee) -> new RemoveTokenAction(ActionTypes.REMOVE_TOKEN.get(), interactor, card, interactee, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.ATK).addInteraction()
            .newInteraction().icon(ActionIcons.REMOVE_TOKEN_DEF.get()).interactorIncluded(PlayFieldTypes.getAllFieldZones()).interactorCardToken().interacteeIncluded(PlayFieldTypes.getAllFieldZones()).interaction((player, interactor, card, interactee) -> new RemoveTokenAction(ActionTypes.REMOVE_TOKEN.get(), interactor, card, interactee, player)).playerAndInteractorSameOwner().interactorEqualsInteractee().cardInPosition(CardPosition.DEF).addInteraction();
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
                actions.add(new ChangePositionAction(ActionTypes.CHANGE_POSITION.get(), zone.index, i, CardPosition.ATK));
            }
        }
        
        actions.add(action);
        
        return new ListAction(listActionType, actions);
    }
    
    public static ImmutableList<ZoneType> getAllMonsterZones()
    {
        return ImmutableList.of(ZoneTypes.MONSTER.get(), ZoneTypes.EXTRA_MONSTER_RIGHT.get(), ZoneTypes.EXTRA_MONSTER_LEFT.get());
    }
    
    public static ImmutableList<ZoneType> getAllStackZones()
    {
        return ImmutableList.of(ZoneTypes.DECK.get(), ZoneTypes.EXTRA_DECK.get(), ZoneTypes.GRAVEYARD.get(), ZoneTypes.EXTRA.get());
    }
    
    public static ImmutableList<ZoneType> getAllDeckZones()
    {
        return ImmutableList.of(ZoneTypes.DECK.get(), ZoneTypes.EXTRA_DECK.get());
    }
    
    public static ImmutableList<ZoneType> getAllSpellZones()
    {
        return ImmutableList.of(ZoneTypes.SPELL_TRAP.get(), ZoneTypes.FIELD_SPELL.get());
    }
    
    public static ImmutableList<ZoneType> getAllFieldZones()
    {
        return ImmutableList.of(ZoneTypes.SPELL_TRAP.get(), ZoneTypes.FIELD_SPELL.get(), ZoneTypes.MONSTER.get(), ZoneTypes.EXTRA_MONSTER_LEFT.get(), ZoneTypes.EXTRA_MONSTER_RIGHT.get());
    }
}
