package de.cas_ual_ty.ydm.duel.network;

import de.cas_ual_ty.ydm.YDM;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class DuelMessageHeaders
{
    public static final DuelMessageHeaderType CONTAINER = null;
    public static final DuelMessageHeaderType TILE_ENTITY = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<DuelMessageHeaderType> event)
    {
        IForgeRegistry<DuelMessageHeaderType> registry = event.getRegistry();
        registry.register(new DuelMessageHeaderType(() -> new DuelMessageHeader.ContainerHeader(DuelMessageHeaders.CONTAINER)).setRegistryName(YDM.MOD_ID, "container"));
        registry.register(new DuelMessageHeaderType(() -> new DuelMessageHeader.TileEntityHeader(DuelMessageHeaders.TILE_ENTITY)).setRegistryName(YDM.MOD_ID, "tile_entity"));
    }
}