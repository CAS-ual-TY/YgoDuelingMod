package de.cas_ual_ty.ydm.duel;

import java.util.List;

import de.cas_ual_ty.ydm.deckbox.DeckHolder;
import de.cas_ual_ty.ydm.duelmanager.DeckSource;
import de.cas_ual_ty.ydm.duelmanager.DuelManager;
import de.cas_ual_ty.ydm.duelmanager.DuelState;
import de.cas_ual_ty.ydm.duelmanager.PlayerRole;
import de.cas_ual_ty.ydm.duelmanager.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class DuelContainer extends Container
{
    public BlockPos pos;
    public DuelTileEntity te;
    
    public DuelContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        this(type, id, playerInventory, extraData.readBlockPos());
    }
    
    public void updateDuelState(DuelState duelState)
    {
        this.getDuelManager().setDuelStateAndUpdate(duelState);
    }
    
    public DuelContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, BlockPos blockPos)
    {
        super(type, id);
        this.pos = blockPos;
        this.te = (DuelTileEntity)playerInventory.player.world.getTileEntity(this.pos);
        this.onContainerOpened(playerInventory.player);
    }
    
    @Override
    public boolean canInteractWith(PlayerEntity player) // from LockableLootTileEntity::isUsableByPlayer
    {
        /*if (player.world.getTileEntity(this.pos) != )
        {
            return false;
        }
        else
        {*/
        return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
        //        }
    }
    
    public DuelManager getDuelManager()
    {
        return this.te.duelManager;
    }
    
    public void handleAction(PlayerRole source, Action action)
    {
        action.init(this.getDuelManager().getPlayField());
        action.doAction();
    }
    
    public void receiveDeckSources(List<DeckSource> deckSources)
    {
        
    }
    
    public void receiveDeck(int index, DeckHolder deck)
    {
        
    }
    
    public void deckAccepted(PlayerRole role)
    {
        
    }
    
    public void onContainerOpened(PlayerEntity player)
    {
        this.getDuelManager().onPlayerOpenContainer(player);
    }
    
    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        this.getDuelManager().onPlayerCloseContainer(player);
        super.onContainerClosed(player);
    }
}
