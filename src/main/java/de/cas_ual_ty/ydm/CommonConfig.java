package de.cas_ual_ty.ydm;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.List;

public class CommonConfig
{
    public final ConfigValue<String> dbSourceUrl;
    
    public final ConfigValue<Integer> drawCooldown;
    public final ConfigValue<Integer> loserCooldown;
    public final ConfigValue<Integer> winnerCooldown;
    public final ConfigValue<Boolean> cooldownOnlyWhileOnServer;
    
    public final ConfigValue<List<? extends String>> defeatBothOnCDCommands;
    public final ConfigValue<List<? extends String>> defeatWinnerOffCDCommands;
    public final ConfigValue<List<? extends String>> defeatLoserOffCDCommands;
    public final ConfigValue<List<? extends String>> defeatBothOffCDCommands;
    
    public final ConfigValue<List<? extends String>> drawBothOnCDCommands;
    public final ConfigValue<List<? extends String>> drawPlayer1OffCDCommands;
    public final ConfigValue<List<? extends String>> drawPlayer2OffCDCommands;
    public final ConfigValue<List<? extends String>> drawBothOffCDCommands;
    
    public CommonConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("common");
        
        dbSourceUrl = builder
                .comment("Link to the db.json of the used cards and sets database.")
                .define("dbSourceUrl", "https://raw.githubusercontent.com/CAS-ual-TY/YDM2-DB/master/db.json");
        
        builder.push("commandCooldowns").comment("Cooldowns (in ticks; 1 second = 20 ticks) given to the players after commands are executed involving them.");
        
        drawCooldown = builder
                .comment("Cooldown (in ticks; 1 second = 20 ticks) given to a player that drew a duel")
                .defineInRange("drawCooldown", 20 * 30, 0, 7 * 24 * 60 * 60 * 20);
        
        loserCooldown = builder
                .comment("Cooldown (in ticks; 1 second = 20 ticks) given to a player that lost a duel")
                .defineInRange("loserCooldown", 20 * 30, 0, 7 * 24 * 60 * 60 * 20);
        
        winnerCooldown = builder
                .comment("Cooldown (in ticks; 1 second = 20 ticks) given to a player that won a duel")
                .defineInRange("winnerCooldown", 20 * 30, 0, 7 * 24 * 60 * 60 * 20);
        
        cooldownOnlyWhileOnServer = builder
                .comment("If set to true cooldown will only tick down while a player is on the server, if set to false then cooldown will tick down even if a player is not on the server.")
                .define("mustBeOnServer", false);
        
        builder.pop();
        
        builder.push("defeatCommands").comment("Commands executed when a player admits defeat. Use %winner% and %loser% as placeholders for player names (even if they are on cooldown). Only one of these commands lists will be executed.");
        
        defeatBothOnCDCommands = builder
                .comment("Both %winner% and %loser% are on cooldown.")
                .defineList("bothOnCD", ImmutableList.of("say %winner% won a duel against %loser%!"), s -> true);
        
        defeatWinnerOffCDCommands = builder
                .comment("%winner% is not on cooldown, and %loser% is on cooldown.")
                .defineList("winnerOffCD", ImmutableList.of("say %winner% won a duel against %loser%!"), s -> true);
        
        defeatLoserOffCDCommands = builder
                .comment("%winner% is on cooldown, and %loser% is not on cooldown.")
                .defineList("loserOffCD", ImmutableList.of("say %winner% won a duel against %loser%!"), s -> true);
        
        defeatBothOffCDCommands = builder
                .comment("Both %winner% and %loser% are not on cooldown.")
                .defineList("bothOffCD", ImmutableList.of("say %winner% won a duel against %loser%!"), s -> true);
        
        builder.pop();
        
        builder.push("drawCommands").comment("Commands executed when both players agree to a draw. Use %player1% and %player2% as placeholders for player names (even if they are on cooldown). Only one of these commands lists will be executed.");
        
        drawBothOnCDCommands = builder
                .comment("Both %player1% and %player2% are on cooldown.")
                .defineList("bothOnCD", ImmutableList.of("say %player1% and %player2% drew a duel!"), s -> true);
        
        drawPlayer1OffCDCommands = builder
                .comment("%player1% is not on cooldown, and %player2% is on cooldown.")
                .defineList("player1OffCD", ImmutableList.of("say %player1% and %player2% drew a duel!"), s -> true);
        
        drawPlayer2OffCDCommands = builder
                .comment("%player1% is on cooldown, and %player2% is not on cooldown.")
                .defineList("player2OffCD", ImmutableList.of("say %player1% and %player2% drew a duel!"), s -> true);
        
        drawBothOffCDCommands = builder
                .comment("Both %player1% and %player2% are not on cooldown.")
                .defineList("bothOffCD", ImmutableList.of("say %player1% and %player2% drew a duel!"), s -> true);
        
        builder.pop();
        
        builder.pop();
    }
}
