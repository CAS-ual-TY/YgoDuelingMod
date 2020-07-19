package de.cas_ual_ty.ydm;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.cas_ual_ty.ydm.capability.CardHolder;
import de.cas_ual_ty.ydm.capability.CardHolderProvider;
import de.cas_ual_ty.ydm.capability.CardHolderStorage;
import de.cas_ual_ty.ydm.capability.ICardHolder;
import de.cas_ual_ty.ydm.card.CardItem;
import de.cas_ual_ty.ydm.proxy.ISidedProxy;
import de.cas_ual_ty.ydm.util.DatabaseReader;
import de.cas_ual_ty.ydm.util.YdmIOUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(YDM.MOD_ID)
public class YDM
{
    public static final String MOD_ID = "ydm";
    public static final String PROTOCOL_VERSION = "1";
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static YDM instance;
    public static ISidedProxy proxy;
    public static YdmItemGroup ydmItemGroup;
    
    public static File mainFolder;
    public static File cardsFolder;
    public static File setsFolder;
    public static File distributionsFolder;
    public static File imagesParentFolder;
    public static File rawImagesFolder;
    public static File cardImagesFolder;
    
    public static int activeImageSize;
    public static boolean keepCachedImages = true;
    
    public static SimpleChannel channel;
    
    public YDM()
    {
        YDM.instance = this;
        YDM.proxy = DistExecutor.runForDist(
            () -> de.cas_ual_ty.ydm.proxy.ClientProxy::new,
            () -> de.cas_ual_ty.ydm.proxy.ServerProxy::new);
        YDM.ydmItemGroup = new YdmItemGroup("itemGroup." + YDM.MOD_ID);
        
        this.initFiles();
        DatabaseReader.readFiles();
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        
        bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::attachCapabilitiesItemStack);
        
        YDM.proxy.preInit();
    }
    
    private void initFiles()
    {
        YDM.mainFolder = new File("ydm_db");
        YDM.cardsFolder = new File(YDM.mainFolder, "cards");
        YDM.setsFolder = new File(YDM.mainFolder, "sets");
        YDM.distributionsFolder = new File(YDM.mainFolder, "distributions");
        YDM.imagesParentFolder = new File(YDM.mainFolder, "images");
        YDM.rawImagesFolder = new File(YDM.imagesParentFolder, "cards_raw");
        YDM.activeImageSize = 128;
        
        // change this depending on resolution (64/128/256) and anime (yes/no) settings
        YDM.cardImagesFolder = new File(YDM.imagesParentFolder, "cards_" + YDM.activeImageSize);
        
        YdmIOUtil.createDirIfNonExistant(YDM.imagesParentFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.rawImagesFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.cardImagesFolder);
    }
    
    private void init(FMLCommonSetupEvent event)
    {
        YDM.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(YDM.MOD_ID, "main"),
            () -> YDM.PROTOCOL_VERSION,
            YDM.PROTOCOL_VERSION::equals,
            YDM.PROTOCOL_VERSION::equals);
        
        CapabilityManager.INSTANCE.register(ICardHolder.class, new CardHolderStorage(), CardHolder::new);
    }
    
    private void attachCapabilitiesItemStack(AttachCapabilitiesEvent<ItemStack> event)
    {
        if(event.getObject() instanceof ItemStack && event.getObject().getItem() instanceof CardItem)
        {
            CardHolderProvider provider = new CardHolderProvider();
            event.addCapability(new ResourceLocation(YDM.MOD_ID, "card_holder"), provider);
            event.addListener(provider.getListener());
        }
    }
    
    public static void log(String s)
    {
        YDM.LOGGER.info(s);
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
