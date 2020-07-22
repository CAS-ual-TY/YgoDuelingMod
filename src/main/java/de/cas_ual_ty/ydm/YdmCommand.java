package de.cas_ual_ty.ydm;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;

public class YdmCommand
{
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        Commands.literal(YDM.MOD_ID).then(Commands.literal("cards").executes(YdmCommand::cards));
    }
    
    public static int cards(CommandContext<CommandSource> context)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            return Command.SINGLE_SUCCESS;
        }
        
        return 0;
    }
}
