package de.cas_ual_ty.ydm.clientutil;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;;

public class ClientConfig
{
    public final IntValue activeInfoImageSize;
    public final IntValue activeItemImageSize;
    public final IntValue activeMainImageSize;
    public final BooleanValue keepCachedImages;
    public final BooleanValue itemsUseCardImages;
    public final BooleanValue showBinderId;
    public final IntValue maxInfoImages;
    public final IntValue maxMainImages;
    public final DoubleValue duelChatSize;
    
    public ClientConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("client");
        
        this.activeInfoImageSize = builder
            .comment("The size of card images in shown card infos (\"info\" images).")
            .defineInRange("infoImageSize", 256, 16, 1024);
        this.activeItemImageSize = builder
            .comment("The size of card images used for items (only if itemsUseCardImages is set to true) (\"item\" images).")
            .defineInRange("itemImageSize", 16, 16, 256);
        this.activeMainImageSize = builder
            .comment("The size of card images in duels and in card inventories (\"main\" images).")
            .defineInRange("mainImageSize", 64, 16, 256);
        this.keepCachedImages = builder
            .comment("Keep the raw images cached when downloading and converting them to the appropriate size.")
            .define("keepCachedImages", true);
        this.itemsUseCardImages = builder
            .comment("Make card items use their images instead of only the back side. Requires a lot more resources.")
            .define("itemsUseCardImages", false);
        this.showBinderId = builder
            .comment("Show card binder UUIDs when hovering over them.")
            .define("showBinderId", false);
        this.maxInfoImages = builder
            .comment("The amount of \"info\" images that may be loaded at once (oldest ones get unloaded not to overstep this limit).")
            .defineInRange("maxInfoImages", 64, 1, 256);
        this.maxMainImages = builder
            .comment("The amount of \"main\" images that may be loaded at once (oldest ones get unloaded not to overstep this limit).")
            .defineInRange("maxMainImages", 256, 64, 1024);
        this.duelChatSize = builder
            .comment("The chat size multiplier when in the dueling GUI.")
            .defineInRange("duelChatSize", 1D, 0.5D, 1D);
        
        builder.pop();
    }
}
