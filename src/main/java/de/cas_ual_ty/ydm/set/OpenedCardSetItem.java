package de.cas_ual_ty.ydm.set;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class OpenedCardSetItem extends CardSetBaseItem implements INamedContainerProvider
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
            
            if(hasItemHandler(itemStack))
            {
                HeldCIIContainer.openGui(player, hand, getSize(itemStack), this);
            }
            
            return ActionResult.success(itemStack);
        }
        
        return super.use(world, player, hand);
    }
    
    public int getSize(ItemStack itemStack)
    {
        return getNBT(itemStack).getInt("size");
    }
    
    public boolean hasItemHandler(ItemStack itemStack)
    {
        return getNBT(itemStack).contains("itemHandler");
    }
    
    @Nullable
    public IItemHandler getItemHandler(ItemStack itemStack)
    {
        CompoundNBT nbt = getNBT(itemStack);
        
        if(!nbt.contains("itemHandler") || !nbt.contains("size"))
        {
            return null;
        }
        else
        {
            int size = nbt.getInt("size");
            IItemHandler itemHandler = new ItemStackHandler(size);
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemHandler, null, nbt.get("itemHandler"));
            return itemHandler;
        }
    }
    
    public void setItemHandler(ItemStack itemStack, IItemHandler itemHandler)
    {
        CompoundNBT nbt = getNBT(itemStack);
        nbt.putInt("size", itemHandler.getSlots());
        nbt.put("itemHandler", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemHandler, null));
    }
    
    public ItemStack createItemForSet(CardSet set, IItemHandler itemHandler)
    {
        ItemStack itemStack = new ItemStack(this);
        setCardSet(itemStack, set);
        setItemHandler(itemStack, itemHandler);
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
        return createItemForSet(set, new ItemStackHandler(items));
    }
    
    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
    {
        return;
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
    {
        Hand hand = YdmUtil.getActiveItem(player, this);
        ItemStack itemStack = player.getItemInHand(hand);
        IItemHandler itemHandler = getItemHandler(itemStack);
        return new CardSetContainer(YdmContainerTypes.CARD_SET, id, playerInventory, itemHandler, hand);
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".card_set");
    }
}
