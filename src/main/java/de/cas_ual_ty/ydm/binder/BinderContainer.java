package de.cas_ual_ty.ydm.binder;

import de.cas_ual_ty.ydm.YdmItems;
import de.cas_ual_ty.ydm.cardinventory.JsonCardInventoryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class BinderContainer extends Container
{
    protected final JsonCardInventoryManager manager;
    
    public BinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        this(type, id, playerInventory, null, YdmItems.CARD_BINDER.getActiveBinder(playerInventory.player));
    }
    
    public BinderContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, JsonCardInventoryManager manager, ItemStack itemStack)
    {
        super(type, id);
        this.manager = manager;
        
        Slot s;
        
        // player inventory
        for(int y = 0; y < 3; ++y)
        {
            for(int x = 0; x < 9; ++x)
            {
                s = new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 139 + y * 18);
                
                if(s.getStack() == itemStack)
                {
                    s = new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 139 + y * 18)
                    {
                        @Override
                        public boolean canTakeStack(PlayerEntity playerIn)
                        {
                            return false;
                        }
                    };
                }
                
                this.addSlot(s);
            }
        }
        
        // player hot bar
        for(int x = 0; x < 9; ++x)
        {
            s = new Slot(playerInventory, x, 8 + x * 18, 197);
            
            if(s.getStack() == itemStack)
            {
                s = new Slot(playerInventory, x, 8 + x * 18, 197)
                {
                    @Override
                    public boolean canTakeStack(PlayerEntity playerIn)
                    {
                        return false;
                    }
                };
            }
            
            this.addSlot(s);
        }
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
}
