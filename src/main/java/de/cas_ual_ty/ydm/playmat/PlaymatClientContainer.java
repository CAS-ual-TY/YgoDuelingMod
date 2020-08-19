package de.cas_ual_ty.ydm.playmat;

import java.util.List;

import de.cas_ual_ty.ydm.YDM;
import de.cas_ual_ty.ydm.deckbox.DeckProvider;
import de.cas_ual_ty.ydm.duel.DuelMessages;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.PacketDistributor;

public class PlaymatClientContainer extends PlaymatContainer
{
    public PlaymatClientContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(type, id, playerInventory, extraData);
    }
    
    @Override
    public void handleAction(PlayerRole source, Action action)
    {
        // TODO
        super.handleAction(source, action);
    }
    
    @Override
    public void receiveDeckProviders(List<DeckProvider> deckProviders)
    {
        // TODO
        super.receiveDeckProviders(deckProviders);
    }
    
    @Override
    public void onContainerOpened(PlayerEntity player)
    {
        super.onContainerOpened(player);
        
        // request full update
        YDM.channel.send(PacketDistributor.SERVER.noArg(), new DuelMessages.RequestFullUpdate());
    }
    
    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);
        
        // no need to keep any data when closing on client
        this.getDuelManager().reset();
    }
}
