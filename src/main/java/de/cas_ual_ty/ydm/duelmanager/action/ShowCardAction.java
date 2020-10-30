package de.cas_ual_ty.ydm.duelmanager.action;

import de.cas_ual_ty.ydm.duelmanager.playfield.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public class ShowCardAction extends SingleCardAction
{
    public ShowCardAction(ActionType actionType, byte sourceZoneId, short sourceCardIndex)
    {
        super(actionType, sourceZoneId, sourceCardIndex);
    }
    
    public ShowCardAction(ActionType actionType, Zone sourceZone, DuelCard sourceCard)
    {
        super(actionType, sourceZone.index, sourceZone.getCardIndexShort(sourceCard));
    }
    
    public ShowCardAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public void doAction()
    {
        // TODO show card action
    }
}
