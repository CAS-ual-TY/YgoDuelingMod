package de.cas_ual_ty.ydm.duelmanager.network;

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
    public static final DuelMessageHeader DUEL_CONTAINER = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<DuelMessageHeader> event)
    {
        IForgeRegistry<DuelMessageHeader> registry = event.getRegistry();
        registry.register(new DuelMessageHeader.ContainerHeader().setRegistryName(YDM.MOD_ID, "duel_container"));
    }
}