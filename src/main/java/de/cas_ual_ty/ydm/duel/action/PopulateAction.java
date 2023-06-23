package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class PopulateAction extends SingleZoneAction
{
    public List<DuelCard> cards;
    
    public PopulateAction(ActionType actionType, byte sourceZoneId, List<DuelCard> cards)
    {
        super(actionType, sourceZoneId);
        this.cards = cards;
    }
    
    public PopulateAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, buf.readByte(), DuelMessageUtility.decodeList(buf, DuelMessageUtility::decodeDuelCard));
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        super.writeToBuf(buf);
        DuelMessageUtility.encodeList(cards, buf, DuelMessageUtility::encodeDuelCard);
    }
    
    @Override
    public void doAction()
    {
        sourceZone.setCardsList(cards);
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
