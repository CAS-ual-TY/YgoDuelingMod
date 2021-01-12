package de.cas_ual_ty.ydm;

import java.io.File;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.cas_ual_ty.ydm.cardbinder.CardBinderCardsManager;
import de.cas_ual_ty.ydm.cardbinder.CardBinderMessages;
import de.cas_ual_ty.ydm.cardinventory.JsonCardsManager;
import de.cas_ual_ty.ydm.cardsupply.CardSupplyMessages;
import de.cas_ual_ty.ydm.deckbox.DeckBoxItem;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.deckbox.ItemHandlerDeckHolder;
import de.cas_ual_ty.ydm.duel.FindDecksEvent;
import de.cas_ual_ty.ydm.duel.action.ActionIcon;
import de.cas_ual_ty.ydm.duel.action.ActionType;
import de.cas_ual_ty.ydm.duel.network.DuelMessage;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeaderType;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.ZoneType;
import de.cas_ual_ty.ydm.serverutil.YdmCommand;
import de.cas_ual_ty.ydm.task.WorkerManager;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(YDM.MOD_ID)
public class YDM
{
    public static final String MOD_ID = "ydm";
    public static final String MOD_ID_UP = YDM.MOD_ID.toUpperCase();
    public static final String PROTOCOL_VERSION = "1";
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static YDM instance;
    public static ISidedProxy proxy;
    public static Random random;
    public static ItemGroup ydmItemGroup;
    public static ItemGroup cardsItemGroup;
    public static ItemGroup setsItemGroup;
    
    public static ForgeConfigSpec commonConfigSpec;
    public static CommonConfig commonConfig;
    
    public static String dbSourceUrl;
    
    public static File mainFolder;
    public static File cardsFolder;
    public static File setsFolder;
    public static File distributionsFolder;
    public static File bindersFolder;
    
    public static SimpleChannel channel;
    
    @CapabilityInject(CardBinderCardsManager.class)
    public static Capability<CardBinderCardsManager> BINDER_INVENTORY_CAPABILITY = null;
    
    public static IForgeRegistry<ActionIcon> actionIconRegistry;
    public static IForgeRegistry<ZoneType> zoneTypeRegistry;
    public static IForgeRegistry<ActionType> actionTypeRegistry;
    public static IForgeRegistry<DuelMessageHeaderType> duelMessageHeaderRegistry;
    public static volatile boolean continueTasks = true;
    public static volatile boolean forceTaskStop = false;
    
    public YDM()
    {
        YDM.instance = this;
        YDM.proxy = DistExecutor.safeRunForDist(
            () -> de.cas_ual_ty.ydm.clientutil.ClientProxy::new,
            () -> de.cas_ual_ty.ydm.serverutil.ServerProxy::new);
        YDM.random = new Random();
        YDM.ydmItemGroup = new YdmItemGroup(YDM.MOD_ID, () -> YdmItems.CARD_BACK);
        YDM.cardsItemGroup = new YdmItemGroup(YDM.MOD_ID + ".cards", () -> YdmItems.BLANC_CARD)
        {
            @Override
            public boolean hasSearchBar()
            {
                return true;
            }
        }.setBackgroundImageName("item_search.png");
        YDM.setsItemGroup = new YdmItemGroup(YDM.MOD_ID + ".sets", () -> YdmItems.BLANC_SET)
        {
            @Override
            public boolean hasSearchBar()
            {
                return true;
            }
        }.setBackgroundImageName("item_search.png");
        
        Pair<CommonConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        YDM.commonConfig = common.getLeft();
        YDM.commonConfigSpec = common.getRight();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, YDM.commonConfigSpec);
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        bus.addListener(this::modConfig);
        bus.addListener(this::newRegistry);
        YDM.proxy.registerModEventListeners(bus);
        
        bus = MinecraftForge.EVENT_BUS;
        // see: https://github.com/MinecraftForge/MinecraftForge/pull/6954
        // need to write directly to nbt for now
        // bus.addGenericListener(ItemStack.class, this::attachItemStackCapabilities);
        bus.addListener(this::registerCommands);
        bus.addListener(this::findDecks);
        bus.addListener(this::serverStopped);
        YDM.proxy.registerForgeEventListeners(bus);
        
