package de.cas_ual_ty.ydm.duel.dueldisk;

import de.cas_ual_ty.ydm.duel.DuelContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class DuelEntityContainer extends DuelContainer
{
    public int entityId;
    public boolean requestUpdate;
    
    public DuelEntityContainer(MenuType<?> type, int id, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(type, id, playerInventory, extraData.readInt(), extraData.readBoolean());
        onContainerOpenedClient(playerInventory.player); // see below
    }
    
    public DuelEntityContainer(MenuType<?> type, int id, Inventory playerInventory, int entityId, boolean requestUpdate)
    {
        super(type, id, playerInventory.player, ((DuelEntity) playerInventory.player.level.getEntity(entityId)).duelManager);
        this.entityId = entityId;
        this.requestUpdate = requestUpdate;
    }
    
    @Override
    public void onContainerOpened(Player player)
    {
        if(!player.level.isClientSide)
        {
            getDuelManager().playerOpenContainer(player);
        }
    }
    
    // need to override the above method as it is called in constructor and "requestUpdate" is not set yet
    public void onContainerOpenedClient(Player player)
    {
        if(player.level.isClientSide && requestUpdate)
        {
            requestFullUpdate();
        }
    }
    
    
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player)
    {
        return player.getOffhandItem().getItem() instanceof DuelDiskItem;
    }
}
