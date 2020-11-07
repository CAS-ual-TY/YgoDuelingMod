package de.cas_ual_ty.ydm.duel;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.duel.network.DuelMessages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class DuelContainer extends Container
{
    protected DuelManager duelManager;
    
    public DuelContainer(ContainerType<?> type, int id, PlayerEntity player, DuelManager duelManager)
    {
        super(type, id);
        this.duelManager = duelManager;
        this.onContainerOpened(player);
    }
    
    @Override
    public abstract boolean canInteractWith(PlayerEntity player);
    
    public DuelManager getDuelManager()
    {
        return this.duelManager;
    }
    
    public void onContainerOpened(PlayerEntity player)
    {
        if(player.world.isRemote)
        {
            YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestFullUpdate(this.getDuelManager().getHeader()));
        }
        else
        {
            this.getDuelManager().playerOpenContainer(player);
        }
    }
    
    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        this.getDuelManager().playerCloseContainer(player);
        super.onContainerClosed(player);
    }
}
