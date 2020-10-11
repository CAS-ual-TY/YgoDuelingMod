package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.duelmanager.CardPosition;
import de.cas_ual_ty.ydm.duelmanager.playfield.ZoneType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmZoneTypes
{
    public static final ZoneType HAND = null;
    public static final ZoneType DECK = null;
    public static final ZoneType SPELL_TRAP = null;
    public static final ZoneType EXTRA_DECK = null;
    public static final ZoneType GRAVEYARD = null;
    public static final ZoneType MONSTER = null;
    public static final ZoneType FIELD_SPELL = null;
    public static final ZoneType BANISHED = null;
    public static final ZoneType EXTRA = null;
    public static final ZoneType EXTRA_MONSTER_RIGHT = null;
    public static final ZoneType EXTRA_MONSTER_LEFT = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<ZoneType> event)
    {
        IForgeRegistry<ZoneType> registry = event.getRegistry();
        registry.register(new ZoneType().setRegistryName(YDM.MOD_ID, "hand"));
        registry.register(new ZoneType().secret().defaultCardPosition(CardPosition.FACE_DOWN).setRegistryName(YDM.MOD_ID, "deck"));
        registry.register(new ZoneType().setRegistryName(YDM.MOD_ID, "spell_trap"));
        registry.register(new ZoneType().setRegistryName(YDM.MOD_ID, "extra_deck"));
        registry.register(new ZoneType().strict().setRegistryName(YDM.MOD_ID, "graveyard"));
        registry.register(new ZoneType().allowSideways().setRegistryName(YDM.MOD_ID, "monster"));
        registry.register(new ZoneType().setRegistryName(YDM.MOD_ID, "field_spell"));
        registry.register(new ZoneType().strict().setRegistryName(YDM.MOD_ID, "banished"));
        registry.register(new ZoneType().setRegistryName(YDM.MOD_ID, "extra"));
        registry.register(new ZoneType().noOwner().setRegistryName(YDM.MOD_ID, "extra_monster_right"));
        registry.register(new ZoneType().noOwner().setRegistryName(YDM.MOD_ID, "extra_monster_left"));
    }
    
}