        YDM.proxy.preInit();
    }
    
    private void init(FMLCommonSetupEvent event)
    {
        YDM.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(YDM.MOD_ID, "main"),
            () -> YDM.PROTOCOL_VERSION,
            YDM.PROTOCOL_VERSION::equals,
            YDM.PROTOCOL_VERSION::equals);
        
        CapabilityManager.INSTANCE.<CardBinderCardsManager>register(CardBinderCardsManager.class, new CardBinderCardsManager.Storage(), CardBinderCardsManager::new);
        
        this.initFiles();
        
        int index = 0;
        YDM.channel.registerMessage(index++, CardBinderMessages.ChangePage.class, CardBinderMessages.ChangePage::encode, CardBinderMessages.ChangePage::decode, CardBinderMessages.ChangePage::handle);
        YDM.channel.registerMessage(index++, CardBinderMessages.UpdatePage.class, CardBinderMessages.UpdatePage::encode, CardBinderMessages.UpdatePage::decode, CardBinderMessages.UpdatePage::handle);
        YDM.channel.registerMessage(index++, CardBinderMessages.UpdateList.class, CardBinderMessages.UpdateList::encode, CardBinderMessages.UpdateList::decode, CardBinderMessages.UpdateList::handle);
        YDM.channel.registerMessage(index++, CardBinderMessages.IndexClicked.class, CardBinderMessages.IndexClicked::encode, CardBinderMessages.IndexClicked::decode, CardBinderMessages.IndexClicked::handle);
        YDM.channel.registerMessage(index++, CardBinderMessages.IndexDropped.class, CardBinderMessages.IndexDropped::encode, CardBinderMessages.IndexDropped::decode, CardBinderMessages.IndexDropped::handle);
        YDM.channel.registerMessage(index++, CardSupplyMessages.RequestCard.class, CardSupplyMessages.RequestCard::encode, CardSupplyMessages.RequestCard::decode, CardSupplyMessages.RequestCard::handle);
        DuelMessage.register(YDM.channel, index++, DuelMessages.SelectRole.class, DuelMessages.SelectRole::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.UpdateRole.class, DuelMessages.UpdateRole::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.UpdateDuelState.class, DuelMessages.UpdateDuelState::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.RequestFullUpdate.class, DuelMessages.RequestFullUpdate::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.RequestReady.class, DuelMessages.RequestReady::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.UpdateReady.class, DuelMessages.UpdateReady::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.SendAvailableDecks.class, DuelMessages.SendAvailableDecks::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.RequestDeck.class, DuelMessages.RequestDeck::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.SendDeck.class, DuelMessages.SendDeck::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.ChooseDeck.class, DuelMessages.ChooseDeck::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.DeckAccepted.class, DuelMessages.DeckAccepted::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.DuelAction.class, DuelMessages.DuelAction::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.RequestDuelAction.class, DuelMessages.RequestDuelAction::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.AllDuelActions.class, DuelMessages.AllDuelActions::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.SendMessageToServer.class, DuelMessages.SendMessageToServer::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.SendMessageToClient.class, DuelMessages.SendMessageToClient::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.SendAllMessagesToClient.class, DuelMessages.SendAllMessagesToClient::new);
        
        YDM.proxy.init();
        WorkerManager.init();
    }
    
    private void initFiles()
    {
        YDM.mainFolder = new File("ydm_db");
        YDM.cardsFolder = new File(YDM.mainFolder, "cards");
        YDM.setsFolder = new File(YDM.mainFolder, "sets");
        YDM.distributionsFolder = new File(YDM.mainFolder, "distributions");
        
        YDM.bindersFolder = new File("ydm_binders");
        YdmIOUtil.createDirIfNonExistant(YDM.bindersFolder);
        
        YDM.proxy.initFiles();
        YdmIOUtil.setAgent();
        YdmDatabase.initDatabase();
    }
    
    /*
    private void attachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        if(event.getObject() instanceof ItemStack && event.getObject().getItem() == YdmItems.CARD_BINDER)
        {
            final LazyOptional<CardBinderCardsManager> instance = LazyOptional.of(CardBinderCardsManager::new);
            final ICapabilitySerializable<INBT> provider = new ICapabilitySerializable<INBT>()
            {
                @Override
                public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
                {
                    return YDM.BINDER_INVENTORY_CAPABILITY.orEmpty(cap, instance);
                }
                
                @Override
                public INBT serializeNBT()
                {
                    return YDM.BINDER_INVENTORY_CAPABILITY.writeNBT(instance.orElseThrow(YdmUtil.throwNullCapabilityException()), null);
                }
                
                @Override
                public void deserializeNBT(INBT nbt)
                {
                    YDM.BINDER_INVENTORY_CAPABILITY.readNBT(instance.orElseThrow(YdmUtil.throwNullCapabilityException()), null, nbt);
                }
            };
            event.addCapability(new ResourceLocation(YDM.MOD_ID, "card_inventory_manager"), provider);
            event.addListener(instance::invalidate);
        }
        else if(event.getObject() instanceof ItemStack && event.getObject().getItem() instanceof DeckBoxItem)
        {
            final LazyOptional<IItemHandler> instance = LazyOptional.of(() -> new ItemStackHandler(DeckHolder.TOTAL_DECK_SIZE)
            {
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack)
                {
                    return stack.getItem() == YdmItems.CARD;
                }
            });
            final ICapabilitySerializable<INBT> provider = new ICapabilitySerializable<INBT>()
            {
                @Override
                public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
                {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, instance);
                }
                
                @Override
                public INBT serializeNBT()
                {
                    return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(instance.orElseThrow(YdmUtil.throwNullCapabilityException()), null);
                }
                
                @Override
                public void deserializeNBT(INBT nbt)
                {
                    CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(instance.orElseThrow(YdmUtil.throwNullCapabilityException()), null, nbt);
                }
            };
            event.addCapability(new ResourceLocation(YDM.MOD_ID, "item_handler"), provider);
            event.addListener(instance::invalidate);
        }
    }
    */
    
    private void registerCommands(RegisterCommandsEvent event)
    {
        YdmCommand.registerCommand(event.getDispatcher());
    }
    
    private void modConfig(final ModConfig.ModConfigEvent event)
    {
        if(event.getConfig().getSpec() == YDM.commonConfigSpec)
        {
            YDM.log("Baking common config!");
            YDM.dbSourceUrl = YDM.commonConfig.dbSourceUrl.get();
        }
    }
    
    private void findDecks(FindDecksEvent event)
    {
        PlayerEntity player = event.getPlayer();
        
        ItemStack itemStack;
        DeckHolder dh;
        
        for(int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            itemStack = player.inventory.getStackInSlot(i);
            
            if(itemStack.getItem() instanceof DeckBoxItem)
            {
                dh = new ItemHandlerDeckHolder(((DeckBoxItem)itemStack.getItem()).getItemHandler(itemStack));
                
                if(!dh.isEmpty())
                {
                    event.addDeck(dh, itemStack);
                }
            }
        }
        
        itemStack = player.getHeldItemOffhand();
        if(itemStack.getItem() instanceof DeckBoxItem)
        {
            dh = new ItemHandlerDeckHolder(((DeckBoxItem)itemStack.getItem()).getItemHandler(itemStack));
            
            if(!dh.isEmpty())
            {
                event.addDeck(dh, itemStack);
            }
        }
    }
    
    private void newRegistry(NewRegistry event)
    {
        YDM.actionIconRegistry = new RegistryBuilder<ActionIcon>().setName(new ResourceLocation(YDM.MOD_ID, "action_icons")).setType(ActionIcon.class).setMaxID(511).create();
        YDM.zoneTypeRegistry = new RegistryBuilder<ZoneType>().setName(new ResourceLocation(YDM.MOD_ID, "zone_types")).setType(ZoneType.class).setMaxID(511).create();
        YDM.actionTypeRegistry = new RegistryBuilder<ActionType>().setName(new ResourceLocation(YDM.MOD_ID, "action_types")).setType(ActionType.class).setMaxID(511).create();
        YDM.duelMessageHeaderRegistry = new RegistryBuilder<DuelMessageHeaderType>().setName(new ResourceLocation(YDM.MOD_ID, "duel_message_headers")).setType(DuelMessageHeaderType.class).setMaxID(63).create();
    }
    
    private void serverStopped(FMLServerStoppedEvent event)
    {
        synchronized(JsonCardsManager.LOADED_MANAGERS)
        {
            for(JsonCardsManager m : JsonCardsManager.LOADED_MANAGERS)
            {
                m.safe(() ->
                {});
            }
        }
    }
    
    public static void log(String s)
    {
        YDM.LOGGER.info("[" + YDM.MOD_ID + "] " + s);
    }
    
    public static void debug(String s)
    {
        YDM.LOGGER.debug("[" + YDM.MOD_ID + "_debug] " + s);
    }
    
    public static void debug(Object s)
    {
        if(s == null)
        {
            YDM.debug("null");
        }
        else
        {
            YDM.debug(s.toString());
        }
    }
}
