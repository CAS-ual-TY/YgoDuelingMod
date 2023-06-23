package de.cas_ual_ty.ydm.simplebinder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import de.cas_ual_ty.ydm.carditeminventory.HeldCIIContainer;
import de.cas_ual_ty.ydm.util.YDMItemHandler;
import de.cas_ual_ty.ydm.util.YdmUtil;
import net.minecraft.nbt.CompoundTag;


import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

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
    public void appendHoverText(ItemStack itemStack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(itemStack, worldIn, tooltip, flagIn);
        //        tooltip.add(new Component(this.getTranslationKey() + ".desc").modifyStyle((s) -> s.applyFormatting(ChatFormatting.RED)));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        if(!world.isClientSide && hand == YdmUtil.getActiveItem(player, this))
        {
            ItemStack itemStack = player.getItemInHand(hand);
            
            getItemHandler(itemStack).ifPresent(handler ->
            {
                HeldCIIContainer.openGui(player, hand, binderSize, new MenuProvider()
                {
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player)
                    {
                        ItemStack itemStack = player.getItemInHand(hand);
                        return new SimpleBinderContainer(YdmContainerTypes.SIMPLE_BINDER.get(), id, playerInventory, handler, hand);
                    }
                    
                    @Override
                    public Component getDisplayName()
                    {
                        return Component.translatable("container." + YDM.MOD_ID + ".simple_binder");
                    }
                });
            });
            
            return InteractionResultHolder.success(itemStack);
        }
        
        return super.use(world, player, hand);
    }
    
    public LazyOptional<YDMItemHandler> getItemHandler(ItemStack itemStack)
    {
        return itemStack.getCapability(YDM.CARD_ITEM_INVENTORY);
    }
    
    public CompoundTag getNBT(ItemStack itemStack)
    {
        return itemStack.getOrCreateTag();
    }
    
    public static Item makeItem(String modId, CreativeModeTab itemGroup, int pagesAmt)
    {
        return new SimpleBinderItem(new Properties().tab(itemGroup).stacksTo(1), 6 * 9 * pagesAmt);
    }
    
    @Override
    public boolean shouldOverrideMultiplayerNbt()
    {
        return true;
    }
    
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null && nbt.contains("simple_binder_inventory", Tag.TAG_COMPOUND))
        {
            stack.getCapability(YDM.CARD_ITEM_INVENTORY).ifPresent(handler ->
            {
                handler.deserializeNBT(nbt.getCompound("simple_binder_inventory"));
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
            finalNBT.put("simple_binder_inventory", handler.serializeNBT());
        });
        
        return finalNBT;
    }
}
