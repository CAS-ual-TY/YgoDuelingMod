package de.cas_ual_ty.ydm.duel.action;

import javax.annotation.Nullable;

import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.PacketBuffer;

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
        this(actionType, sourceZone == null ? (byte)-1 : sourceZone.index, (sourceZone == null || card == null) ? (short)-1 : sourceZone.getCardIndexShort(card), owner.getIndex());
    }
    
    public SelectAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte());
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        this.owner = ZoneOwner.getFromIndex(this.ownerIndex);
        this.action = () -> playField.setClickedForPlayer(this.owner, this.sourceZone, this.card);
    }
    
    @Override
    public void doAction()
    {
        this.action.run();
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeByte(this.ownerIndex);
    }
}
