package de.cas_ual_ty.ydm.playmat;

import java.util.List;

import de.cas_ual_ty.ydm.deckbox.DeckProvider;
import de.cas_ual_ty.ydm.duel.PlayerRole;
import de.cas_ual_ty.ydm.duel.action.Action;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

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
    
    public void receiveDeckProviders(List<DeckProvider> deckProviders)
    {
        // TODO
    }
}
