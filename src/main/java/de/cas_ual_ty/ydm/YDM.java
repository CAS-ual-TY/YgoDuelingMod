package de.cas_ual_ty.ydm;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.cas_ual_ty.ydm.util.YdmIOUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
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
    public static File cardInfoImagesFolder;
    public static File cardItemImagesFolder;
    
    public static int activeInfoImageSize;
    public static int activeItemImageSize;
    public static boolean keepCachedImages = true;
    public static boolean itemsUseCardImages = true;
    
    public static SimpleChannel channel;
    
    public YDM()
    {
        YDM.instance = this;
        YDM.proxy = DistExecutor.runForDist(
            () -> de.cas_ual_ty.ydm.client.ClientProxy::new,
            () -> () -> new ISidedProxy() {});
        YDM.ydmItemGroup = new YdmItemGroup("itemGroup." + YDM.MOD_ID);
        
        this.initFiles();
        
        YdmIOUtil.setAgent();
        Database.readFiles();
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        YDM.proxy.registerModEventListeners(bus);
        
        bus = MinecraftForge.EVENT_BUS;
        YDM.proxy.registerForgeEventListeners(bus);
        
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
        YDM.activeInfoImageSize = 256;
        YDM.activeItemImageSize = 16;
        
        // change this depending on resolution (64/128/256) and anime (yes/no) settings
        YDM.cardInfoImagesFolder = new File(YDM.imagesParentFolder, "cards_" + YDM.activeInfoImageSize);
        YDM.cardItemImagesFolder = new File(YDM.imagesParentFolder, "cards_" + YDM.activeItemImageSize);
        
        YdmIOUtil.createDirIfNonExistant(YDM.imagesParentFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.rawImagesFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.cardInfoImagesFolder);
        YdmIOUtil.createDirIfNonExistant(YDM.cardItemImagesFolder);
    }
    
    private void init(FMLCommonSetupEvent event)
    {
        YDM.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(YDM.MOD_ID, "main"),
            () -> YDM.PROTOCOL_VERSION,
            YDM.PROTOCOL_VERSION::equals,
            YDM.PROTOCOL_VERSION::equals);
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
