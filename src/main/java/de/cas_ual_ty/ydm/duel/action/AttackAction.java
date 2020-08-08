package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.PlayField;
import de.cas_ual_ty.ydm.duel.Zone;
import net.minecraft.network.PacketBuffer;

public class AttackAction extends SingleZoneAction
{
    public byte attackedZoneId;
    
    public Zone attackedZone;
    
    public AttackAction(ActionType actionType, byte sourceZoneId, byte attackedZoneId)
    {
        super(actionType, sourceZoneId);
        this.attackedZoneId = attackedZoneId;
    }
    
    public AttackAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readByte());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeByte(this.attackedZoneId);
    }
    
    @Override
    public void init(PlayField playField)
    {
        super.init(playField);
        this.attackedZone = playField.getZone(this.attackedZoneId);
    }
    
    @Override
    public void doAction()
    {
        // TODO attack action
        // from zone to zone, no need for a specific card
    }
}
