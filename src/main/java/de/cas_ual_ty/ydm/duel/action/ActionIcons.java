package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class ActionIcons
{
    public static final ActionIcon TO_TOP_OF_DECK_FD = null;
    public static final ActionIcon TO_BOTTOM_OF_DECK_FD = null;
    public static final ActionIcon TO_TOP_OF_DECK_ATK = null;
    public static final ActionIcon NORMAL_SUMMON = null;
    public static final ActionIcon SET = null;
    public static final ActionIcon SPECIAL_SUMMON_ATK = null;
    public static final ActionIcon SPECIAL_SUMMON_DEF = null;
    public static final ActionIcon SPECIAL_SUMMON_SET = null;
    public static final ActionIcon ATK_TO_DEF = null;
    public static final ActionIcon ATK_TO_SET = null;
    public static final ActionIcon DEF_SET_TO_ATK = null;
    public static final ActionIcon SET_TO_DEF = null;
    public static final ActionIcon DEF_TO_SET = null;
    public static final ActionIcon BANISH_ATK = null;
    public static final ActionIcon BANISH_FD = null;
    public static final ActionIcon ACTIVATE_SPELL_TRAP = null;
    public static final ActionIcon SET_SPELL_TRAP = null;
    public static final ActionIcon OVERLAY = null;
    public static final ActionIcon UNDERLAY = null;
    public static final ActionIcon SPECIAL_SUMMON_OVERLAY_ATK = null;
    public static final ActionIcon SPECIAL_SUMMON_OVERLAY_DEF = null;
    public static final ActionIcon ADD_TO_HAND = null;
    public static final ActionIcon SHUFFLE_DECK = null;
    public static final ActionIcon SHUFFLE_HAND = null;
    public static final ActionIcon VIEW_DECK = null;
    public static final ActionIcon SHOW_HAND = null;
    public static final ActionIcon SHOW_DECK = null;
    public static final ActionIcon SHOW_CARD = null;
    public static final ActionIcon MOVE = null;
    public static final ActionIcon TO_GRAVEYARD = null;
    public static final ActionIcon ATTACK = null;
    public static final ActionIcon ATTACK_DIRECTLY = null;
    public static final ActionIcon TO_EXTRA_ATK = null;
    public static final ActionIcon TO_EXTRA_FD = null;
    
    public static final ActionIcon ALL_TO_GRAVEYARD = null;
    
    @SubscribeEvent
    public static void registerActionIcons(RegistryEvent.Register<ActionIcon> event)
    {
        IForgeRegistry<ActionIcon> registry = event.getRegistry();
        
        registry.register(ActionIcons.create("to_top_of_deck_fd"));
        registry.register(ActionIcons.create("to_bottom_of_deck_fd"));
        registry.register(ActionIcons.create("to_top_of_deck_atk"));
        registry.register(ActionIcons.create("normal_summon"));
        registry.register(ActionIcons.create("set"));
        registry.register(ActionIcons.create("special_summon_atk"));
        registry.register(ActionIcons.create("special_summon_def"));
        registry.register(ActionIcons.create("special_summon_set"));
        registry.register(ActionIcons.create("atk_to_def"));
        registry.register(ActionIcons.create("atk_to_set"));
        registry.register(ActionIcons.create("def_set_to_atk"));
        registry.register(ActionIcons.create("set_to_def"));
        registry.register(ActionIcons.create("def_to_set"));
        registry.register(ActionIcons.create("banish_atk"));
        registry.register(ActionIcons.create("banish_fd"));
        registry.register(ActionIcons.create("activate_spell_trap"));
        registry.register(ActionIcons.create("set_spell_trap"));
        registry.register(ActionIcons.create("overlay"));
        registry.register(ActionIcons.create("underlay"));
        registry.register(ActionIcons.create("special_summon_overlay_atk"));
        registry.register(ActionIcons.create("special_summon_overlay_def"));
        registry.register(ActionIcons.create("add_to_hand"));
        registry.register(ActionIcons.create("shuffle_deck"));
        registry.register(ActionIcons.create("shuffle_hand"));
        registry.register(ActionIcons.create("view_deck"));
        registry.register(ActionIcons.create("show_hand"));
        registry.register(ActionIcons.create("show_deck"));
        registry.register(ActionIcons.create("show_card"));
        registry.register(ActionIcons.create("move"));
        registry.register(ActionIcons.create("to_graveyard"));
        registry.register(ActionIcons.create("attack"));
        registry.register(ActionIcons.create("attack_directly"));
        registry.register(ActionIcons.create("to_extra_atk"));
        registry.register(ActionIcons.create("to_extra_fd"));
        
        registry.register(ActionIcons.create("all_to_graveyard"));
    }
    
    public static ActionIcon create(String name)
    {
        return new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/" + name + ".png"), 64).setRegistryName(new ResourceLocation(YDM.MOD_ID, name));
    }
}