package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.cardbinder.CardBinderMessages;
import de.cas_ual_ty.ydm.cardbinder.UUIDHolder;
import de.cas_ual_ty.ydm.cardinventory.JsonCardsManager;
import de.cas_ual_ty.ydm.carditeminventory.CIIMessages;
import de.cas_ual_ty.ydm.cardsupply.CardSupplyMessages;
import de.cas_ual_ty.ydm.deckbox.DeckBoxItem;
import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.deckbox.ItemHandlerDeckHolder;
import de.cas_ual_ty.ydm.duel.FindDecksEvent;
import de.cas_ual_ty.ydm.duel.action.ActionIcon;
import de.cas_ual_ty.ydm.duel.action.ActionIcons;
import de.cas_ual_ty.ydm.duel.action.ActionType;
import de.cas_ual_ty.ydm.duel.action.ActionTypes;
import de.cas_ual_ty.ydm.duel.network.DuelMessage;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeaderType;
import de.cas_ual_ty.ydm.duel.network.DuelMessageHeaders;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import de.cas_ual_ty.ydm.duel.playfield.ZoneType;
import de.cas_ual_ty.ydm.duel.playfield.ZoneTypes;
import de.cas_ual_ty.ydm.serverutil.YdmCommand;
import de.cas_ual_ty.ydm.simplebinder.SimpleBinderItem;
import de.cas_ual_ty.ydm.task.WorkerManager;
import de.cas_ual_ty.ydm.util.CooldownHolder;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Random;
import java.util.function.Supplier;

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
    public static CreativeModeTab ydmItemGroup;
    public static CreativeModeTab cardsItemGroup;
    public static CreativeModeTab setsItemGroup;
    
    public static ForgeConfigSpec commonConfigSpec;
    public static CommonConfig commonConfig;
    
    public static String dbSourceUrl;
    
    public static File mainFolder;
    public static File cardsFolder;
    public static File setsFolder;
    public static File distributionsFolder;
    public static File bindersFolder;
    
    public static SimpleChannel channel;
    
    public static Capability<UUIDHolder> UUID_HOLDER = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<YDMItemHandler> CARD_ITEM_INVENTORY = CapabilityManager.get(new CapabilityToken<>() {});
    public static Capability<CooldownHolder> COOLDOWN_HOLDER = CapabilityManager.get(new CapabilityToken<>() {});
    
    public static Supplier<IForgeRegistry<ActionIcon>> actionIconRegistry;
    public static Supplier<IForgeRegistry<ZoneType>> zoneTypeRegistry;
    public static Supplier<IForgeRegistry<ActionType>> actionTypeRegistry;
    public static Supplier<IForgeRegistry<DuelMessageHeaderType>> duelMessageHeaderRegistry;
    public static volatile boolean continueTasks = true;
    public static volatile boolean forceTaskStop = false;
    
    public YDM()
    {
        YDM.instance = this;
        YDM.proxy = DistExecutor.safeRunForDist(
                () -> de.cas_ual_ty.ydm.clientutil.ClientProxy::new,
                () -> de.cas_ual_ty.ydm.serverutil.ServerProxy::new);
        YDM.random = new Random();
        YDM.ydmItemGroup = new YdmItemGroup(YDM.MOD_ID, YdmItems.CARD_BACK);
        YDM.cardsItemGroup = new YdmItemGroup(YDM.MOD_ID + ".cards", YdmItems.BLANC_CARD)
        {
            @Override
            public boolean hasSearchBar()
            {
                return true;
            }
        }.setBackgroundSuffix("item_search.png");
        YDM.setsItemGroup = new YdmItemGroup(YDM.MOD_ID + ".sets", YdmItems.BLANC_SET)
        {
            @Override
            public boolean hasSearchBar()
            {
                return true;
            }
        }.setBackgroundSuffix("item_search.png");
        
        Pair<CommonConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        YDM.commonConfig = common.getLeft();
        YDM.commonConfigSpec = common.getRight();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, YDM.commonConfigSpec);
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        bus.addListener(this::modConfig);
        bus.addListener(this::newRegistry);
        YDM.proxy.registerModEventListeners(bus);
        
        YdmBlocks.register(bus);
        YdmItems.register(bus);
        YdmContainerTypes.register(bus);
        YdmEntityTypes.register(bus);
        YdmTileEntityTypes.register(bus);
        ActionIcons.register(bus);
        ZoneTypes.register(bus);
        ActionTypes.register(bus);
        DuelMessageHeaders.register(bus);
        
        bus = MinecraftForge.EVENT_BUS;
        // see: https://github.com/MinecraftForge/MinecraftForge/pull/6954
        // need to write directly to nbt for now
        bus.addGenericListener(ItemStack.class, this::attachItemStackCapabilities);
        bus.addGenericListener(Entity.class, this::attachPlayerCapabilities);
        bus.addListener(this::playerClone);
        bus.addListener(this::playerTick);
        bus.addListener(this::registerCommands);
        bus.addListener(this::findDecks);
        bus.addListener(this::serverStopped);
        YDM.proxy.registerForgeEventListeners(bus);
        
        YDM.proxy.preInit();
        initFolders();
    }
    
    private void init(FMLCommonSetupEvent event)
    {
        YDM.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(YDM.MOD_ID, "main"),
                () -> YDM.PROTOCOL_VERSION,
                YDM.PROTOCOL_VERSION::equals,
                YDM.PROTOCOL_VERSION::equals);
        
        initFiles();
        
        int index = 0;
        YDM.channel.registerMessage(index++, CardBinderMessages.ChangePage.class, CardBinderMessages.ChangePage::encode, CardBinderMessages.ChangePage::decode, CardBinderMessages.ChangePage::handle);
        YDM.channel.registerMessage(index++, CardBinderMessages.ChangeSearch.class, CardBinderMessages.ChangeSearch::encode, CardBinderMessages.ChangeSearch::decode, CardBinderMessages.ChangeSearch::handle);
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
        DuelMessage.register(YDM.channel, index++, DuelMessages.SendAdmitDefeat.class, DuelMessages.SendAdmitDefeat::new);
        DuelMessage.register(YDM.channel, index++, DuelMessages.SendOfferDraw.class, DuelMessages.SendOfferDraw::new);
        YDM.channel.registerMessage(index++, CIIMessages.SetPage.class, CIIMessages.SetPage::encode, CIIMessages.SetPage::decode, CIIMessages.SetPage::handle);
        YDM.channel.registerMessage(index++, CIIMessages.ChangePage.class, CIIMessages.ChangePage::encode, CIIMessages.ChangePage::decode, CIIMessages.ChangePage::handle);
        
        YDM.proxy.init();
        WorkerManager.init();
    }
    
    public void initFolders()
    {
        YDM.mainFolder = new File("ydm_db");
        YDM.cardsFolder = new File(YDM.mainFolder, "cards");
        YDM.setsFolder = new File(YDM.mainFolder, "sets");
        YDM.distributionsFolder = new File(YDM.mainFolder, "distributions");
        
        YDM.bindersFolder = new File("ydm_binders");
        YdmIOUtil.createDirIfNonExistant(YDM.bindersFolder);
        
        YDM.proxy.initFolders();
    }
    
    private void initFiles()
    {
        YDM.proxy.initFiles();
        YdmIOUtil.setAgent();
        YdmDatabase.initDatabase();
    }
    
    private void attachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        if(event.getObject().getItem() == YdmItems.CARD_BINDER.get())
        {
            attachCapability(event, new UUIDHolder(event.getObject()::getOrCreateTag), UUID_HOLDER, "uuid_holder", true);
        }
        if(event.getObject().getItem() instanceof SimpleBinderItem)
        {
            SimpleBinderItem item = (SimpleBinderItem) event.getObject().getItem();
            YDMItemHandler handler = new YDMItemHandler(item.binderSize, event.getObject()::getOrCreateTag);
            attachCapability(event, handler, CARD_ITEM_INVENTORY, "card_item_inventory", true);
        }
        if(event.getObject().getItem() == YdmItems.OPENED_SET.get())
        {
            attachCapability(event, new YDMItemHandler(0, event.getObject()::getOrCreateTag), CARD_ITEM_INVENTORY, "card_item_inventory", true);
        }
        if(event.getObject().getItem() instanceof DeckBoxItem)
        {
            attachCapability(event, new YDMItemHandler(DeckHolder.TOTAL_DECK_SIZE, event.getObject()::getOrCreateTag), CARD_ITEM_INVENTORY, "card_item_inventory", true);
        }
    }
    
    private void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player)
        {
            Player player = (Player) event.getObject();
            attachCapability(event, new CooldownHolder(), COOLDOWN_HOLDER, "cooldown_holder", false);
        }
    }
    
    private static <T extends Tag, C extends INBTSerializable<T>> void attachCapability(AttachCapabilitiesEvent<?> event, C capData, Capability<C> capability, String name, boolean invalidate)
    {
        LazyOptional<C> optional = LazyOptional.of(() -> capData);
        ICapabilitySerializable<T> provider = new ICapabilitySerializable<T>()
        {
            @Override
            public <S> LazyOptional<S> getCapability(Capability<S> cap, Direction side)
            {
                if(cap == capability)
                {
                    return optional.cast();
                }
                
                return LazyOptional.empty();
            }
            
            @Override
            public T serializeNBT()
            {
                return capData.serializeNBT();
            }
            
            @Override
            public void deserializeNBT(T tag)
            {
                capData.deserializeNBT(tag);
            }
        };
        
        event.addCapability(new ResourceLocation(MOD_ID, name), provider);
        
        if(invalidate)
        {
            event.addListener(optional::invalidate);
        }
    }
    
    private void playerClone(PlayerEvent.Clone event)
    {
        final Player original = event.getOriginal();
        final Player current = event.getEntity();
        
        original.revive();
        
        original.getCapability(COOLDOWN_HOLDER).ifPresent(originalCD ->
        {
            current.getCapability(COOLDOWN_HOLDER).ifPresent(currentCD ->
            {
                currentCD.deserializeNBT(original.serializeNBT());
            });
        });
        
        original.discard();
    }
    
    private void playerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            event.player.getCapability(COOLDOWN_HOLDER).ifPresent(CooldownHolder::tick);
        }
    }
    
    private void registerCommands(RegisterCommandsEvent event)
    {
        YdmCommand.registerCommand(event.getDispatcher());
    }
    
    private void modConfig(ModConfigEvent event)
    {
        if(event.getConfig().getSpec() == YDM.commonConfigSpec)
        {
            YDM.log("Baking common config!");
            YDM.dbSourceUrl = YDM.commonConfig.dbSourceUrl.get();
        }
    }
    
    private void findDecks(FindDecksEvent event)
    {
        Player player = event.getEntity();
        
        ItemStack itemStack;
        DeckHolder dh;
        
        for(int i = 0; i < player.getInventory().getContainerSize(); ++i)
        {
            itemStack = player.getInventory().getItem(i);
            
            if(itemStack.getItem() instanceof DeckBoxItem)
            {
                dh = new ItemHandlerDeckHolder(((DeckBoxItem) itemStack.getItem()).getItemHandler(itemStack), ((DeckBoxItem) itemStack.getItem()).getCardSleeves(itemStack));
                
                if(!dh.isEmpty())
                {
                    event.addDeck(dh, itemStack);
                }
            }
        }
        
        itemStack = player.getOffhandItem();
        if(itemStack.getItem() instanceof DeckBoxItem)
        {
            dh = new ItemHandlerDeckHolder(((DeckBoxItem) itemStack.getItem()).getItemHandler(itemStack), ((DeckBoxItem) itemStack.getItem()).getCardSleeves(itemStack));
            
            if(!dh.isEmpty())
            {
                event.addDeck(dh, itemStack);
            }
        }
    }
    
    private void newRegistry(NewRegistryEvent event)
    {
        YDM.actionIconRegistry = event.create(new RegistryBuilder<ActionIcon>().setName(new ResourceLocation(YDM.MOD_ID, "action_icons")).setMaxID(511));
        YDM.zoneTypeRegistry = event.create(new RegistryBuilder<ZoneType>().setName(new ResourceLocation(YDM.MOD_ID, "zone_types")).setMaxID(511));
        YDM.actionTypeRegistry = event.create(new RegistryBuilder<ActionType>().setName(new ResourceLocation(YDM.MOD_ID, "action_types")).setMaxID(511));
        YDM.duelMessageHeaderRegistry = event.create(new RegistryBuilder<DuelMessageHeaderType>().setName(new ResourceLocation(YDM.MOD_ID, "duel_message_headers")).setMaxID(63));
    }
    
    private void serverStopped(ServerStoppedEvent event)
    {
        synchronized(JsonCardsManager.LOADED_MANAGERS)
        {
            for(JsonCardsManager m : JsonCardsManager.LOADED_MANAGERS)
            {
                m.safe(() ->
                {
                });
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
