package de.cas_ual_ty.ydm.duel.playfield;

import de.cas_ual_ty.ydm.YDM;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class ZoneTypes
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
    public static void registerZoneTypes(RegistryEvent.Register<ZoneType> event)
    {
        IForgeRegistry<ZoneType> registry = event.getRegistry();
        registry.register(new ZoneType().showFaceDownCardsToOwner().setRegistryName(YDM.MOD_ID, "hand"));
        registry.register(new ZoneType().secret().keepFocusedAfterInteraction().defaultCardPosition(CardPosition.FD).setRegistryName(YDM.MOD_ID, "deck"));
        registry.register(new ZoneType().canHaveCounters().setRegistryName(YDM.MOD_ID, "spell_trap"));
        registry.register(new ZoneType().keepFocusedAfterInteraction().setRegistryName(YDM.MOD_ID, "extra_deck"));
        registry.register(new ZoneType().strict().keepFocusedAfterInteraction().setRegistryName(YDM.MOD_ID, "graveyard"));
        registry.register(new ZoneType().allowSideways().canHaveCounters().setRegistryName(YDM.MOD_ID, "monster"));
        registry.register(new ZoneType().canHaveCounters().setRegistryName(YDM.MOD_ID, "field_spell"));
        registry.register(new ZoneType().strict().keepFocusedAfterInteraction().setRegistryName(YDM.MOD_ID, "banished"));
        registry.register(new ZoneType().keepFocusedAfterInteraction().setRegistryName(YDM.MOD_ID, "extra"));
        registry.register(new ZoneType().noOwner().canHaveCounters().setRegistryName(YDM.MOD_ID, "extra_monster_right"));
        registry.register(new ZoneType().noOwner().canHaveCounters().setRegistryName(YDM.MOD_ID, "extra_monster_left"));
    }
}