package de.cas_ual_ty.ydm.duel.dueldisk;

import de.cas_ual_ty.ydm.duel.DuelContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

public class DuelEntityContainer extends DuelContainer
{
    public int entityId;
    public boolean requestUpdate;
    
    public DuelEntityContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        this(type, id, playerInventory, extraData.readInt(), extraData.readBoolean());
        onContainerOpenedClient(playerInventory.player); // see below
    }
    
    public DuelEntityContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, int entityId, boolean requestUpdate)
    {
        super(type, id, playerInventory.player, ((DuelEntity) playerInventory.player.level.getEntity(entityId)).duelManager);
        this.entityId = entityId;
        this.requestUpdate = requestUpdate;
    }
    
    @Override
    public void onContainerOpened(PlayerEntity player)
    {
        if(!player.level.isClientSide)
        {
            getDuelManager().playerOpenContainer(player);
        }
    }
    
    // need to override the above method as it is called in constructor and "requestUpdate" is not set yet
    public void onContainerOpenedClient(PlayerEntity player)
    {
        if(player.level.isClientSide && requestUpdate)
        {
            requestFullUpdate();
        }
    }
    
    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return player.getOffhandItem().getItem() instanceof DuelDiskItem;
    }
}
