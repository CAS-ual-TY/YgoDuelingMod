package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.YDM;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class ActionTypes
{
    public static final ActionType POPULATE = null;
    public static final ActionType MOVE_ON_TOP = null;
    public static final ActionType SPECIAL_SUMMON = null;
    public static final ActionType SPECIAL_SUMMON_OVERLAY = null;
    public static final ActionType MOVE_TO_BOTTOM = null;
    public static final ActionType CHANGE_POSITION = null;
    public static final ActionType SHUFFLE_ZONE = null;
    public static final ActionType SHOW_ZONE = null;
    public static final ActionType VIEW_ZONE = null;
    public static final ActionType SHOW_CARD = null;
    public static final ActionType ATTACK = null;
    public static final ActionType LIST = null;
    
    @SubscribeEvent
    public static void registerActionTypes(RegistryEvent.Register<ActionType> event)
    {
        IForgeRegistry<ActionType> registry = event.getRegistry();
        registry.register(new ActionType(PopulateAction::new).setRegistryName(YDM.MOD_ID, "populate"));
        registry.register(new ActionType(MoveTopAction::new).setRegistryName(YDM.MOD_ID, "move_on_top"));
        registry.register(new ActionType(MoveTopAction::new).setRegistryName(YDM.MOD_ID, "special_summon"));
        registry.register(new ActionType(ListAction::new).setRegistryName(YDM.MOD_ID, "special_summon_overlay"));
        registry.register(new ActionType(MoveBottomAction::new).setRegistryName(YDM.MOD_ID, "move_to_bottom"));
        registry.register(new ActionType(ChangePositionAction::new).setRegistryName(YDM.MOD_ID, "change_position"));
        registry.register(new ActionType(ShuffleAction::new).setRegistryName(YDM.MOD_ID, "shuffle_zone"));
        registry.register(new ActionType(ShowZoneAction::new).setRegistryName(YDM.MOD_ID, "show_zone"));
        registry.register(new ActionType(ViewZoneAction::new).setRegistryName(YDM.MOD_ID, "view_zone"));
        registry.register(new ActionType(ShowCardAction::new).setRegistryName(YDM.MOD_ID, "show_card"));
        registry.register(new ActionType(AttackAction::new).setRegistryName(YDM.MOD_ID, "attack"));
        registry.register(new ActionType(ListAction::new).setRegistryName(YDM.MOD_ID, "list"));
    }
}
