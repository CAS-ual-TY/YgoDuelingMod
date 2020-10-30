package de.cas_ual_ty.ydm.duelmanager.action;

import java.util.List;

import de.cas_ual_ty.ydm.duelmanager.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duelmanager.playfield.DuelCard;
import net.minecraft.network.PacketBuffer;

public class PopulateAction extends SingleZoneAction
{
    public List<DuelCard> cards;
    
    public PopulateAction(ActionType actionType, byte sourceZoneId, List<DuelCard> cards)
    {
        super(actionType, sourceZoneId);
        this.cards = cards;
    }
    
    public PopulateAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), DuelMessageUtility.decodeList(buf, DuelMessageUtility::decodeDuelCard));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodeList(this.cards, buf, DuelMessageUtility::encodeDuelCard);
    }
    
    @Override
    public void doAction()
    {
        this.sourceZone.setCardsList(this.cards);
    }
    
    @Override
    public void undoAction()
    {
        // this will always be the first action, so dont do anything here
    }
    
    @Override
    public void redoAction()
    {
        // this will always be the first action, so dont do anything here
    }
}
