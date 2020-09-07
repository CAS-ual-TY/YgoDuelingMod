package de.cas_ual_ty.ydm.duelmanager.action;

import java.util.List;

import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import net.minecraft.network.PacketBuffer;

public class ShuffleAction extends SingleZoneAction
{
    protected List<DuelCard> before;
    protected List<DuelCard> after;
    
    public ShuffleAction(ActionType actionType, byte sourceZoneId)
    {
        super(actionType, sourceZoneId);
    }
    
    public ShuffleAction(ActionType actionType, PacketBuffer buf)
    {
        super(actionType, buf);
    }
    
    @Override
    public void doAction()
    {
        this.before = this.sourceZone.getCardsList();
        this.sourceZone.shuffle();
        this.after = this.sourceZone.getCardsList();
    }
    
    @Override
    public void undoAction()
    {
        this.sourceZone.setCardsList(this.before);
    }
    
    @Override
    public void redoAction()
    {
        this.sourceZone.setCardsList(this.after);
    }
}
