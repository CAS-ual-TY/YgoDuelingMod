package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.cardbinder.CardBinderContainer;
import de.cas_ual_ty.ydm.carditeminventory.CIIContainer;
import de.cas_ual_ty.ydm.cardsupply.CardSupplyContainer;
import de.cas_ual_ty.ydm.deckbox.DeckBoxContainer;
import de.cas_ual_ty.ydm.duel.block.DuelBlockContainer;
import de.cas_ual_ty.ydm.set.CardSetContainer;
import de.cas_ual_ty.ydm.simplebinder.SimpleBinderContainer;
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
    public static final ContainerType<CardSupplyContainer> CARD_SUPPLY = null;
    public static final ContainerType<CIIContainer> CARD_SET = null;
    public static final ContainerType<CIIContainer> SIMPLE_BINDER = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<ContainerType<?>> event)
    {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(new ContainerType<>((id, playerInv) -> new CardBinderContainer(YdmContainerTypes.CARD_BINDER, id, playerInv)).setRegistryName(YDM.MOD_ID, "card_binder"));
        registry.register(new ContainerType<>((id, playerInv) -> new DeckBoxContainer(YdmContainerTypes.DECK_BOX, id, playerInv)).setRegistryName(YDM.MOD_ID, "deck_box"));
        registry.register(new ContainerType<>((IContainerFactory<DuelBlockContainer>)(id, playerInv, extraData) -> new DuelBlockContainer(YdmContainerTypes.DECK_BOX, id, playerInv, extraData)).setRegistryName(YDM.MOD_ID, "duel_block_container"));
        registry.register(new ContainerType<>((IContainerFactory<CardSupplyContainer>)(id, playerInv, extraData) -> new CardSupplyContainer(YdmContainerTypes.CARD_SUPPLY, id, playerInv, extraData)).setRegistryName(YDM.MOD_ID, "card_supply"));
        registry.register(new ContainerType<>((IContainerFactory<CIIContainer>)(id, playerInv, extraData) -> new CardSetContainer(YdmContainerTypes.CARD_SET, id, playerInv, extraData)).setRegistryName(YDM.MOD_ID, "card_set"));
        registry.register(new ContainerType<>((IContainerFactory<CIIContainer>)(id, playerInv, extraData) -> new SimpleBinderContainer(YdmContainerTypes.CARD_SET, id, playerInv, extraData)).setRegistryName(YDM.MOD_ID, "simple_binder"));
    }
}