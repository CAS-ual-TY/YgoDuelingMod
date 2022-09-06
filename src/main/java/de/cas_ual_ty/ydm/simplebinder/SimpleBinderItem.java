package de.cas_ual_ty.ydm.simplebinder;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class SimpleBinderItem extends Item
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
            
            getItemHandler(itemStack).ifPresent(handler ->
            {
                if(hasOldItemHandler(itemStack))
                {
                    ItemStackHandler oldHandler = getOldItemHandler(itemStack);
                    handler.deserializeNBT(oldHandler.serializeNBT());
                    removeOldItemHandler(itemStack);
                }
                
                HeldCIIContainer.openGui(player, hand, binderSize, new INamedContainerProvider()
                {
                    @Override
                    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
                    {
                        ItemStack itemStack = player.getItemInHand(hand);
                        return new SimpleBinderContainer(YdmContainerTypes.SIMPLE_BINDER, id, playerInventory, handler, hand);
                    }
                    
                    @Override
                    public ITextComponent getDisplayName()
                    {
                        return new TranslationTextComponent("container." + YDM.MOD_ID + ".simple_binder");
                    }
                });
            });
            
            return ActionResult.success(itemStack);
        }
        
        return super.use(world, player, hand);
    }
    
    public LazyOptional<YDMItemHandler> getItemHandler(ItemStack itemStack)
    {
        return itemStack.getCapability(YDM.CARD_ITEM_INVENTORY);
    }
    
    public boolean hasOldItemHandler(ItemStack itemStack)
    {
        CompoundNBT nbt = getNBT(itemStack);
        return nbt != null && nbt.contains("itemHandler");
    }
    
    @Nullable
    public ItemStackHandler getOldItemHandler(ItemStack itemStack)
    {
        CompoundNBT nbt = getNBT(itemStack);
        ItemStackHandler itemHandler = new ItemStackHandler(binderSize);
        
        if(nbt.contains("itemHandler"))
        {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(itemHandler, null, nbt.get("itemHandler"));
        }
        
        return itemHandler;
    }
    
    public void removeOldItemHandler(ItemStack itemStack)
    {
        getNBT(itemStack).remove("itemHandler");
    }
    
    public CompoundNBT getNBT(ItemStack itemStack)
    {
        return itemStack.getOrCreateTag();
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
    
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null && nbt.contains("simple_binder_inventory", Constants.NBT.TAG_COMPOUND))
        {
            stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
            {
                handler.deserializeNBT(nbt.getCompound("simple_binder_inventory"));
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
            finalNBT.put("simple_binder_inventory", handler.serializeNBT());
        });
        
        return finalNBT;
    }
}
