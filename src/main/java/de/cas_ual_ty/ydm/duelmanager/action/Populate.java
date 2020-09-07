package de.cas_ual_ty.ydm.duelmanager.action;

import java.util.List;

import de.cas_ual_ty.ydm.duelmanager.DuelCard;
import de.cas_ual_ty.ydm.duelmanager.DuelMessages;
import net.minecraft.network.PacketBuffer;

public class Populate extends SingleZoneAction
{
    public List<DuelCard> cards;
    
    public Populate(ActionType actionType, byte sourceZoneId, List<DuelCard> cards)
    {
        super(actionType, sourceZoneId);
        this.cards = cards;
    }
    
    public Populate(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), DuelMessages.decodeList(buf, DuelMessages::decodeDuelCard));
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        DuelMessages.encodeList(this.cards, buf, DuelMessages::encodeDuelCard);
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
