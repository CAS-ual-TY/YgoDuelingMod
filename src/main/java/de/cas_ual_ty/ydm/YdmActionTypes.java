package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.duelmanager.action.ActionType;
import de.cas_ual_ty.ydm.duelmanager.action.AttackAction;
import de.cas_ual_ty.ydm.duelmanager.action.ListAction;
import de.cas_ual_ty.ydm.duelmanager.action.MoveBottomAction;
import de.cas_ual_ty.ydm.duelmanager.action.MoveTopAction;
import de.cas_ual_ty.ydm.duelmanager.action.PopulateAction;
import de.cas_ual_ty.ydm.duelmanager.action.ShowZoneAction;
import de.cas_ual_ty.ydm.duelmanager.action.ShuffleAction;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmActionTypes
{
    public static final ActionType POPULATE = null;
    public static final ActionType MOVE_ON_TOP = null;
    public static final ActionType MOVE_TO_BOTTOM = null;
    public static final ActionType SHUFFLE = null;
    public static final ActionType SHOW_ZONE = null;
    public static final ActionType ATTACK = null;
    public static final ActionType LIST = null;
    
    @SubscribeEvent
    public static void registerActionTypes(RegistryEvent.Register<ActionType> event)
    {
        IForgeRegistry<ActionType> registry = event.getRegistry();
        registry.register(new ActionType(PopulateAction::new).setRegistryName(YDM.MOD_ID, "populate"));
        registry.register(new ActionType(MoveTopAction::new).setRegistryName(YDM.MOD_ID, "move_on_top"));
        registry.register(new ActionType(MoveBottomAction::new).setRegistryName(YDM.MOD_ID, "move_to_bottom"));
        registry.register(new ActionType(ShuffleAction::new).setRegistryName(YDM.MOD_ID, "shuffle"));
        registry.register(new ActionType(ShowZoneAction::new).setRegistryName(YDM.MOD_ID, "show_zone"));
        registry.register(new ActionType(AttackAction::new).setRegistryName(YDM.MOD_ID, "attack"));
        registry.register(new ActionType(ListAction::new).setRegistryName(YDM.MOD_ID, "list"));
    }
}
