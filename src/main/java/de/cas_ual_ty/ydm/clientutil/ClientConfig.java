package de.cas_ual_ty.ydm.clientutil;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ClientConfig
{
    public final IntValue activeCardInfoImageSize;
    public final IntValue activeCardItemImageSize;
    public final IntValue activeCardMainImageSize;
    public final IntValue activeSetInfoImageSize;
    public final IntValue activeSetItemImageSize;
    public final BooleanValue keepCachedImages;
    public final BooleanValue itemsUseCardImages;
    public final BooleanValue itemsUseSetImages;
    public final BooleanValue showBinderId;
    public final IntValue maxInfoImages;
    public final IntValue maxMainImages;
    public final DoubleValue duelChatSize;
    public final IntValue moveAnimationLength;
    public final IntValue specialAnimationLength;
    public final IntValue attackAnimationLength;
    public final IntValue announcementAnimationLength;
    
    public ClientConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("client");
        
        builder.push("card_images");
        
        activeCardInfoImageSize = builder
                .comment("The size of card images in shown card infos (\"info\" images).")
                .defineInRange("cardInfoImageSize", 256, 16, 1024);
        activeCardItemImageSize = builder
                .comment("The size of card images used for items (only if cardItemsUseImages is set to true) (\"item\" images).")
                .defineInRange("cardItemImageSize", 16, 16, 256);
        activeCardMainImageSize = builder
                .comment("The size of card images in duels and in card inventories (\"main\" images).")
                .defineInRange("cardMainImageSize", 64, 16, 256);
        itemsUseCardImages = builder
                .comment("Make card items use their images instead of only the back side. Requires a lot more resources.")
                .define("cardItemsUseImages", false);
        maxInfoImages = builder
                .comment("The amount of \"info\" images that may be loaded at once (oldest ones get unloaded not to overstep this limit).")
                .defineInRange("maxInfoImages", 64, 1, 256);
        maxMainImages = builder
                .comment("The amount of \"main\" images that may be loaded at once (oldest ones get unloaded not to overstep this limit).")
                .defineInRange("maxMainImages", 256, 64, 1024);
        
        builder.pop();
        
        builder.push("set_images");
        
        activeSetInfoImageSize = builder
                .comment("The size of set images in shown set infos (\"info\" images).")
                .defineInRange("setInfoImageSize", 256, 16, 1024);
        activeSetItemImageSize = builder
                .comment("The size of set images used for items (only if setItemsUseImages is set to true) (\"item\" images).")
                .defineInRange("setItemImageSize", 16, 16, 256);
        itemsUseSetImages = builder
                .comment("Make set items use their images instead of the dummy texture. Requires more resources.")
                .define("setItemsUseImages", false);
        
        builder.pop();
        
        builder.push("duel");
        
        duelChatSize = builder
                .comment("The chat size multiplier when in the dueling GUI.")
                .defineInRange("duelChatSize", 1D, 0.5D, 1D);
        moveAnimationLength = builder
                .comment("The length in ticks (20 ticks = 1 sec) for card move animations.")
                .defineInRange("moveAnimationLength", 10, 8, 40);
        specialAnimationLength = builder
                .comment("The length in ticks (20 ticks = 1 sec) for special card move post-animations (eg. special summon rings).")
                .defineInRange("specialAnimationLength", 10, 8, 40);
        attackAnimationLength = builder
                .comment("The length in ticks (20 ticks = 1 sec) for attack animations.")
                .defineInRange("attackAnimationLength", 12, 8, 40);
        announcementAnimationLength = builder
                .comment("The length in ticks (20 ticks = 1 sec) for announcement animations (the text shown when eg. shuffling your deck).")
                .defineInRange("announcementAnimationLength", 16, 8, 40);
        
        builder.pop();
        
        builder.push("misc");
        
        showBinderId = builder
                .comment("Show card binder UUIDs when hovering over them.")
                .define("showBinderId", true);
        keepCachedImages = builder
                .comment("Keep the raw images cached when downloading and converting them to the appropriate size.")
                .define("keepCachedImages", true);
        
        builder.pop();
        
        builder.pop();
    }
}
