package de.cas_ual_ty.ydm.cardbinder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CardBinderItem extends Item implements INamedContainerProvider
{
    private static final HashMap<UUID, CardBinderCardsManager> MANAGER_MAP = new HashMap<>();
    public static final String MANAGER_UUID_KEY_OLD = "binder_uuid";
    public static final String MANAGER_UUID_KEY = "binder_uuid_cap";
    
    public CardBinderItem(Properties properties)
    {
        super(properties);
    }
    
    public CardBinderCardsManager getInventoryManager(ItemStack itemStack)
    {
        CardBinderCardsManager manager;
        UUID uuid = getUUID(itemStack);
        
        if(uuid == null || !CardBinderItem.MANAGER_MAP.containsKey(uuid))
        {
            manager = new CardBinderCardsManager();
            
            if(uuid == null)
            {
                manager.generateUUIDIfNull();
                uuid = manager.getUUID();
                setUUID(itemStack, uuid);
            }
            else
            {
                manager.setUUID(uuid);
            }
            
            CardBinderItem.MANAGER_MAP.put(uuid, manager);
        }
        else
        {
            manager = CardBinderItem.MANAGER_MAP.get(uuid);
        }
        
        return manager;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        
        tooltip.add(new TranslationTextComponent(getDescriptionId() + ".uuid"));
        
        UUID uuid = getUUID(stack);
        
        if(uuid != null)
        {
            tooltip.add(new StringTextComponent(uuid.toString()));
        }
        else
        {
            tooltip.add(new TranslationTextComponent(getDescriptionId() + ".uuid.empty"));
        }
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        // must also fix UUID on client side if player is in creative mode
        getUUID(player.getItemInHand(hand));
        
        ItemStack stack = getActiveBinder(player);
        
        if(player.getItemInHand(hand) == stack)
        {
            player.openMenu(this);
            return ActionResult.success(stack);
        }
        
        return super.use(world, player, hand);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player)
    {
        ItemStack s = getActiveBinder(player);
        return new CardBinderContainer(YdmContainerTypes.CARD_BINDER, id, playerInv, getInventoryManager(s), s);
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".card_binder");
    }
    
    public ItemStack getActiveBinder(PlayerEntity player)
    {
        if(player.getMainHandItem().getItem() == this)
        {
            return player.getMainHandItem();
        }
        else if(player.getOffhandItem().getItem() == this)
        {
            return player.getOffhandItem();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }
    
    public UUID getUUID(ItemStack itemStack)
    {
        UUID uuid;
        
        UUIDHolder holder = itemStack.getCapability(YDM.UUID_HOLDER).orElse(UUIDHolder.NULL_HOLDER);
        
        if(itemStack.getOrCreateTag().contains(CardBinderItem.MANAGER_UUID_KEY_OLD))
        {
            uuid = itemStack.getOrCreateTag().getUUID(CardBinderItem.MANAGER_UUID_KEY_OLD);
            holder.setUUID(uuid);
            itemStack.getOrCreateTag().remove(MANAGER_UUID_KEY_OLD);
        }
        else
        {
            uuid = holder.getUUID();
        }
        
        return uuid;
    }
    
    public void setUUID(ItemStack itemStack, UUID uuid)
    {
        itemStack.getCapability(YDM.UUID_HOLDER).ifPresent(holder -> holder.setUUID(uuid));
    }
    
    public void setUUIDAndUpdateManager(ItemStack itemStack, UUID uuid)
    {
        CardBinderCardsManager manager = getInventoryManager(itemStack);
        itemStack.getCapability(YDM.UUID_HOLDER).ifPresent(holder -> holder.setUUID(uuid));
        MANAGER_MAP.remove(manager.getUUID());
        manager.setUUID(uuid);
        MANAGER_MAP.put(uuid, manager);
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
        
        CompoundNBT finalNbt = nbt;
        
        stack.getCapability(YDM.UUID_HOLDER).ifPresent(holder -> finalNbt.put(MANAGER_UUID_KEY, holder.serializeNBT()));
        return finalNbt;
    }
    
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null && nbt.contains(MANAGER_UUID_KEY, 8))
        {
            stack.getCapability(YDM.UUID_HOLDER).ifPresent(holder -> holder.deserializeNBT((StringNBT) nbt.get(MANAGER_UUID_KEY)));
        }
    }
}
