package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.cardbinder.CardBinderContainer;
import de.cas_ual_ty.ydm.deckbox.DeckBoxContainer;
import de.cas_ual_ty.ydm.duel.block.DuelBlockContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = YDM.MOD_ID, bus = Bus.MOD)
@ObjectHolder(YDM.MOD_ID)
public class YdmContainerTypes
{
    public static final ContainerType<CardBinderContainer> CARD_BINDER = null;
    public static final ContainerType<DeckBoxContainer> DECK_BOX = null;
    public static final ContainerType<DuelBlockContainer> DUEL_BLOCK_CONTAINER = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<ContainerType<?>> event)
    {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(new ContainerType<>((id, playerInv) -> new CardBinderContainer(YdmContainerTypes.CARD_BINDER, id, playerInv)).setRegistryName(YDM.MOD_ID, "card_binder"));
        registry.register(new ContainerType<>((id, playerInv) -> new DeckBoxContainer(YdmContainerTypes.DECK_BOX, id, playerInv)).setRegistryName(YDM.MOD_ID, "deck_box"));
        registry.register(new ContainerType<>((IContainerFactory<DuelBlockContainer>)(id, playerInv, extraData) -> new DuelBlockContainer(YdmContainerTypes.DECK_BOX, id, playerInv, extraData)).setRegistryName(YDM.MOD_ID, "duel_block_container"));
    }
}