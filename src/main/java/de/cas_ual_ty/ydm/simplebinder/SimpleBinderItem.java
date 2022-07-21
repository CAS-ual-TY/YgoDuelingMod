package de.cas_ual_ty.ydm.simplebinder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class SimpleBinderItem extends Item implements INamedContainerProvider
{
    public final int binderSize;
    
    public SimpleBinderItem(Properties properties, int binderSize)
    {
        super(properties);
        this.binderSize = binderSize;
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(itemStack, worldIn, tooltip, flagIn);
        //        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".desc").modifyStyle((s) -> s.applyFormatting(TextFormatting.RED)));
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        if(!world.isClientSide && hand == YdmUtil.getActiveItem(player, this))
        {
            ItemStack itemStack = player.getItemInHand(hand);
            HeldCIIContainer.openGui(player, hand, binderSize, this);
            return ActionResult.success(itemStack);
        }
        
        return super.use(world, player, hand);
    }
    
    @Nullable
    public IItemHandler getItemHandler(ItemStack itemStack)
    {
        CompoundNBT nbt = getNBT(itemStack);
        IItemHandler itemHandler = new ItemStackHandler(binderSize);
        
        if(nbt.contains("itemHandler"))
        {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemHandler, null, nbt.get("itemHandler"));
        }
        
        return itemHandler;
    }
    
    public void setItemHandler(ItemStack itemStack, IItemHandler itemHandler)
    {
        getNBT(itemStack).put("itemHandler", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemHandler, null));
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
    {
        Hand hand = YdmUtil.getActiveItem(player, this);
        ItemStack itemStack = player.getItemInHand(hand);
        IItemHandler itemHandler = getItemHandler(itemStack);
        return new SimpleBinderContainer(YdmContainerTypes.SIMPLE_BINDER, id, playerInventory, itemHandler, hand);
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".simple_binder");
    }
    
    public CompoundNBT getNBT(ItemStack itemStack)
    {
        return itemStack.getOrCreateTag();
    }
    
    public static void saveItemHandler(ItemStack itemStack, IItemHandler itemHandler)
    {
        if(itemStack.getItem() instanceof SimpleBinderItem)
        {
            ((SimpleBinderItem) itemStack.getItem()).setItemHandler(itemStack, itemHandler);
        }
    }
    
    public static Item makeItem(String modId, ItemGroup itemGroup, int pagesAmt)
    {
        return new SimpleBinderItem(new Properties().tab(itemGroup).stacksTo(1), 6 * 9 * pagesAmt).setRegistryName(modId, "simple_binder_" + pagesAmt);
    }
    
    @Override
    public boolean shouldOverrideMultiplayerNbt()
    {
        return true;
    }
}
