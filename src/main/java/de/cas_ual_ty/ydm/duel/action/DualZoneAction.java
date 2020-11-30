package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.PacketBuffer;

public abstract class DualZoneAction extends SingleCardAction
{
    public byte destinationZoneId;
    
    public Zone destinationZone;
    
    public DualZoneAction(ActionType actionType, byte zoneId, short cardIndex, byte destinationZoneId)
    {
        super(actionType, zoneId, cardIndex);
        this.destinationZoneId = destinationZoneId;
    }
    
    public DualZoneAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readShort(), buf.readByte());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeByte(this.destinationZoneId);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        this.destinationZone = playField.getZone(this.destinationZoneId);
    }
}
