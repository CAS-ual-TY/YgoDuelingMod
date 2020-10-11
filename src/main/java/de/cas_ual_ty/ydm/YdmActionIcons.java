package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.duelmanager.action.ActionIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmActionIcons
{
    public static final ActionIcon ADD_TO_HAND = null;
    
    @SubscribeEvent
    public static void registerActionIcons(RegistryEvent.Register<ActionIcon> event)
    {
        IForgeRegistry<ActionIcon> registry = event.getRegistry();
        
        ResourceLocation rl = new ResourceLocation(YDM.MOD_ID, "textures/gui/duel_actions.png");
        int fileSize = 256;
        int iconWidth = 64;
        int iconHeight = 32;
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)0).setRegistryName(YDM.MOD_ID, "to_top_of_deck_fd"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)1).setRegistryName(YDM.MOD_ID, "def_to_atk"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)2).setRegistryName(YDM.MOD_ID, "normal_summon"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)3).setRegistryName(YDM.MOD_ID, "banish_fa"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)4).setRegistryName(YDM.MOD_ID, "to_bottom_of_deck_fd"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)5).setRegistryName(YDM.MOD_ID, "atk_to_def"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)6).setRegistryName(YDM.MOD_ID, "set_spell_trap_fd"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)7).setRegistryName(YDM.MOD_ID, "banish_fd"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)8).setRegistryName(YDM.MOD_ID, "to_top_of_deck_fa"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)9).setRegistryName(YDM.MOD_ID, "atk_def_to_set"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)10).setRegistryName(YDM.MOD_ID, "set_monster"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)11).setRegistryName(YDM.MOD_ID, "questionmark1"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)12).setRegistryName(YDM.MOD_ID, "overlay_to_top"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)13).setRegistryName(YDM.MOD_ID, "special_summon_atk"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)14).setRegistryName(YDM.MOD_ID, "special_summon_def"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)15).setRegistryName(YDM.MOD_ID, "questionmark2"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)16).setRegistryName(YDM.MOD_ID, "overlay_to_bottom"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)17).setRegistryName(YDM.MOD_ID, "add_to_hand"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)18).setRegistryName(YDM.MOD_ID, "shuffle_deck"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)19).setRegistryName(YDM.MOD_ID, "questionmark3"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)20).setRegistryName(YDM.MOD_ID, "questionmark4"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)21).setRegistryName(YDM.MOD_ID, "shuffle_hand"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)22).setRegistryName(YDM.MOD_ID, "view_deck"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)23).setRegistryName(YDM.MOD_ID, "questionmark5"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)24).setRegistryName(YDM.MOD_ID, "questionmark6"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)25).setRegistryName(YDM.MOD_ID, "questionmark7"));
        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)26).setRegistryName(YDM.MOD_ID, "special_summon_set"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)27).setRegistryName(YDM.MOD_ID, ""));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)28).setRegistryName(YDM.MOD_ID, "questionmark8"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)29).setRegistryName(YDM.MOD_ID, "questionmark9"));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)30).setRegistryName(YDM.MOD_ID, ""));
        //        registry.register(new ActionIcon(rl, fileSize, iconWidth, iconHeight, (byte)31).setRegistryName(YDM.MOD_ID, ""));
        
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