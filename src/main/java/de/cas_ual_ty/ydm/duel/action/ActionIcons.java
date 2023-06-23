package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

public class ActionIcons
{
    private static final DeferredRegister<ActionIcon> DEFERRED_REGISTER = DeferredRegister.create(new ResourceLocation(YDM.MOD_ID, "action_icons"), YDM.MOD_ID);
    
    public static final RegistryObject<ActionIcon> TO_TOP_OF_DECK_FD = DEFERRED_REGISTER.register("to_top_of_deck_fd", () -> ActionIcons.create("to_top_of_deck_fd"));
    public static final RegistryObject<ActionIcon> TO_BOTTOM_OF_DECK_FD = DEFERRED_REGISTER.register("to_bottom_of_deck_fd", () -> ActionIcons.create("to_bottom_of_deck_fd"));
    public static final RegistryObject<ActionIcon> TO_TOP_OF_DECK_ATK = DEFERRED_REGISTER.register("to_top_of_deck_atk", () -> ActionIcons.create("to_top_of_deck_atk"));
    public static final RegistryObject<ActionIcon> NORMAL_SUMMON = DEFERRED_REGISTER.register("normal_summon", () -> ActionIcons.create("normal_summon"));
    public static final RegistryObject<ActionIcon> SET = DEFERRED_REGISTER.register("set", () -> ActionIcons.create("set"));
    public static final RegistryObject<ActionIcon> SPECIAL_SUMMON_ATK = DEFERRED_REGISTER.register("special_summon_atk", () -> ActionIcons.create("special_summon_atk"));
    public static final RegistryObject<ActionIcon> SPECIAL_SUMMON_DEF = DEFERRED_REGISTER.register("special_summon_def", () -> ActionIcons.create("special_summon_def"));
    public static final RegistryObject<ActionIcon> SPECIAL_SUMMON_SET = DEFERRED_REGISTER.register("special_summon_set", () -> ActionIcons.create("special_summon_set"));
    public static final RegistryObject<ActionIcon> ATK_TO_DEF = DEFERRED_REGISTER.register("atk_to_def", () -> ActionIcons.create("atk_to_def"));
    public static final RegistryObject<ActionIcon> ATK_TO_SET = DEFERRED_REGISTER.register("atk_to_set", () -> ActionIcons.create("atk_to_set"));
    public static final RegistryObject<ActionIcon> DEF_SET_TO_ATK = DEFERRED_REGISTER.register("def_set_to_atk", () -> ActionIcons.create("def_set_to_atk"));
    public static final RegistryObject<ActionIcon> SET_TO_DEF = DEFERRED_REGISTER.register("set_to_def", () -> ActionIcons.create("set_to_def"));
    public static final RegistryObject<ActionIcon> DEF_TO_SET = DEFERRED_REGISTER.register("def_to_set", () -> ActionIcons.create("def_to_set"));
    public static final RegistryObject<ActionIcon> BANISH_ATK = DEFERRED_REGISTER.register("banish_atk", () -> ActionIcons.create("banish_atk"));
    public static final RegistryObject<ActionIcon> BANISH_FD = DEFERRED_REGISTER.register("banish_fd", () -> ActionIcons.create("banish_fd"));
    public static final RegistryObject<ActionIcon> ACTIVATE_SPELL_TRAP = DEFERRED_REGISTER.register("activate_spell_trap", () -> ActionIcons.create("activate_spell_trap"));
    public static final RegistryObject<ActionIcon> SET_SPELL_TRAP = DEFERRED_REGISTER.register("set_spell_trap", () -> ActionIcons.create("set_spell_trap"));
    public static final RegistryObject<ActionIcon> OVERLAY = DEFERRED_REGISTER.register("overlay", () -> ActionIcons.create("overlay"));
    public static final RegistryObject<ActionIcon> UNDERLAY = DEFERRED_REGISTER.register("underlay", () -> ActionIcons.create("underlay"));
    public static final RegistryObject<ActionIcon> SPECIAL_SUMMON_OVERLAY_ATK = DEFERRED_REGISTER.register("special_summon_overlay_atk", () -> ActionIcons.create("special_summon_overlay_atk"));
    public static final RegistryObject<ActionIcon> SPECIAL_SUMMON_OVERLAY_DEF = DEFERRED_REGISTER.register("special_summon_overlay_def", () -> ActionIcons.create("special_summon_overlay_def"));
    public static final RegistryObject<ActionIcon> ADD_TO_HAND = DEFERRED_REGISTER.register("add_to_hand", () -> ActionIcons.create("add_to_hand"));
    public static final RegistryObject<ActionIcon> SHUFFLE_DECK = DEFERRED_REGISTER.register("shuffle_deck", () -> ActionIcons.create("shuffle_deck"));
    public static final RegistryObject<ActionIcon> SHUFFLE_HAND = DEFERRED_REGISTER.register("shuffle_hand", () -> ActionIcons.create("shuffle_hand"));
    public static final RegistryObject<ActionIcon> VIEW_DECK = DEFERRED_REGISTER.register("view_deck", () -> ActionIcons.create("view_deck"));
    public static final RegistryObject<ActionIcon> SHOW_HAND = DEFERRED_REGISTER.register("show_hand", () -> ActionIcons.create("show_hand"));
    public static final RegistryObject<ActionIcon> SHOW_DECK = DEFERRED_REGISTER.register("show_deck", () -> ActionIcons.create("show_deck"));
    public static final RegistryObject<ActionIcon> SHOW_CARD = DEFERRED_REGISTER.register("show_card", () -> ActionIcons.create("show_card"));
    public static final RegistryObject<ActionIcon> MOVE = DEFERRED_REGISTER.register("move", () -> ActionIcons.create("move"));
    public static final RegistryObject<ActionIcon> TO_GRAVEYARD = DEFERRED_REGISTER.register("to_graveyard", () -> ActionIcons.create("to_graveyard"));
    public static final RegistryObject<ActionIcon> ATTACK = DEFERRED_REGISTER.register("attack", () -> ActionIcons.create("attack"));
    public static final RegistryObject<ActionIcon> ATTACK_DIRECTLY = DEFERRED_REGISTER.register("attack_directly", () -> ActionIcons.create("attack_directly"));
    public static final RegistryObject<ActionIcon> TO_EXTRA_ATK = DEFERRED_REGISTER.register("to_extra_atk", () -> ActionIcons.create("to_extra_atk"));
    public static final RegistryObject<ActionIcon> TO_EXTRA_FD = DEFERRED_REGISTER.register("to_extra_fd", () -> ActionIcons.create("to_extra_fd"));
    public static final RegistryObject<ActionIcon> REMOVE_TOKEN_ATK = DEFERRED_REGISTER.register("remove_token_atk", () -> ActionIcons.create("remove_token_atk"));
    public static final RegistryObject<ActionIcon> REMOVE_TOKEN_DEF = DEFERRED_REGISTER.register("remove_token_def", () -> ActionIcons.create("remove_token_def"));
    
    public static final RegistryObject<ActionIcon> ALL_TO_GRAVEYARD = DEFERRED_REGISTER.register("all_to_graveyard", () -> ActionIcons.create("all_to_graveyard"));
    public static final RegistryObject<ActionIcon> BANISH_ALL_ATK = DEFERRED_REGISTER.register("banish_all_atk", () -> ActionIcons.create("banish_all_atk"));
    public static final RegistryObject<ActionIcon> BANISH_ALL_FD = DEFERRED_REGISTER.register("banish_all_fd", () -> ActionIcons.create("banish_all_fd"));
    public static final RegistryObject<ActionIcon> SPECIAL_SUMMON_TOKEN_ATK = DEFERRED_REGISTER.register("special_summon_token_atk", () -> ActionIcons.create("special_summon_token_atk"));
    public static final RegistryObject<ActionIcon> SPECIAL_SUMMON_TOKEN_DEF = DEFERRED_REGISTER.register("special_summon_token_def", () -> ActionIcons.create("special_summon_token_def"));
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register(bus);
    }
    
    public static ActionIcon create(String name)
    {
        return new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/" + name + ".png"), 64);
    }
}