package de.cas_ual_ty.ydm;

import de.cas_ual_ty.ydm.proxy.ISidedProxy;
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
    public static final String MOD_ID = "ydm2";
    public static final String PROTOCOL_VERSION = "1";
    
    public static YDM instance;
    public static ISidedProxy proxy;
    public static SimpleChannel channel;
    
    public YDM()
    {
        YDM.instance = this;
        YDM.proxy = DistExecutor.runForDist(
            () -> de.cas_ual_ty.ydm.proxy.ClientProxy::new,
            () -> de.cas_ual_ty.ydm.proxy.ServerProxy::new);
        
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        
        bus = MinecraftForge.EVENT_BUS;
        //TODO
    }
    
    private void init(FMLCommonSetupEvent event)
    {
        YDM.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(YDM.MOD_ID, "main"),
            () -> YDM.PROTOCOL_VERSION,
            YDM.PROTOCOL_VERSION::equals,
            YDM.PROTOCOL_VERSION::equals);
    }
}
