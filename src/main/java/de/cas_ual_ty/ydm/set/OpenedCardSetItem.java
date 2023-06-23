package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.ChatFormatting;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class OpenedCardSetItem extends CardSetBaseItem
{
    public OpenedCardSetItem(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(itemStack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable(getDescriptionId() + ".desc").withStyle((s) -> s.applyFormat(ChatFormatting.RED)));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        if(!world.isClientSide && hand == YdmUtil.getActiveItem(player, this))
        {
            ItemStack itemStack = player.getItemInHand(hand);
            
            getItemHandler(itemStack).ifPresent(itemHandler ->
            {
                itemHandler.load();
                HeldCIIContainer.openGui(player, hand, itemHandler.getSlots(), new MenuProvider()
                {
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player)
                    {
                        return new CardSetContainer(YdmContainerTypes.CARD_SET.get(), id, playerInventory, itemHandler, hand);
                    }
                    
                    @Override
                    public Component getDisplayName()
                    {
                        return Component.translatable("container." + YDM.MOD_ID + ".card_set");
                    }
                });
            });
            
            return InteractionResultHolder.success(itemStack);
        }
        
        return super.use(world, player, hand);
    }
    
    public int getSize(ItemStack itemStack)
    {
        return getNBT(itemStack).getInt("size");
    }
    
    public LazyOptional<YDMItemHandler> getItemHandler(ItemStack itemStack)
    {
        return itemStack.getCapability(YDM.CARD_ITEM_INVENTORY);
    }
    
    public ItemStack createItemForSet(CardSet set, YDMItemHandler itemHandler)
    {
        ItemStack itemStack = new ItemStack(this);
        setCardSet(itemStack, set);
        getItemHandler(itemStack).ifPresent(current -> current.deserializeNBT(itemHandler.serializeNBT()));
        return itemStack;
    }
    
    public ItemStack createItemForSet(CardSet set)
    {
        List<ItemStack> cards = set.open(new Random());
        NonNullList<ItemStack> items;
        
        if(cards == null)
        {
            items = NonNullList.withSize(0, ItemStack.EMPTY);
        }
        else
        {
            items = NonNullList.of(ItemStack.EMPTY, cards.toArray(new ItemStack[0]));
        }
        
        return createItemForSet(set, items);
    }
    
    public ItemStack createItemForSet(CardSet set, NonNullList<ItemStack> items)
    {
        ItemStack itemStack = new ItemStack(this);
        YDMItemHandler itemHandler = new YDMItemHandler(items, itemStack::getOrCreateTag);
        setCardSet(itemStack, set);
        getItemHandler(itemStack).ifPresent(current -> current.deserializeNBT(itemHandler.serializeNBT()));
        return itemStack;
    }
    
    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
    {
        return;
    }
    
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null && nbt.contains("card_item_inventory", Tag.TAG_COMPOUND))
        {
            stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
            {
                handler.deserializeNBT(nbt.getCompound("card_item_inventory"));
            });
        }
    }
    
    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack)
    {
        CompoundTag nbt = super.getShareTag(stack);
        
        if(nbt == null)
        {
            nbt = new CompoundTag();
        }
        
        CompoundTag finalNBT = nbt;
        
        stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
        {
            finalNBT.put("card_item_inventory", handler.serializeNBT());
        });
        
        return finalNBT;
    }
}
