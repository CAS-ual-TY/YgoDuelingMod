package de.cas_ual_ty.ydm.cardbinder;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CardBinderItem extends Item implements MenuProvider
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
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        
        tooltip.add(Component.translatable(getDescriptionId() + ".uuid"));
        
        UUID uuid = getUUID(stack);
        
        if(uuid != null)
        {
            tooltip.add(Component.literal(uuid.toString()));
        }
        else
        {
            tooltip.add(Component.translatable(getDescriptionId() + ".uuid.empty"));
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        // must also fix UUID on client side if player is in creative mode
        getUUID(player.getItemInHand(hand));
        
        ItemStack stack = getActiveBinder(player);
        
        if(player.getItemInHand(hand) == stack)
        {
            player.openMenu(this);
            return InteractionResultHolder.success(stack);
        }
        
        return super.use(world, player, hand);
    }
    
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player)
    {
        ItemStack s = getActiveBinder(player);
        return new CardBinderContainer(YdmContainerTypes.CARD_BINDER.get(), id, playerInv, getInventoryManager(s), s);
    }
    
    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container." + YDM.MOD_ID + ".card_binder");
    }
    
    public ItemStack getActiveBinder(Player player)
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
    
    @Override
    public boolean shouldOverrideMultiplayerNbt()
    {
        return true;
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
        
        CompoundTag finalNbt = nbt;
        
        stack.getCapability(YDM.UUID_HOLDER).ifPresent(holder -> finalNbt.put(MANAGER_UUID_KEY, holder.serializeNBT()));
        return finalNbt;
    }
    
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt)
    {
        super.readShareTag(stack, nbt);
        
        if(nbt != null && nbt.contains(MANAGER_UUID_KEY, 8))
        {
            stack.getCapability(YDM.UUID_HOLDER).ifPresent(holder -> holder.deserializeNBT((StringTag) nbt.get(MANAGER_UUID_KEY)));
        }
    }
}
