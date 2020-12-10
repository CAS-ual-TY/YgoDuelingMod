package de.cas_ual_ty.ydm;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CommonConfig
{
    public final ConfigValue<String> dbSourceUrl;
    
    public CommonConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("common");
        
        this.dbSourceUrl = builder
            .comment("Link to the db.json of the used cards and sets database.")
            .define("dbSourceUrl", "https://raw.githubusercontent.com/CAS-ual-TY/YDM2-DB/master/db.json");
        
        builder.pop();
    }
}
