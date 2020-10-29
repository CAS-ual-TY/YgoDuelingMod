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
    public static final ActionIcon SET_SPELL_TRAP_FD = null;
    public static final ActionIcon BANISH_FD = null;
    public static final ActionIcon TO_TOP_OF_DECK_FA = null;
    public static final ActionIcon ATK_DEF_TO_SET = null;
    public static final ActionIcon SET_MONSTER = null;
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
        
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/to_top_of_deck_fd.png"), newFileSize).setRegistryName(YDM.MOD_ID, "to_top_of_deck_fd"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)1).setRegistryName(YDM.MOD_ID, "def_to_atk"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/normal_summon.png"), newFileSize).setRegistryName(YDM.MOD_ID, "normal_summon"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/banish_fa.png"), newFileSize).setRegistryName(YDM.MOD_ID, "banish_fa"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/to_bottom_of_deck_fd.png"), newFileSize).setRegistryName(YDM.MOD_ID, "to_bottom_of_deck_fd"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)5).setRegistryName(YDM.MOD_ID, "atk_to_def"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/set_spell_trap.png"), newFileSize).setRegistryName(YDM.MOD_ID, "set_spell_trap_fd"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/banish_fd.png"), newFileSize).setRegistryName(YDM.MOD_ID, "banish_fd"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/to_top_of_deck_fa.png"), newFileSize).setRegistryName(YDM.MOD_ID, "to_top_of_deck_fa"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)9).setRegistryName(YDM.MOD_ID, "atk_def_to_set"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/set.png"), newFileSize).setRegistryName(YDM.MOD_ID, "set_monster"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/overlay.png"), newFileSize).setRegistryName(YDM.MOD_ID, "overlay"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/special_summon_atk.png"), newFileSize).setRegistryName(YDM.MOD_ID, "special_summon_atk"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/special_summon_def.png"), newFileSize).setRegistryName(YDM.MOD_ID, "special_summon_def"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/underlay.png"), newFileSize).setRegistryName(YDM.MOD_ID, "underlay"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/add_to_hand.png"), newFileSize).setRegistryName(YDM.MOD_ID, "add_to_hand"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/shuffle_deck.png"), newFileSize).setRegistryName(YDM.MOD_ID, "shuffle_deck"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/shuffle_hand.png"), newFileSize).setRegistryName(YDM.MOD_ID, "shuffle_hand"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)22).setRegistryName(YDM.MOD_ID, "view_deck"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/special_summon_set.png"), newFileSize).setRegistryName(YDM.MOD_ID, "special_summon_set"));
        registry.register(new ActionIcon(new ResourceLocation(YDM.MOD_ID, "textures/gui/action_icons/activate_spell_trap.png"), newFileSize).setRegistryName(YDM.MOD_ID, "activate_spell_trap"));
        
        rl = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_actions_large.png");
        fileSize = 256;
        iconWidth = 64;
        iconHeight = 64;
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)0).setRegistryName(YDM.MOD_ID, "show_hand"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)1).setRegistryName(YDM.MOD_ID, "show_deck"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)2).setRegistryName(YDM.MOD_ID, "show_card"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)3).setRegistryName(YDM.MOD_ID, "move"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)4).setRegistryName(YDM.MOD_ID, "to_graveyard"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)5).setRegistryName(YDM.MOD_ID, "attack"));
    }
}