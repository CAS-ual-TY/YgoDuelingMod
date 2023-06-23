package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.FriendlyByteBuf;

public class ShowCardAction extends SingleCardAction implements IAnnouncedAction
{
    public ShowCardAction(ActionType actionType, byte sourceZoneId, short sourceCardIndex)
    {
        super(actionType, sourceZoneId, sourceCardIndex);
    }
    
    public ShowCardAction(ActionType actionType, Zone sourceZone, DuelCard sourceCard)
    {
        super(actionType, sourceZone.index, sourceZone.getCardIndexShort(sourceCard));
    }
    
    public ShowCardAction(ActionType actionType, FriendlyByteBuf buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public void doAction()
    {
    }
    
    @Override
    public String getAnnouncementLocalKey()
    {
        return actionType.getLocalKey();
    }
    
    @Override
    public Zone getFieldAnnouncementZone()
    {
        return sourceZone;
    }
}
