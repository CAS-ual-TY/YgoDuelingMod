package de.cas_ual_ty.ydm.simplebinder;

import java.util.List;

import javax.annotation.Nullable;

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

public class SimpleBinderItem extends Item implements INamedContainerProvider
{
    public final int binderSize;
    
    public SimpleBinderItem(Properties properties, int binderSize)
    {
        super(properties);
        this.binderSize = binderSize;
    }
    
    @Override
    public void addInformation(ItemStack itemStack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(itemStack, worldIn, tooltip, flagIn);
        //        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".desc").modifyStyle((s) -> s.applyFormatting(TextFormatting.RED)));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        if(!world.isRemote && hand == YdmUtil.getActiveItem(player, this))
        {
            ItemStack itemStack = player.getHeldItem(hand);
            HeldCIIContainer.openGui(player, hand, this.binderSize, this);
            return ActionResult.resultSuccess(itemStack);
        }
        
        return super.onItemRightClick(world, player, hand);
    }
    
    public @Nullable IItemHandler getItemHandler(ItemStack itemStack)
    {
        CompoundNBT nbt = this.getNBT(itemStack);
        IItemHandler itemHandler = new ItemStackHandler(this.binderSize);
        
        if(nbt.contains("itemHandler"))
        {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemHandler, null, nbt.get("itemHandler"));
        }
        
        return itemHandler;
    }
    
    public void setItemHandler(ItemStack itemStack, IItemHandler itemHandler)
    {
        this.getNBT(itemStack).put("itemHandler", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(itemHandler, null));
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
    {
        Hand hand = YdmUtil.getActiveItem(player, this);
        ItemStack itemStack = player.getHeldItem(hand);
        IItemHandler itemHandler = this.getItemHandler(itemStack);
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
            ((SimpleBinderItem)itemStack.getItem()).setItemHandler(itemStack, itemHandler);
        }
    }
    
    public static Item makeItem(String modId, ItemGroup itemGroup, int pagesAmt)
    {
        return new SimpleBinderItem(new Properties().group(itemGroup).maxStackSize(1), 6 * 9 * pagesAmt).setRegistryName(modId, "simple_binder_" + pagesAmt);
    }
    
    @Override
    public boolean shouldSyncTag()
    {
        return true;
    }
}
