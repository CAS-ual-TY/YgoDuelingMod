package de.cas_ual_ty.ydm;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CommonConfig
{
    public final ConfigValue<String> dbSourceUrl;
    
    public CommonConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("client");
        
        this.dbSourceUrl = builder
            .comment("Download link for the cards and sets database. Must be a .zip file.")
            .define("dbSourceUrl", "https://github.com/CAS-ual-TY/YDM2-DB/archive/master.zip");
        
        builder.pop();
    }
}
