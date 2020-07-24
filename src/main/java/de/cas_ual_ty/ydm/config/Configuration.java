package de.cas_ual_ty.ydm.config;

import org.apache.commons.lang3.tuple.Pair;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraftforge.common.ForgeConfigSpec;

public class Configuration
{
    public static final ForgeConfigSpec CLIENT_SPEC;
    //    public static final ForgeConfigSpec COMMON_SPEC;
    
    public static final ClientConfig CLIENT;
    //    public static final CommonConfig COMMON;
    
    static
    {
        Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = client.getLeft();
        CLIENT_SPEC = client.getRight();
        
        //        Pair<VCommonConfig, ForgeConfigSpec> common = new Builder().configure(VCommonConfig::new);
        //        COMMON = common.getLeft();
        //        COMMON_SPEC = common.getRight();
    }
    
    public static void bakeClient()
    {
        YDM.activeInfoImageSize = YdmUtil.toPow2(Configuration.CLIENT.activeInfoImageSize.get());
        YDM.activeItemImageSize = YdmUtil.toPow2(Configuration.CLIENT.activeItemImageSize.get());
        YDM.keepCachedImages = Configuration.CLIENT.keepCachedImages.get();
        YDM.itemsUseCardImages = Configuration.CLIENT.itemsUseCardImages.get();
        YDM.dbSourceUrl = Configuration.CLIENT.dbSourceUrl.get();
    }
}
