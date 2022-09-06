package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

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
    public void appendHoverText(ItemStack itemStack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(itemStack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent(getDescriptionId() + ".desc").withStyle((s) -> s.applyFormat(TextFormatting.RED)));
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        if(!world.isClientSide && hand == YdmUtil.getActiveItem(player, this))
        {
            ItemStack itemStack = player.getItemInHand(hand);
            
            if(hasOldItemHandler(itemStack))
            {
                ItemStackHandler itemHandler = getOldItemHandler(itemStack);
                getItemHandler(itemStack).ifPresent(current -> {
                    current.deserializeNBT(itemHandler.serializeNBT());
                    current.save();
                });
                removeOldItemHandler(itemStack);
            }
            
            getItemHandler(itemStack).ifPresent(itemHandler ->
            {
                itemHandler.load();
                HeldCIIContainer.openGui(player, hand, itemHandler.getSlots(), new INamedContainerProvider()
                {
                    @Override
                    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
                    {
                        return new CardSetContainer(YdmContainerTypes.CARD_SET, id, playerInventory, itemHandler, hand);
                    }
                    
                    @Override
                    public ITextComponent getDisplayName()
                    {
                        return new TranslationTextComponent("container." + YDM.MOD_ID + ".card_set");
                    }
                });
            });
            
            return ActionResult.success(itemStack);
        }
        
        return super.use(world, player, hand);
    }
    
    public int getSize(ItemStack itemStack)
    {
        return getNBT(itemStack).getInt("size");
    }
    
    public boolean hasOldItemHandler(ItemStack itemStack)
    {
        return getNBT(itemStack).contains("itemHandler");
    }
    
    @Nullable
    public ItemStackHandler getOldItemHandler(ItemStack itemStack)
    {
        CompoundNBT nbt = getNBT(itemStack);
        
        if(!nbt.contains("itemHandler") || !nbt.contains("size"))
        {
            return null;
        }
        else
        {
            int size = nbt.getInt("size");
            ItemStackHandler itemHandler = new ItemStackHandler(size);
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemHandler, null, nbt.get("itemHandler"));
            return itemHandler;
        }
    }
    
    public void removeOldItemHandler(ItemStack itemStack)
    {
        CompoundNBT nbt = getNBT(itemStack);
        nbt.remove("itemHandler");
        nbt.remove("size");
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
    {
        return;
    }
    
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null && nbt.contains("card_item_inventory", Constants.NBT.TAG_COMPOUND))
        {
            stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
            {
                handler.deserializeNBT(nbt.getCompound("card_item_inventory"));
            });
        }
    }
    
    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack)
    {
        CompoundNBT nbt = super.getShareTag(stack);
        
        if(nbt == null)
        {
            nbt = new CompoundNBT();
        }
        
        CompoundNBT finalNBT = nbt;
        
        stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
        {
            finalNBT.put("card_item_inventory", handler.serializeNBT());
        });
        
        return finalNBT;
    }
}
