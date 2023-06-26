package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ActionTypes
{
    private static final DeferredRegister<ActionType> DEFERRED_REGISTER = DeferredRegister.create(new ResourceLocation(YDM.MOD_ID, "action_types"), YDM.MOD_ID);
    
    public static final RegistryObject<ActionType> POPULATE = DEFERRED_REGISTER.register("populate", () -> new ActionType(PopulateAction::new));
    public static final RegistryObject<ActionType> MOVE_ON_TOP = DEFERRED_REGISTER.register("move_on_top", () -> new ActionType(MoveTopAction::new));
    public static final RegistryObject<ActionType> SPECIAL_SUMMON = DEFERRED_REGISTER.register("special_summon", () -> new ActionType(MoveTopAction::new));
    public static final RegistryObject<ActionType> SPECIAL_SUMMON_OVERLAY = DEFERRED_REGISTER.register("special_summon_overlay", () -> new ActionType(ListAction::new));
    public static final RegistryObject<ActionType> MOVE_TO_BOTTOM = DEFERRED_REGISTER.register("move_to_bottom", () -> new ActionType(MoveBottomAction::new));
    public static final RegistryObject<ActionType> CHANGE_POSITION = DEFERRED_REGISTER.register("change_position", () -> new ActionType(ChangePositionAction::new));
    public static final RegistryObject<ActionType> SHUFFLE_ZONE = DEFERRED_REGISTER.register("shuffle_zone", () -> new ActionType(ShuffleAction::new));
    public static final RegistryObject<ActionType> SHOW_ZONE = DEFERRED_REGISTER.register("show_zone", () -> new ActionType(ShowZoneAction::new));
    public static final RegistryObject<ActionType> VIEW_ZONE = DEFERRED_REGISTER.register("view_zone", () -> new ActionType(ViewZoneAction::new));
    public static final RegistryObject<ActionType> SHOW_CARD = DEFERRED_REGISTER.register("show_card", () -> new ActionType(ShowCardAction::new));
    public static final RegistryObject<ActionType> ATTACK = DEFERRED_REGISTER.register("attack", () -> new ActionType(AttackAction::new));
    public static final RegistryObject<ActionType> LIST = DEFERRED_REGISTER.register("list", () -> new ActionType(ListAction::new));
    public static final RegistryObject<ActionType> CHANGE_LP = DEFERRED_REGISTER.register("change_lp", () -> new ActionType(ChangeLPAction::new));
    public static final RegistryObject<ActionType> COIN_FLIP = DEFERRED_REGISTER.register("coin_flip", () -> new ActionType(CoinFlipAction::new));
    public static final RegistryObject<ActionType> DICE_ROLL = DEFERRED_REGISTER.register("dice_roll", () -> new ActionType(DiceRollAction::new));
    public static final RegistryObject<ActionType> CHANGE_COUNTERS = DEFERRED_REGISTER.register("change_counters", () -> new ActionType(ChangeCountersAction::new));
    public static final RegistryObject<ActionType> CREATE_TOKEN = DEFERRED_REGISTER.register("create_token", () -> new ActionType(CreateTokenAction::new));
    public static final RegistryObject<ActionType> REMOVE_TOKEN = DEFERRED_REGISTER.register("remove_token", () -> new ActionType(RemoveTokenAction::new));
    public static final RegistryObject<ActionType> CHANGE_PHASE = DEFERRED_REGISTER.register("change_phase", () -> new ActionType(ChangePhaseAction::new));
    public static final RegistryObject<ActionType> END_TURN = DEFERRED_REGISTER.register("end_turn", () -> new ActionType(EndTurnAction::new));
    public static final RegistryObject<ActionType> INIT_SLEEVES = DEFERRED_REGISTER.register("init_sleeves", () -> new ActionType(InitSleevesAction::new));
    public static final RegistryObject<ActionType> SELECT = DEFERRED_REGISTER.register("select", () -> new ActionType(SelectAction::new));
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register(bus);
    }
}
