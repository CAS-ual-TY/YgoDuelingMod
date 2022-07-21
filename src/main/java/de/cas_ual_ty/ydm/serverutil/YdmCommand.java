package de.cas_ual_ty.ydm.serverutil;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmDatabase;
import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.card.CardHolder;
import de.cas_ual_ty.ydm.card.Rarity;
import de.cas_ual_ty.ydm.cardbinder.CardBinderCardsManager;
import de.cas_ual_ty.ydm.set.CardSetBaseItem;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.UUID;

public class YdmCommand
{
    public static void registerCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
                Commands.literal(YDM.MOD_ID)
                        .then(Commands.literal("setcontents")
                                .requires((source) -> source.getEntity() instanceof PlayerEntity)
                                .executes((source) -> YdmCommand.setcontents(source)))
                        .then(Commands.literal("binders")
                                .then(Commands.literal("uuid")
                                        .requires((source) -> source.getEntity() instanceof PlayerEntity)
                                        .then(Commands.literal("get")
                                                .executes((context) -> YdmCommand.bindersGet(context)))
                                        .then(Commands.literal("create")
                                                .requires((source) -> source.getServer().isSingleplayer() || source.hasPermission(2))
                                                .then(Commands.argument("uuid", StringArgumentType.word())
                                                        .executes((context) -> YdmCommand.bindersSet(context, StringArgumentType.getString(context, "uuid")))))
                                        .then(Commands.literal("set")
                                                .requires((source) -> source.getServer().isSingleplayer() || source.hasPermission(2))
                                                .then(Commands.argument("uuid", StringArgumentType.word())
                                                        .executes((context) -> YdmCommand.bindersSet(context, StringArgumentType.getString(context, "uuid"))))))
                                .then(Commands.literal("fill")
                                        .requires((source) -> source.getEntity() instanceof PlayerEntity)
                                        .executes((context) -> YdmCommand.bindersFill(context, 3))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes((context) -> YdmCommand.bindersFill(context, IntegerArgumentType.getInteger(context, "count")))))));
    }
    
    public static int bindersGet(CommandContext<CommandSource> context)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            ItemStack itemStack = YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity) context.getSource().getEntity());
            
            if(!itemStack.isEmpty())
            {
                CardBinderCardsManager m = YdmItems.CARD_BINDER.getInventoryManager(itemStack);
                context.getSource().sendSuccess(new StringTextComponent("Binder UUID: " + m.getUUID()), true);
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
            context.getSource().sendSuccess(new StringTextComponent("Created Binder with UUID: " + uuid.toString()), true);
            ((PlayerEntity) context.getSource().getEntity()).addItem(itemStack);
        }
        
        return Command.SINGLE_SUCCESS;
    }
    
    public static int bindersSet(CommandContext<CommandSource> context, String uuidArg)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            UUID uuid = UUID.fromString(uuidArg);
            ItemStack itemStack = YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity) context.getSource().getEntity());
            
            if(!itemStack.isEmpty())
            {
                CardBinderCardsManager m = YdmItems.CARD_BINDER.getInventoryManager(itemStack);
                
                UUID uuidOld = m.getUUID();
                m.setUUID(uuid);
                
                if(uuidOld == null)
                {
                    context.getSource().sendSuccess(new StringTextComponent("Binder did not have an UUID!"), true);
                }
                else
                {
                    context.getSource().sendSuccess(new StringTextComponent("Old Binder UUID: " + uuidOld.toString()), true);
                }
                
                context.getSource().sendSuccess(new StringTextComponent("Set Binder UUID to: " + uuid.toString()), true);
            }
        }
        
        return Command.SINGLE_SUCCESS;
    }
    
    public static int bindersFill(CommandContext<CommandSource> context, int amount)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            ItemStack itemStack = YdmItems.CARD_BINDER.getActiveBinder((PlayerEntity) context.getSource().getEntity());
            
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
                        context.getSource().sendSuccess(new StringTextComponent("Loading Binder..."), true);
                        m.loadRunnable().run();
                    }
                    
                    // --- Filling ---
                    
                    context.getSource().sendSuccess(new StringTextComponent("Filling Binder..."), true);
                    
                    List<CardHolder> list = m.forceGetList();
                    
                    YdmDatabase.forAllCardVariants((card, imageIndex) ->
                    {
                        for(int i = 0; i < amount; ++i)
                        {
                            list.add(new CardHolder(card, imageIndex, Rarity.CREATIVE.name));
                        }
                    });
                    
                    // --- Saving ---
                    
                    context.getSource().sendSuccess(new StringTextComponent("Saving Binder..."), true);
                    m.safeRunnable().run();
                    
                    // --- Done ---
                    
                    m.setIdle();
                    
                    context.getSource().sendSuccess(new StringTextComponent("Done! Binder can now be opened!"), true);
                }
            }
            
            return Command.SINGLE_SUCCESS;
        }
        
        return 0;
    }
    
    public static int setcontents(CommandContext<CommandSource> context)
    {
        if(context.getSource().getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) context.getSource().getEntity();
            Hand hand = CardSetBaseItem.getActiveSetItem(player);
            
            if(hand != null)
            {
                ItemStack itemStack = player.getItemInHand(hand);
                
                ((CardSetBaseItem) itemStack.getItem()).viewSetContents(player.level, player, itemStack);
                
                return Command.SINGLE_SUCCESS;
            }
        }
        
        return 0;
    }
}
