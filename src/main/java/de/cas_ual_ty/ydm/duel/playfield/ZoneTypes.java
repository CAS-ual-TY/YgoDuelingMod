package de.cas_ual_ty.ydm.duel.playfield;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ZoneTypes
{
    private static final DeferredRegister<ZoneType> DEFERRED_REGISTER = DeferredRegister.create(new ResourceLocation(YDM.MOD_ID, "zone_types"), YDM.MOD_ID);
    
    public static final RegistryObject<ZoneType> HAND = DEFERRED_REGISTER.register("hand", () -> new ZoneType().showFaceDownCardsToOwner());
    public static final RegistryObject<ZoneType> DECK = DEFERRED_REGISTER.register("deck", () -> new ZoneType().secret().keepFocusedAfterInteraction().defaultCardPosition(CardPosition.FD));
    public static final RegistryObject<ZoneType> SPELL_TRAP = DEFERRED_REGISTER.register("spell_trap", () -> new ZoneType().canHaveCounters());
    public static final RegistryObject<ZoneType> EXTRA_DECK = DEFERRED_REGISTER.register("extra_deck", () -> new ZoneType().keepFocusedAfterInteraction());
    public static final RegistryObject<ZoneType> GRAVEYARD = DEFERRED_REGISTER.register("graveyard", () -> new ZoneType().strict().keepFocusedAfterInteraction());
    public static final RegistryObject<ZoneType> MONSTER = DEFERRED_REGISTER.register("monster", () -> new ZoneType().allowSideways().canHaveCounters());
    public static final RegistryObject<ZoneType> FIELD_SPELL = DEFERRED_REGISTER.register("field_spell", () -> new ZoneType().canHaveCounters());
    public static final RegistryObject<ZoneType> BANISHED = DEFERRED_REGISTER.register("banished", () -> new ZoneType().strict().keepFocusedAfterInteraction());
    public static final RegistryObject<ZoneType> EXTRA = DEFERRED_REGISTER.register("extra", () -> new ZoneType().keepFocusedAfterInteraction());
    public static final RegistryObject<ZoneType> EXTRA_MONSTER_RIGHT = DEFERRED_REGISTER.register("extra_monster_right", () -> new ZoneType().noOwner().canHaveCounters());
    public static final RegistryObject<ZoneType> EXTRA_MONSTER_LEFT = DEFERRED_REGISTER.register("extra_monster_left", () -> new ZoneType().noOwner().canHaveCounters());
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register(bus);
    }
}