package de.cas_ual_ty.ydm.duel;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class SimpleDuelContainer extends Container
{
    public SimpleDuelContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        super(type, id);
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
}
