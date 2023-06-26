package de.cas_ual_ty.ydm.duel.network;

import de.cas_ual_ty.ydm.YDM;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DuelMessageHeaders
{
    private static final DeferredRegister<DuelMessageHeaderType> DEFERRED_REGISTER = DeferredRegister.create(new ResourceLocation(YDM.MOD_ID, "duel_message_headers"), YDM.MOD_ID);
    
    public static final RegistryObject<DuelMessageHeaderType> CONTAINER = DEFERRED_REGISTER.register("container", () -> new DuelMessageHeaderType(() -> new DuelMessageHeader.ContainerHeader(DuelMessageHeaders.CONTAINER.get())));
    public static final RegistryObject<DuelMessageHeaderType> TILE_ENTITY = DEFERRED_REGISTER.register("tile_entity", () -> new DuelMessageHeaderType(() -> new DuelMessageHeader.TileEntityHeader(DuelMessageHeaders.TILE_ENTITY.get())));
    public static final RegistryObject<DuelMessageHeaderType> ENTITY = DEFERRED_REGISTER.register("entity", () -> new DuelMessageHeaderType(() -> new DuelMessageHeader.EntityHeader(DuelMessageHeaders.ENTITY.get())));
    
    public static void register(IEventBus bus)
    {
        DEFERRED_REGISTER.register(bus);
    }
}