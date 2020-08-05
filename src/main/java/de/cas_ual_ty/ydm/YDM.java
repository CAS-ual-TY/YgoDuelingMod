package de.cas_ual_ty.ydm;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.cas_ual_ty.ydm.cardbinder.CardBinderCardsManager;
import de.cas_ual_ty.ydm.cardbinder.CardBinderMessages;
import de.cas_ual_ty.ydm.deckbox.DeckBoxItem;
import de.cas_ual_ty.ydm.deckbox.IDeckHolder;
import de.cas_ual_ty.ydm.util.ISidedProxy;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@Mod(YDM.MOD_ID)
public class YDM
{
    public static final String MOD_ID = "ydm";
    public static final String PROTOCOL_VERSION = "1";
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static YDM instance;
    public static ISidedProxy proxy;
    public static YdmItemGroup ydmItemGroup;
    public static YdmItemGroup cardsItemGroup;
    public static boolean itemsUseCardImagesActive;
    
    public static File mainFolder;
    public static File cardsFolder;
    public static File setsFolder;
    public static File distributionsFolder;
    public static File imagesParentFolder;
    public static File rawImagesFolder;
    public static File cardInfoImagesFolder;
    public static File cardItemImagesFolder;
    public static File cardMainImagesFolder;
    public static File bindersFolder;
    
    public static int activeInfoImageSize;
    public static int activeItemImageSize;
    public static int activeMainImageSize;
    public static boolean keepCachedImages;
    public static boolean itemsUseCardImages;
    public static String dbSourceUrl;
    public static boolean showBinderId;
    
    public static SimpleChannel channel;
    
    @CapabilityInject(CardBinderCardsManager.class)
    public static Capability<CardBinderCardsManager> BINDER_INVENTORY_CAPABILITY = null;
    
    public YDM()
    {
        YDM.instance = this;
        YDM.proxy = DistExecutor.runForDist(
            () -> de.cas_ual_ty.ydm.clientutil.ClientProxy::new,
            () -> () -> new ISidedProxy()
            {
            });
        YDM.ydmItemGroup = new YdmItemGroup(YDM.MOD_ID, () -> YdmItems.CARD_BACK);
        YDM.cardsItemGroup = new YdmItemGroup(YDM.MOD_ID + ".cards", () -> YdmItems.BLANC_CARD);
        YDM.itemsUseCardImagesActive = false;
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        YDM.proxy.registerModEventListeners(bus);
        
        bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::attachItemStackCapabilities);
        bus.addListener(this::serverStarting);
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
        
        YDM.proxy.init();
    }
    
    private void initFiles()
    {
        YDM.mainFolder = new File("ydm_db");
        
        if(!YDM.mainFolder.exists())
        {
            try
            {
                YdmDatabase.downloadDatabase();
                YDM.mainFolder = new File("ydm_db");
            }
            catch (IOException e)
            {
                YDM.log("Failed downloading cards database.");
                e.printStackTrace();
                return;
            }
        }
        
        YDM.cardsFolder = new File(YDM.mainFolder, "cards");
        YDM.setsFolder = new File(YDM.mainFolder, "sets");
        YDM.distributionsFolder = new File(YDM.mainFolder, "distributions");
        
        YDM.imagesParentFolder = new File("ydm_db_images");
        YDM.rawImagesFolder = new File(YDM.imagesParentFolder, "cards_raw");
        
        // change this depending on resolution (64/128/256) and anime (yes/no) settings
        YDM.cardInfoImagesFolder = new File(YDM.imagesParentFolder, "cards_" + YDM.activeInfoImageSize);
        YDM.cardItemImagesFolder = new File(YDM.imagesParentFolder, "cards_" + YDM.activeItemImageSize);
        YDM.cardMainImagesFolder = new File(YDM.imagesParentFolder, "cards_" + YDM.activeMainImageSize);
        
        YDM.bindersFolder = new File("ydm_binders");
        
        YdmIOUtil.createDirIfNonExistant(YDM.imagesParentFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.rawImagesFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.cardInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.cardItemImagesFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.bindersFolder);
        
        YdmIOUtil.setAgent();
        YdmDatabase.readFiles();
    }
    
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
            final LazyOptional<IItemHandler> instance = LazyOptional.of(() -> new ItemStackHandler(IDeckHolder.TOTAL_DECK_SIZE)
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
    
    private void serverStarting(FMLServerStartingEvent event)
    {
        YdmCommand.registerCommand(event.getCommandDispatcher());
    }
    
    public static void log(String s)
    {
        YDM.LOGGER.info("[" + YDM.MOD_ID + "] " + s);
    }
    
    public static void debug(String s)
    {
        YDM.LOGGER.debug(s);
    }
    
    public static void debug(Object s)
    {
        YDM.debug(s.toString());
    }
}
