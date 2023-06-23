package de.cas_ual_ty.ydm.util;

import com.google.gson.JsonObject;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.card.properties.*;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class YdmUtil
{
    private static final int[] POW_2 = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
    
    public static Properties buildProperties(JsonObject j)
    {
        Properties p0 = new Properties(j);
        
        if(p0.getIsSpell())
        {
            return new SpellProperties(p0, j);
        }
        else if(p0.getIsTrap())
        {
            return new TrapProperties(p0, j);
        }
        else if(p0.getIsMonster())
        {
            MonsterProperties p1 = new MonsterProperties(p0, j);
            
            if(p1.getHasDef())
            {
                p1 = new DefMonsterProperties(p1, j);
                
                if(p1.getHasLevel())
                {
                    return new LevelMonsterProperties(p1, j);
                }
                else if(p1.getIsXyz())
                {
                    return new XyzMonsterProperties(p1, j);
                }
            }
            else if(p1.getIsLink())
            {
                return new LinkMonsterProperties(p1, j);
            }
        }
        
        return p0;
    }
    
    public static String toSimpleString(String s)
    {
        return s.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }
    
    public static int getPow2(int pow)
    {
        assert pow >= 0 && pow < YdmUtil.POW_2.length;
        return YdmUtil.POW_2[pow];
    }
    
    public static UUID createRandomUUID()
    {
        return Mth.createInsecureUUID();
    }
    
    public static NonNullSupplier<IllegalArgumentException> throwNullCapabilityException()
    {
        return () -> new IllegalArgumentException("[" + YDM.MOD_ID + "] Capability can not be null!");
    }
    
    public static int toPow2ConfigValue(int i, int min)
    {
        return YdmUtil.getPow2(YdmUtil.range(Mth.log2(i), min, YdmUtil.POW_2.length - 1));
    }
    
    public static int range(int i, int min, int max)
    {
        return Math.max(min, Math.min(max, i));
    }
    
    @Nullable
    public static InteractionHand getActiveItem(Player player, Item item)
    {
        return YdmUtil.getActiveItem(player, (itemStack) -> itemStack.getItem() == item);
    }
    
    @Nullable
    public static InteractionHand getActiveItem(Player player, Predicate<ItemStack> item)
    {
        if(item.test(player.getMainHandItem()))
        {
            return InteractionHand.MAIN_HAND;
        }
        else if(item.test(player.getOffhandItem()))
        {
            return InteractionHand.OFF_HAND;
        }
        else
        {
            return null;
        }
    }
    
    public static ZoneOwner getViewOwner(ZoneOwner owner, ZoneOwner view, ZoneOwner toMap)
    {
        if(!toMap.isPlayer())
        {
            return ZoneOwner.NONE;
        }
        else if(owner.isPlayer())
        {
            return owner;
        }
        else
        {
            if(view == toMap)
            {
                return view;
            }
            else
            {
                return view.opponent();
            }
        }
    }
    
    public static void executeAdmitDefeatCommands(Player winner, Player loser)
    {
        if(winner.level instanceof ServerLevel)
        {
            ServerLevel world = (ServerLevel) winner.level;
            MinecraftServer server = world.getServer();
            
            winner.getCapability(YDM.COOLDOWN_HOLDER).ifPresent(cdWinner ->
            {
                loser.getCapability(YDM.COOLDOWN_HOLDER).ifPresent(cdLoser ->
                {
                    List<? extends String> commands;
                    
                    if(cdWinner.isOffCooldown())
                    {
                        if(cdLoser.isOffCooldown())
                        {
                            // both off CD
                            commands = YDM.commonConfig.defeatBothOffCDCommands.get();
                        }
                        else
                        {
                            // winner off CD
                            commands = YDM.commonConfig.defeatWinnerOffCDCommands.getPath();
                        }
                    }
                    else
                    {
                        if(cdLoser.isOffCooldown())
                        {
                            // loser off CD
                            commands = YDM.commonConfig.defeatLoserOffCDCommands.get();
                        }
                        else
                        {
                            // both on CD
                            commands = YDM.commonConfig.defeatBothOnCDCommands.getPath();
                        }
                    }
                    
                    if(cdWinner.isOffCooldown())
                    {
                        cdWinner.setCooldown(YDM.commonConfig.winnerCooldown.get());
                    }
                    
                    if(cdLoser.isOffCooldown())
                    {
                        cdLoser.setCooldown(YDM.commonConfig.loserCooldown.get());
                    }
                    
                    for(String command : commands)
                    {
                        command = command.replace("%winner%", winner.getScoreboardName()).replace("%loser%", loser.getScoreboardName());
                        
                        try
                        {
                            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
                        }
                        catch(Exception e)
                        {
                            YDM.log("Could not execute command triggered by duel defeat: " + command);
                            e.printStackTrace();
                        }
                    }
                });
            });
        }
    }
    
    public static void executeDrawCommands(Player player1, Player player2)
    {
        if(player1.level instanceof ServerLevel)
        {
            ServerLevel world = (ServerLevel) player1.level;
            MinecraftServer server = world.getServer();
            
            player1.getCapability(YDM.COOLDOWN_HOLDER).ifPresent(cd1 ->
            {
                player2.getCapability(YDM.COOLDOWN_HOLDER).ifPresent(cd2 ->
                {
                    List<? extends String> commands;
                    
                    if(cd1.isOffCooldown())
                    {
                        if(cd2.isOffCooldown())
                        {
                            // both off CD
                            commands = YDM.commonConfig.drawBothOffCDCommands.get();
                        }
                        else
                        {
                            // p1 off CD
                            commands = YDM.commonConfig.drawPlayer1OffCDCommands.getPath();
                        }
                    }
                    else
                    {
                        if(cd2.isOffCooldown())
                        {
                            // p2 off CD
                            commands = YDM.commonConfig.drawPlayer2OffCDCommands.get();
                        }
                        else
                        {
                            // both on CD
                            commands = YDM.commonConfig.drawBothOnCDCommands.getPath();
                        }
                    }
                    
                    if(cd1.isOffCooldown())
                    {
                        cd1.setCooldown(YDM.commonConfig.drawCooldown.get());
                    }
                    
                    if(cd2.isOffCooldown())
                    {
                        cd2.setCooldown(YDM.commonConfig.drawCooldown.get());
                    }
                    
                    for(String command : commands)
                    {
                        command = command.replace("%player1%", player1.getScoreboardName()).replace("%player2%", player2.getScoreboardName());
                        
                        try
                        {
                            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
                        }
                        catch(Exception e)
                        {
                            YDM.log("Could not execute command triggered by duel draw: " + command);
                            e.printStackTrace();
                        }
                    }
                });
            });
        }
    }
}
