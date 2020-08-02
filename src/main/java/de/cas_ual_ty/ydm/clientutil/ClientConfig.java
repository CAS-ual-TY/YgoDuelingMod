package de.cas_ual_ty.ydm.clientutil;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ClientConfig
{
    public final IntValue activeInfoImageSize;
    public final IntValue activeItemImageSize;
    public final IntValue activeMainImageSize;
    public final BooleanValue keepCachedImages;
    public final BooleanValue itemsUseCardImages;
    public final ConfigValue<String> dbSourceUrl;
    public final BooleanValue showBinderId;
    
    public ClientConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("client");
        
        this.activeInfoImageSize = builder
            .comment("The size of card images in shown card infos.")
            .defineInRange("infoImageSize", 256, 16, 1024);
        this.activeItemImageSize = builder
            .comment("The size of card images used for items (only if itemsUseCardImages is set to true).")
            .defineInRange("itemImageSize", 64, 16, 256);
        this.activeMainImageSize = builder
            .comment("The size of card images in duels and in card inventories.")
            .defineInRange("mainImageSize", 64, 16, 256);
        this.keepCachedImages = builder
            .comment("Keep the raw images cached when downloading and converting them to the appropriate size.")
            .define("keepCachedImages", true);
        this.itemsUseCardImages = builder
            .comment("Make card items use their images instead of only the back side. Requires a lot more resources.")
            .define("itemsUseCardImages", false);
        this.dbSourceUrl = builder
            .comment("Download link for the cards and sets database. Must be a .zip file.")
            .define("dbSourceUrl", "https://github.com/CAS-ual-TY/YDM2-DB/archive/master.zip");
        this.showBinderId = builder
            .comment("Show card binder UUIDs when hovering over them.")
            .define("showBinderId", false);
        
        builder.pop();
    }
}
