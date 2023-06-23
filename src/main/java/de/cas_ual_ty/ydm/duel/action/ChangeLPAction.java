package de.cas_ual_ty.ydm.duel.action;

import de.cas_ual_ty.ydm.duel.network.DuelMessageUtility;
import de.cas_ual_ty.ydm.duel.playfield.PlayField;
import de.cas_ual_ty.ydm.duel.playfield.ZoneOwner;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class ChangeLPAction extends Action implements IAnnouncedAction
{
    public int changeAmount;
    public ZoneOwner owner;
    
    public PlayField playField;
    public int prevLP;
    public int trueChange;
    public int newLP;
    
    public ChangeLPAction(ActionType actionType, int changeAmount, ZoneOwner owner)
    {
        super(actionType);
        this.changeAmount = changeAmount;
        this.owner = owner;
    }
    
    public ChangeLPAction(ActionType actionType, FriendlyByteBuf buf)
    {
        this(actionType, buf.readInt(), DuelMessageUtility.decodeZoneOwner(buf));
    }
    
    @Override
    public void writeToBuf(FriendlyByteBuf buf)
    {
        buf.writeInt(changeAmount);
        DuelMessageUtility.encodeZoneOwner(owner, buf);
    }
    
    @Override
    public void initServer(PlayField playField)
    {
        this.playField = playField;
        prevLP = this.playField.getLifePoints(owner);
    }
    
    @Override
    public void doAction()
    {
        newLP = playField.changeLifePoints(changeAmount, owner);
        trueChange = newLP - prevLP;
    }
    
    @Override
    public void undoAction()
    {
        playField.setLifePoints(prevLP, owner);
    }
    
    @Override
    public void redoAction()
    {
        playField.setLifePoints(newLP, owner);
    }
    
    @Override
    public String getAnnouncementLocalKey()
    {
        return actionType.getLocalKey();
    }
    
    @Override
    public MutableComponent getAnnouncement(Component playerName)
    {
        MutableComponent t = Component.literal(String.valueOf(trueChange));
        
        if(trueChange > 0)
        {
            t = Component.literal("+").append(t);
        }
        
        return Component.translatable(getAnnouncementLocalKey()).append(": ").append(t);
    }
}
