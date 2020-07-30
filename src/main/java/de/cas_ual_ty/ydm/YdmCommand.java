package de.cas_ual_ty.ydm;

import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.cas_ual_ty.ydm.binder.BinderCardInventoryManager;
import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.CardHolder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class YdmCommand
{
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            
            Commands.literal(YDM.MOD_ID)
                .then(Commands.literal("cards")
                    .executes(YdmCommand::cards))
                .then(Commands.literal("binders")
                    .requires((source) -> source.getServer().isSinglePlayer() || source.hasPermissionLevel(2))
                    .then(Commands.literal("fill")
                        .executes((source) -> YdmCommand.bindersFill(source, 3))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                            .executes((source) -> YdmCommand.bindersFill(source, source.getArgument("amount", Integer.class))))))
        
        );
    }
    
    public static int cards(CommandContext<CommandSource> context)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            return Command.SINGLE_SUCCESS;
        }
        
        return 0;
    }
    
    public static int bindersFill(CommandContext<CommandSource> context, int amount)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            ItemStack itemStack = YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity)context.getSource().getEntity());
            
            if(!itemStack.isEmpty())
            {
                BinderCardInventoryManager m = YdmItems.CARD_BINDER.getInventoryManager(itemStack);
                
                if(m.isInIdleState())
                {
                    // --- Start ---
                    
                    m.setWorking();
                    
                    // --- Loading ---
                    
                    if(!m.isLoaded())
                    {
                        context.getSource().sendFeedback(new StringTextComponent("Loading Binder..."), true);
                        m.loadRunnable().run();
                    }
                    
                    // --- Filling ---
                    
                    context.getSource().sendFeedback(new StringTextComponent("Filling Binder..."), true);
                    
                    List<CardHolder> list = m.forceGetList();
                    
                    int i;
                    CardHolder h;
                    
                    for(Card card : Database.CARDS_LIST)
                    {
                        for(i = 0; i < amount; ++i)
                        {
                            h = new CardHolder(card);
                            list.add(h);
                        }
                    }
                    
                    // --- Saving ---
                    
                    context.getSource().sendFeedback(new StringTextComponent("Saving Binder..."), true);
                    m.safeRunnable().run();
                    
                    // --- Done ---
                    
                    m.setIdle();
                    
                    context.getSource().sendFeedback(new StringTextComponent("Done! Binder can now be opened!"), true);
                }
            }
            
            return Command.SINGLE_SUCCESS;
        }
        
        return 0;
    }
}
