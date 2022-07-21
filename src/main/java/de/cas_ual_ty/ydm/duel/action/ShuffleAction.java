package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.playfield.DuelCard;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.Zone;
import net.minecraft.network.PacketBuffer;

import java.util.List;
import java.util.Random;

public class ShuffleAction extends SingleZoneAction implements IAnnouncedAction
{
    protected List<DuelCard> before;
    protected List<DuelCard> after;
    protected long randomSeed;
    
    protected ShuffleAction(ActionType actionType, byte sourceZoneId, long randomSeed)
    {
        super(actionType, sourceZoneId);
        this.randomSeed = randomSeed;
    }
    
    public ShuffleAction(ActionType actionType, byte sourceZoneId)
    {
        this(actionType, sourceZoneId, -1);
    }
    
    public ShuffleAction(ActionType actionType, Zone sourceZone)
    {
        this(actionType, sourceZone.index);
    }
    
    public ShuffleAction(ActionType actionType, PacketBuffer buf)
    {
        this(actionType, buf.readByte(), buf.readLong());
    }
    
    @Override
    public void writeToBuf(PacketBuffer buf)
    {
        super.writeToBuf(buf);
        buf.writeLong(randomSeed);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        super.initServer(playField);
        randomSeed = playField.getDuelManager().getRandom().nextLong();
    }
    
    @Override
    public void initClient(PlayField playField)
    {
        super.initServer(playField);
    }
    
    @Override
    public void doAction()
    {
        before = sourceZone.getCardsList();
        sourceZone.shuffle(new Random(randomSeed));
        after = sourceZone.getCardsList();
    }
    
    @Override
    public void undoAction()
    {
        sourceZone.setCardsList(before);
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
