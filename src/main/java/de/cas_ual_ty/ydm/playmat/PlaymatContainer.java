package de.cas_ual_ty.ydm.playmat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class PlaymatContainer extends Container
{
    public PlaymatContainer(ContainerType<?> type, int id, PlayerInventory playerInventory)
    {
        super(type, id);
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }
}
