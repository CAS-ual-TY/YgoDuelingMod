package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;

public class SelectAction extends SingleCardAction
{
    public byte ownerIndex;
    
    public ZoneOwner owner;
    
    public Runnable action;
    
    public SelectAction(ActionType actionType, byte sourceZoneId, short cardId, byte ownerIndex)
    {
        super(actionType, sourceZoneId, cardId);
        this.ownerIndex = ownerIndex;
    }
    
    public SelectAction(ActionType actionType, @Nullable Zone sourceZone, @Nullable DuelCard card, ZoneOwner owner)
    {
        this(actionType, sourceZone == null ? (byte) -1 : sourceZone.index, (sourceZone == null || card == null) ? (short) -1 : sourceZone.getCardIndexShort(card), owner.getIndex());
    }
    
    public SelectAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte());
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        owner = ZoneOwner.getFromIndex(ownerIndex);
        action = () -> playField.setClickedForPlayer(owner, sourceZone, card);
    }
    
    @Override
    public void doAction()
    {
        action.run();
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        super.writeToBuf(buf);
        buf.writeByte(ownerIndex);
    }
}
