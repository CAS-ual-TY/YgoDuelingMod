package de.cas_ual_ty.ydm.cardbinder;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.YdmContainerTypes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CardBinderItem extends Item implements INamedContainerProvider
{
    private static final HashMap<UUID, CardBinderCardsManager> MANAGER_MAP = new HashMap<>();
    public static final String MANAGER_UUID_KEY = "binder_uuid";
    
    public CardBinderItem(Properties properties)
    {
        super(properties);
    }
    
    public CardBinderCardsManager getInventoryManager(ItemStack itemStack)
    {
        CardBinderCardsManager manager;
        UUID uuid;
        
        if(itemStack.getOrCreateTag().hasUniqueId(CardBinderItem.MANAGER_UUID_KEY))
        {
            uuid = itemStack.getTag().getUniqueId(CardBinderItem.MANAGER_UUID_KEY);
        }
        else
        {
            uuid = null;
        }
        
        if(uuid == null || !CardBinderItem.MANAGER_MAP.containsKey(uuid))
        {
            manager = new CardBinderCardsManager();
            manager.generateUUIDIfNull();
            uuid = manager.getUUID();
            itemStack.getTag().putUniqueId(CardBinderItem.MANAGER_UUID_KEY, uuid);
            CardBinderItem.MANAGER_MAP.put(uuid, manager);
        }
        else
        {
            manager = CardBinderItem.MANAGER_MAP.get(uuid);
        }
        
        return manager;
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        
        tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".uuid"));
        
        if(stack.getOrCreateTag().hasUniqueId(CardBinderItem.MANAGER_UUID_KEY))
        {
            tooltip.add(new StringTextComponent(stack.getTag().getUniqueId(CardBinderItem.MANAGER_UUID_KEY).toString()));
        }
        else
        {
            tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".uuid.empty"));
        }
        
        /*// used this when capability was still used
        LazyOptional<CardBinderCardsManager> l = stack.getCapability(YDM.BINDER_INVENTORY_CAPABILITY);
        
        if(l.isPresent())
        {
            CardBinderCardsManager m = l.orElse(null);
            
            if(ClientProxy.showBinderId)
            {
                tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".uuid"));
                
                if(m.getUUID() != null)
                {
                    tooltip.add(new StringTextComponent(m.getUUID().toString()));
                }
                else
                {
                    tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".uuid.empty"));
                }
            }
        }
        else
        {
            tooltip.add(new StringTextComponent("Error! No capability present! Pls report to mod author!"));
        }
        */
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = this.getActiveBinder(player);
        
        if(player.getHeldItem(hand) == stack)
        {
            player.openContainer(this);
            return ActionResult.resultSuccess(stack);
        }
        
        return super.onItemRightClick(world, player, hand);
    }
    
    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player)
    {
        ItemStack s = this.getActiveBinder(player);
        return new CardBinderContainer(YdmContainerTypes.CARD_BINDER, id, playerInv, this.getInventoryManager(s), s);
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("container." + YDM.MOD_ID + ".card_binder");
    }
    
    public ItemStack getActiveBinder(PlayerEntity player)
    {
        if(player.getHeldItemMainhand().getItem() == this)
        {
            return player.getHeldItemMainhand();
        }
        else if(player.getHeldItemOffhand().getItem() == this)
        {
            return player.getHeldItemOffhand();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }
}
