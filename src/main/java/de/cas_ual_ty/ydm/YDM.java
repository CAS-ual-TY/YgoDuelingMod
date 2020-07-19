package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.capability.CardHolder;
import de.cas_ual_ty.ydm.capability.CardHolderProvider;
import de.cas_ual_ty.ydm.capability.CardHolderStorage;
import de.cas_ual_ty.ydm.capability.ICardHolder;
import de.cas_ual_ty.ydm.card.CardItem;
import de.cas_ual_ty.ydm.proxy.ISidedProxy;
import de.cas_ual_ty.ydm.util.CardsReader;
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
    
    public static YDM instance;
    public static ISidedProxy proxy;
    public static SimpleChannel channel;
    
    public YDM()
    {
        CardsReader.readFiles();
        
        YDM.instance = this;
        YDM.proxy = DistExecutor.runForDist(
            () -> de.cas_ual_ty.ydm.proxy.ClientProxy::new,
            () -> de.cas_ual_ty.ydm.proxy.ServerProxy::new);
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        
        bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::attachCapabilitiesItemStack);
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
}
