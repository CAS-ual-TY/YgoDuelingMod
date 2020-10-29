package de.cas_ual_ty.ydm.duelmanager.action;

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
    public static final ActionIcon DEF_TO_ATK = null;
    public static final ActionIcon NORMAL_SUMMON = null;
    public static final ActionIcon BANISH_FA = null;
    public static final ActionIcon TO_BOTTOM_OF_DECK_FD = null;
    public static final ActionIcon ATK_TO_DEF = null;
    public static final ActionIcon SET_SPELL_TRAP = null;
    public static final ActionIcon BANISH_FD = null;
    public static final ActionIcon TO_TOP_OF_DECK_FA = null;
    public static final ActionIcon ATK_DEF_TO_SET = null;
    public static final ActionIcon SET = null;
    public static final ActionIcon OVERLAY = null;
    public static final ActionIcon SPECIAL_SUMMON_ATK = null;
    public static final ActionIcon SPECIAL_SUMMON_DEF = null;
    public static final ActionIcon UNDERLAY = null;
    public static final ActionIcon ADD_TO_HAND = null;
    public static final ActionIcon SHUFFLE_DECK = null;
    public static final ActionIcon SHUFFLE_HAND = null;
    public static final ActionIcon VIEW_DECK = null;
    public static final ActionIcon SPECIAL_SUMMON_SET = null;
    public static final ActionIcon ACTIVATE_SPELL_TRAP = null;
    
    public static final ActionIcon SHOW_HAND = null;
    public static final ActionIcon SHOW_DECK = null;
    public static final ActionIcon SHOW_CARD = null;
    public static final ActionIcon MOVE = null;
    public static final ActionIcon TO_GRAVEYARD = null;
    public static final ActionIcon ATTACK = null;
    
    @SubscribeEvent
    public static void registerActionIcons(RegistryEvent.Register<ActionIcon> event)
    {
        IForgeRegistry<ActionIcon> registry = event.getRegistry();
        
        ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_actions.png");
        int fileSize = 256;
        int iconWidth = 64;
        int iconHeight = 32;
        int newFileSize = 64;
        
        registry.register(ActionIcons.create("to_top_of_deck_fd", newFileSize));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)1).setRegistryName(YDM.MOD_ID, "def_to_atk"));
        registry.register(ActionIcons.create("normal_summon", newFileSize));
        registry.register(ActionIcons.create("banish_fa", newFileSize));
        registry.register(ActionIcons.create("to_bottom_of_deck_fd", newFileSize));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)5).setRegistryName(YDM.MOD_ID, "atk_to_def"));
        registry.register(ActionIcons.create("set_spell_trap", newFileSize));
        registry.register(ActionIcons.create("banish_fd", newFileSize));
        registry.register(ActionIcons.create("to_top_of_deck_fa", newFileSize));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)9).setRegistryName(YDM.MOD_ID, "atk_def_to_set"));
        registry.register(ActionIcons.create("set", newFileSize));
        registry.register(ActionIcons.create("overlay", newFileSize));
        registry.register(ActionIcons.create("special_summon_atk", newFileSize));
        registry.register(ActionIcons.create("special_summon_def", newFileSize));
        registry.register(ActionIcons.create("underlay", newFileSize));
        registry.register(ActionIcons.create("add_to_hand", newFileSize));
        registry.register(ActionIcons.create("shuffle_deck", newFileSize));
        registry.register(ActionIcons.create("shuffle_hand", newFileSize));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)22).setRegistryName(YDM.MOD_ID, "view_deck"));
        registry.register(ActionIcons.create("special_summon_set", newFileSize));
        registry.register(ActionIcons.create("activate_spell_trap", newFileSize));
        
        rl = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_actions_large.png");
        fileSize = 256;
        iconWidth = 64;
        iconHeight = 64;
        registry.register(ActionIcons.create("show_hand", newFileSize));
        registry.register(ActionIcons.create("show_deck", newFileSize));
        registry.register(ActionIcons.create("show_card", newFileSize));
        registry.register(ActionIcons.create("move", newFileSize));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)4).setRegistryName(YDM.MOD_ID, "to_graveyard"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)5).setRegistryName(YDM.MOD_ID, "attack"));
    }
    
    public static ActionIcon create(String name, int fileSize)
    {
        return new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/" + name + ".png"), fileSize).setRegistryName(new ResourceLocation(YDM.MOD_ID, name));
    }
}