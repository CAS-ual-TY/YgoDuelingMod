package de.cas_ual_ty.ydm;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import de.cas_ual_ty.ydm.card.Card;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.cardbinder.CardBinderCardsManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class YdmCommand
{
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            
            Commands.literal(YDM.MOD_ID)
                .then(Commands.literal("cards")
                    .requires((source) -> source.getServer().isSinglePlayer() || source.hasPermissionLevel(2))
                    .then(Commands.literal("get")
                        .requires((source) -> source.getEntity() instanceof PlayerEntity)
                        .then(Commands.argument("set-id", StringArgumentType.word())
                            .executes((context) -> YdmCommand.cardsGet(context, context.getArgument("set-id", String.class)))))
                    .then(Commands.literal("give")
                        .then(Commands.argument("targets", EntityArgument.players())
                            .then(Commands.argument("set-id", StringArgumentType.word())
                                .executes((context) -> YdmCommand.cardsGive(context, StringArgumentType.getString(context, "set-id"), EntityArgument.getPlayers(context, "targets"), 1))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                    .executes((context) -> YdmCommand.cardsGive(context, StringArgumentType.getString(context, "set-id"), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count"))))))))
                .then(Commands.literal("binders")
                    .then(Commands.literal("uuid")
                        .requires((source) -> source.getEntity() instanceof PlayerEntity)
                        .then(Commands.literal("get")
                            .executes((context) -> YdmCommand.bindersGet(context)))
                        .then(Commands.literal("create")
                            .then(Commands.argument("uuid", StringArgumentType.word())
                                .executes((context) -> YdmCommand.bindersSet(context, StringArgumentType.getString(context, "uuid")))))
                        .then(Commands.literal("set")
                            .then(Commands.argument("uuid", StringArgumentType.word())
                                .executes((context) -> YdmCommand.bindersSet(context, StringArgumentType.getString(context, "uuid"))))))
                    .requires((source) -> source.getServer().isSinglePlayer() || source.hasPermissionLevel(2))
                    .then(Commands.literal("fill")
                        .requires((source) -> source.getEntity() instanceof PlayerEntity/* && !YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity)source.getEntity()).isEmpty()*/)
                        .executes((context) -> YdmCommand.bindersFill(context, 3))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                            .executes((context) -> YdmCommand.bindersFill(context, IntegerArgumentType.getInteger(context, "count"))))))
        
        );
    }
    
    public static int cardsGet(CommandContext<CommandSource> context, String setId)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            Card card = YdmDatabase.CARDS_LIST.get(setId);
            
            if(card != null)
            {
                PlayerEntity player = (PlayerEntity)context.getSource().getEntity();
                player.addItemStackToInventory(YdmItems.CARD.createItemForCard(card));
            }
            
            return Command.SINGLE_SUCCESS;
        }
        
        return 0;
    }
    
    public static int cardsGive(CommandContext<CommandSource> context, String setId, Collection<ServerPlayerEntity> players, int amount)
    {
        Card card = YdmDatabase.CARDS_LIST.get(setId);
        
        if(card != null)
        {
            for(ServerPlayerEntity player : players)
            {
                player.addItemStackToInventory(YdmItems.CARD.createItemForCard(card));
            }
        }
        
        context.getSource().sendFeedback(new StringTextComponent("Given \"" + card.getProperties().getName() + "\" (" + setId + ") to " + players.size() + " players!"), true);
        
        return players.size();
    }
    
    public static int bindersGet(CommandContext<CommandSource> context)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            ItemStack itemStack = YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity)context.getSource().getEntity());
            
            if(!itemStack.isEmpty())
            {
                CardBinderCardsManager m = YdmItems.CARD_BINDER.getInventoryManager(itemStack);
                context.getSource().sendFeedback(new StringTextComponent("Binder UUID: " + m.getUUID()), true);
            }
        }
        
        return Command.SINGLE_SUCCESS;
    }
    
    public static int bindersCreate(CommandContext<CommandSource> context, String uuidArg)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            UUID uuid = UUID.fromString(uuidArg);
            ItemStack itemStack = new ItemStack(YdmItems.CARD_BINDER);
            CardBinderCardsManager m = YdmItems.CARD_BINDER.getInventoryManager(itemStack);
            m.setUUID(uuid);
            context.getSource().sendFeedback(new StringTextComponent("Created Binder with UUID: " + uuid.toString()), true);
            ((PlayerEntity)context.getSource().getEntity()).addItemStackToInventory(itemStack);
        }
        
        return Command.SINGLE_SUCCESS;
    }
    
    public static int bindersSet(CommandContext<CommandSource> context, String uuidArg)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            UUID uuid = UUID.fromString(uuidArg);
            ItemStack itemStack = YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity)context.getSource().getEntity());
            
            if(!itemStack.isEmpty())
            {
                CardBinderCardsManager m = YdmItems.CARD_BINDER.getInventoryManager(itemStack);
                
                UUID uuidOld = m.getUUID();
                m.setUUID(uuid);
                
                if(uuidOld == null)
                {
                    context.getSource().sendFeedback(new StringTextComponent("Binder did not have an UUID!"), true);
                }
                else
                {
                    context.getSource().sendFeedback(new StringTextComponent("Old Binder UUID: " + uuidOld.toString()), true);
                }
                
                context.getSource().sendFeedback(new StringTextComponent("Set Binder UUID to: " + uuid.toString()), true);
            }
        }
        
        return Command.SINGLE_SUCCESS;
    }
    
    public static int bindersFill(CommandContext<CommandSource> context, int amount)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            ItemStack itemStack = YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity)context.getSource().getEntity());
            
            if(!itemStack.isEmpty())
            {
                CardBinderCardsManager m = YdmItems.CARD_BINDER.getInventoryManager(itemStack);
                
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
                    
                    for(Card card : YdmDatabase.CARDS_LIST)
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
