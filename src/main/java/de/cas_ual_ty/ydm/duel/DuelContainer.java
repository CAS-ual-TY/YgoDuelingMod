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
        onContainerOpened(player);
    }
    
    @Override
    public abstract boolean stillValid(PlayerEntity player);
    
    public DuelManager getDuelManager()
    {
        return duelManager;
    }
    
    public void onContainerOpened(PlayerEntity player)
    {
        if(player.level.isClientSide)
        {
            requestFullUpdate();
        }
        else
        {
            getDuelManager().playerOpenContainer(player);
        }
    }
    
    // make sure to only call this on client
    public void requestFullUpdate()
    {
        getDuelManager().reset();
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestFullUpdate(getDuelManager().getHeader()));
    }
    
    @Override
    public void removed(PlayerEntity player)
    {
        getDuelManager().playerCloseContainer(player);
        super.removed(player);
    }
}
